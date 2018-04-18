package com.forms.config;

import android.app.Application;
import android.os.Environment;

import com.forms.bean.UserBean;
import com.forms.bean.UsersBean;
import com.forms.dhttp.dbean.EntryBean;
import com.forms.dhttp.http.excutor.DHttpCallBack;
import com.forms.dhttp.http.excutor.DHttpExcutorList;
import com.forms.dhttp.http.excutor.listener.Progress;
import com.forms.dhttp.utils.AppInfoUtils;
import com.forms.okhttplibrary.builder.PostMultipartBuilder;
import com.forms.okhttplibrary.util.HttpUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求涉及动态url
 *
 * @author xiaxq
 */
public class DHttpRequest {

    public static void initHttp(Application context, String token) {
        HttpUtil.getInstance().autoManagerCookies().buildOkHttpClient();
        DHttpExcutorList.Builder.buildConfigServer(context,
                UrlConfig.BASE_SERVER,//服务器
                UrlConfig.SERVER_ROOT_PATH)//根目录或其他
                .addParam("deviceType", "1")// 添加需要的参数
                .addParam("deviceId", AppInfoUtils.getDeviceId(context))
                .addParam("systemVersion", AppInfoUtils.getSystemVersion())
                .addParam("appVersion", AppInfoUtils.getVersion(context))
                .addParam("deviceToken", token == null ? "" : token)
                .exceptionCode("61001")//token 过期  添加异常返回码，出现对应的异常返回码后，下次请求会刷新动态流程
                .exceptionCode("61002")//单设备登录
                .autoLogOut(UrlConfig.autoLogOut)//自动登出（发送登出接口，该接口不受任何限制）
                .logState(true)// log 开关
                .loginTokenId(UrlConfig.loginTokenId)// token 自动登录
                .loginState(false)//初始化设置登录状态为false
                .otherCode(UrlConfig.otherCode);//其他返回码 不会刷新动态流程，单单显示失败页面
    }

    /**
     * 登录
     */
    public static void login(Map<String, String> params, Progress dialog, DHttpCallBack<UserBean> callBack) {
        DHttpExcutorList.getInstance().sendPost(UrlConfig.login, params, dialog, false, callBack);
    }

    public static void register(Map<String, String> params, HashMap<String, File> fileParam, Progress dialog, DHttpCallBack<UserBean> callBack) {
        DHttpExcutorList.getInstance().sendPost(UrlConfig.register, params, fileParam, dialog, false, callBack);
    }

    public static void test(Map<String, String> params, HashMap<String, File> fileParam, Progress dialog, DHttpCallBack<UserBean> callBack) {
        DHttpExcutorList.getInstance().sendPost(UrlConfig.test, params, fileParam, dialog, false, callBack);
    }

    public static void getUserList(String pageNum, Progress dialog, DHttpCallBack<UsersBean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum);
        DHttpExcutorList.getInstance().sendPost(UrlConfig.users, params, dialog, false, callBack);
    }

    public static void getUserInfo(Map<String, String> params, Progress dialog, DHttpCallBack<UserBean> callBack) {
        DHttpExcutorList.getInstance().sendPost(UrlConfig.getUserInfo, params, dialog, false, callBack);
    }

    public static void updateUser(Map<String, String> params, Progress dialog, DHttpCallBack<UserBean> callBack) {
        DHttpExcutorList.getInstance().sendPost(UrlConfig.updateUser, params, dialog, false, callBack);
    }

    public static void deleteUser(String userId, Progress dialog, DHttpCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        DHttpExcutorList.getInstance().sendPost(UrlConfig.deleteUser, params, dialog, false, callBack);
    }

    public static void updateProfilePhoto(Map<String, String> params, HashMap<String, File> fileParam, Progress dialog, DHttpCallBack<UserBean> callBack) {
        DHttpExcutorList.getInstance().sendPost(UrlConfig.updateProfilePhoto, params, fileParam, dialog, false, callBack);
    }

    public static void updateUserName(Map<String, String> params, Progress dialog, DHttpCallBack<UserBean> callBack) {
        DHttpExcutorList.getInstance().sendPost(UrlConfig.updateUserName, params, dialog, false, callBack);
    }

    public static void uploadFiles(Progress progress, DHttpCallBack<String> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("paramKey", "value");
        HashMap<String, File> fileParam = new HashMap<>();
//        fileParam.put("fileKey4", new File(""));
//        fileParam.put("fileKey3", new File(""));
//        fileParam.put("fileKey2", new File(""));
//        fileParam.put("fileKey1", new File(""));
        DHttpExcutorList.getInstance().sendPost(UrlConfig.uploadFiles, params, fileParam, progress, false, callBack);
    }

    public static void uploadFilesType(Progress progress, DHttpCallBack<String> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("paramKey", "value");
        List<PostMultipartBuilder.FileObject> filesParam = new ArrayList<>();
//        filesParam.add(new PostMultipartBuilder.FileObject("fileKey1", "image/jpeg", new File("")));
//        filesParam.add(new PostMultipartBuilder.FileObject("fileKey2", "image/png", new File("")));
//        filesParam.add(new PostMultipartBuilder.FileObject("fileKey2", "image/tiff", new File("")));
        DHttpExcutorList.getInstance().sendPost(UrlConfig.uploadFilesType, params, filesParam, progress, false, callBack);
    }

    //下载文件
    public static void downloadFile(String path, DHttpCallBack callBack) {
        String cachePath;
        // 设置视频缓存路径
        String downName = "Spreadit" + System.currentTimeMillis() + path.substring(path.lastIndexOf("."));
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String typeof = "/Spreadit/";//+type+"/"
        cachePath = dcim + typeof;
        File file = new File(cachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        DHttpExcutorList.getInstance().downloadFile(path, cachePath, downName, null, callBack);
    }

    public static void getEntryFuncList(Progress progress, DHttpCallBack<EntryBean> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("enterType", "1");
        params.put("loginStatus", "0");
        DHttpExcutorList.getInstance().sendPost(UrlConfig.getEntryFuncList, params, progress, false, callBack);
    }
}

