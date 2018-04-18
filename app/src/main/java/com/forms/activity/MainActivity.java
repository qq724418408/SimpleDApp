package com.forms.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.forms.xxxapp.R;
import com.forms.base.BaseActivity;
import com.forms.bean.UserBean;
import com.forms.config.DHttpRequest;
import com.forms.config.UrlConfig;
import com.forms.dhttp.dbean.ContentResponse;
import com.forms.dhttp.http.excutor.DHttpCallBack;
import com.forms.dhttp.http.excutor.listener.Progress;
import com.forms.dhttp.utils.LogUtil;
import com.forms.dhttp.widget.LoadingDialog;
import com.forms.utils.BitmapUtils;
import com.forms.utils.FileUtils;
import com.forms.utils.GlideCircleTransform;
import com.forms.utils.ToastUtil;
import com.forms.view.dialog.DialogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static final int PICKTURE_CODE = 0x0001; // 相册
    private static final int CAMERA_CODE = 0x0002;// 相机
    private static final int ZOOM_CODE = 0x0003;// 裁剪
    private Dialog mScDialog;// 选择照片弹窗
    private File photoFile; // 照片
    private Uri temUri;
    private String path;
    private ImageView ivHeadPortrait;
    private TextView tvUserName;
    private Button btnLogout;
    private View lltUserName;
    private View lltUsers;
    private UserBean user;
    private Progress dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new LoadingDialog(this);
        Bundle bundle = getIntent().getExtras();
        user = (UserBean) bundle.getSerializable("user");
        getUserInfo(user);
        setUserId(user.getUserId());
    }

    @Override
    protected void setListener() {
        ivHeadPortrait.setOnClickListener(this);
        lltUserName.setOnClickListener(this);
        lltUsers.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mScDialog = DialogUtil.pictureDialog(this, this);
        path = Environment.getExternalStorageDirectory() + File.separator + "image" + File.separator + "temp";
    }

    @Override
    public void initView() {
        ivHeadPortrait = findView(R.id.ivHeadPortrait);
        tvUserName = findView(R.id.tvUserName);
        lltUserName = findView(R.id.lltUserName);
        lltUsers = findView(R.id.lltUsers);
        btnLogout = findView(R.id.btnLogout);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.ivHeadPortrait:
                mScDialog.show();
                break;
            case R.id.tvPicture:
                temUri = getTempUri();
                startPicture();
                mScDialog.dismiss();
                break;
            case R.id.tvCamera:
                temUri = getTempUri();
                startCamera();
                mScDialog.dismiss();
                break;
            case R.id.tvCancel:
                mScDialog.dismiss();
                break;
            case R.id.lltUserName:
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                startActivityForResult(UpdateUserNameActivity.class, bundle, 10001);
                break;
            case R.id.lltUsers:
                Bundle b = new Bundle();
                b.putSerializable("user", user);
                startActivity(UserListActivity.class, b);
                break;
            case R.id.btnLogout:
                startActivity(LoginActivity.class);
                finish();
                break;
            default:
                break;
        }
    }

    private void getUserInfo(UserBean user) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getUserId());
        DHttpRequest.getUserInfo(params, dialog, new DHttpCallBack<UserBean>(new TypeReference<ContentResponse<UserBean>>() {
        }) {
            @Override
            public void onSuccess(UserBean response) {
                super.onSuccess(response);
                LogUtil.e(response.toString());
                updateUserInfo(response);
            }

            @Override
            public void onEmpty(String response) {
                super.onEmpty(response);
                LogUtil.e(response);
                ToastUtil.show(MainActivity.this, response);
            }

            @Override
            public void onError(String request, String exception) {
                super.onError(request, exception);
                LogUtil.e(exception);
                ToastUtil.show(MainActivity.this, exception);
            }

            @Override
            public void otherCode(String code, String msg, UserBean content) {
                super.otherCode(code, msg, content);
                LogUtil.e(msg);
                ToastUtil.show(MainActivity.this, msg);
            }

            @Override
            public void onDynamicError(String onDynamicError) {
                super.onDynamicError(onDynamicError);
                LogUtil.e(onDynamicError);
                ToastUtil.show(MainActivity.this, onDynamicError);
            }
        });

    }

    private void updateUserInfo(UserBean response) {
        tvUserName.setText(TextUtils.isEmpty(response.getUserName()) ? "未设置" :response.getUserName());
        String photo = response.getProfilePhoto();
        if (!TextUtils.isEmpty(photo)) {
            Glide.with(MainActivity.this)
                    .load(UrlConfig.BASE_SERVER + photo)
                    .error(R.mipmap.icon_mine_head_portrait)
                    .placeholder(R.mipmap.icon_mine_head_portrait)
                    .transform(new GlideCircleTransform(MainActivity.this))
                    .into(ivHeadPortrait);
        }
    }

    /**
     * 结果回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            onResult(requestCode, data);
        } else if (resultCode == 10002 && requestCode == 10001) {
            String userName = data.getStringExtra("userName");
            tvUserName.setText(userName);
            user.setUserName(userName);
        }
    }

    /**
     * 成功结果处理
     *
     * @param requestCode
     * @param data
     */
    private void onResult(int requestCode, Intent data) {
        Uri uri = null;
        try {
            uri = data.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (requestCode) {
            case CAMERA_CODE:
                // 获取图片的选转角度
                int degree = BitmapUtils.readPictureDegree(path);
                // 获取旋转后的图片
                Bitmap bitmap = BitmapUtils.rotaingImage(degree, path);
                // 图片转文件
                File f = BitmapUtils.bitmap2File(bitmap, path);
                try {
                    uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), f.getAbsolutePath(), null, null));
                    startPhotoZoom(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case PICKTURE_CODE:
                if (null != data) {// 为了取消选取不报空指针用
                    startPhotoZoom(uri);
                }
                break;
            case ZOOM_CODE:
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(temUri));
                    onBitmapCallResult(bmp, temUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }

    /**
     * 最终图片结果
     *
     * @param bitmap
     */
    private void onBitmapCallResult(Bitmap bitmap, Uri uri) {
        photoFile = BitmapUtils.bitmap2File(bitmap, uri.getPath());
        updateProfilePhoto(photoFile, uri);
    }

    private void updateProfilePhoto(File photoFile, final Uri uri) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getUserId());
        HashMap<String, File> fileParam = new HashMap<>();
        fileParam.put("file", photoFile);
        DHttpRequest.updateProfilePhoto(params, fileParam, dialog, new DHttpCallBack<UserBean>(new TypeReference<ContentResponse<UserBean>>() {
        }) {
            @Override
            public void onSuccess(UserBean response) {
                super.onSuccess(response);
                LogUtil.e(response.toString());
                ToastUtil.show(MainActivity.this, "头像更新成功");
                //ivHeadPortrait.setImageURI(uri);
                Glide.with(MainActivity.this)
                        .load(uri)
                        .error(R.mipmap.icon_mine_head_portrait)
                        .placeholder(R.mipmap.icon_mine_head_portrait)
                        .transform(new GlideCircleTransform(MainActivity.this))
                        .into(ivHeadPortrait);
                //updateUserInfo(response);
            }

            @Override
            public void onEmpty(String response) {
                super.onEmpty(response);
                LogUtil.e(response);
                ToastUtil.show(MainActivity.this, response);
            }

            @Override
            public void onError(String request, String exception) {
                super.onError(request, exception);
                LogUtil.e(exception);
                ToastUtil.show(MainActivity.this, exception);
            }

            @Override
            public void otherCode(String code, String msg, UserBean content) {
                super.otherCode(code, msg, content);
                LogUtil.e(msg);
                ToastUtil.show(MainActivity.this, msg);
            }

            @Override
            public void onDynamicError(String onDynamicError) {
                super.onDynamicError(onDynamicError);
                LogUtil.e(onDynamicError);
                ToastUtil.show(MainActivity.this, onDynamicError);
            }
        });
    }

    /**
     * 跳转相册
     */
    private void startPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PICKTURE_CODE);
    }

    /**
     * 跳裁剪
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);// 输出是X方向的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, temUri);
        intent.putExtra("return-data", false);// 设置为不返回数据
        startActivityForResult(intent, ZOOM_CODE);
    }

    /**
     * 跳相机
     */
    private void startCamera() {
        // 判断sdcard是否存在
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                // qwe为相片保存的文件夹名
                File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "image");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                // asd为保存的相片名
                File f = new File(dir, "temp");// localTempImgDir和localTempImageFileName是自己定义的名字
                Uri u = Uri.fromFile(f);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(intent, CAMERA_CODE);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCurrentTime() {
        Date dt = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(dt);
    }

    /**
     * 裁剪后保存的uri
     */
    private Uri getTempUri() {
        String uriPath = "file://" + File.separator + createSaveImagePath(this);
        String fileName = "/" + getCurrentTime()  + "-" + user.getUserId() + ".jpg";
        LogUtil.e(uriPath + fileName);
        String path = uriPath + fileName;
        if (path.contains("////")) {
            path.replace("////", "///");
        }
        return Uri.parse(path);
    }

    public static String createSaveImagePath(Context context) {
        // 判断sdcard是否存在
        File dir = null;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                // qwe为相片保存的文件夹名
                String eSDPath = Environment.getExternalStorageDirectory().getPath();
                String pkg = context.getPackageName();
                // qwe为相片保存的文件夹名
                dir = new File(eSDPath + File.separator + pkg + File.separator + "image");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                LogUtil.e(dir.getAbsolutePath());
                LogUtil.e(dir.getPath());
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
        return dir.getAbsolutePath();
    }

    /**
     * onDestroy的时候删除图片，不做本地保留
     */
    public static void delAllImages(Context context) {
        String eSDPath = Environment.getExternalStorageDirectory().getPath();
        String pkg = context.getPackageName();
        String imgPath1 = eSDPath + File.separator + "image";
        String imgPath2 = eSDPath + File.separator + pkg + File.separator + "image";
        File file1 = new File(imgPath1);
        File file2 = new File(imgPath2);
        FileUtils.deleteFilesByDirectory(file1);
        FileUtils.deleteFilesByDirectory(file2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delAllImages(this);
    }

    private long startTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= 2000) {
            ToastUtil.show(MainActivity.this, "再按一次退出");
            startTime = currentTime;
        } else {
            finish();
        }
    }
}
