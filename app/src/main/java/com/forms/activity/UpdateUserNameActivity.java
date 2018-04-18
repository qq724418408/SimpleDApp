package com.forms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.TypeReference;
import com.forms.xxxapp.R;
import com.forms.base.BaseActivity;
import com.forms.bean.UserBean;
import com.forms.config.DHttpRequest;
import com.forms.dhttp.dbean.ContentResponse;
import com.forms.dhttp.http.excutor.DHttpCallBack;
import com.forms.dhttp.http.excutor.listener.Progress;
import com.forms.dhttp.widget.LoadingDialog;
import com.forms.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserNameActivity extends BaseActivity {

    private UserBean user;
    private ImageView ivBack;
    private EditText etUserName;
    private Button btnSave;
    private Progress dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);
        Bundle bundle = getIntent().getExtras();
        user = (UserBean) bundle.getSerializable("user");
        if (null != user) {
            etUserName.setText(user.getUserName());
            etUserName.setSelection(user.getUserName().length());
        }
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void initData() {
        dialog = new LoadingDialog(this);
    }

    @Override
    public void initView() {
        ivBack = findView(R.id.ivBack);
        etUserName = findView(R.id.etUserName);
        btnSave = findView(R.id.btnSave);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnSave:
                String userName = etUserName.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    ToastUtil.show(UpdateUserNameActivity.this, etUserName.getHint().toString());
                    return;
                }
                if (userName.equals(user.getUserName())) {
                    ToastUtil.show(UpdateUserNameActivity.this, "未做任何修改");
                    return;
                }
                updateUserName(userName);
                break;
            default:
                break;
        }
    }

    private void updateUserName(String userName) {
        Map<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("userId", user.getUserId());
        DHttpRequest.updateUserName(params, dialog, new DHttpCallBack<UserBean>(new TypeReference<ContentResponse<UserBean>>() {
        }) {
            @Override
            public void onSuccess(UserBean response) {
                super.onSuccess(response);
                ToastUtil.show(UpdateUserNameActivity.this, "修改成功");
                Intent data = new Intent();
                data.putExtra("userName", response.getUserName());
                setResult(10002, data);
                finish();
            }

            @Override
            public void onEmpty(String response) {
                super.onEmpty(response);
                ToastUtil.show(UpdateUserNameActivity.this, response);
            }

            @Override
            public void onError(String request, String exception) {
                super.onError(request, exception);
                ToastUtil.show(UpdateUserNameActivity.this, exception);
            }

            @Override
            public void otherCode(String code, String msg, UserBean content) {
                super.otherCode(code, msg, content);
                ToastUtil.show(UpdateUserNameActivity.this, msg);
            }

            @Override
            public void onDynamicError(String onDynamicError) {
                super.onDynamicError(onDynamicError);
                ToastUtil.show(UpdateUserNameActivity.this, onDynamicError);
            }
        });
    }
}
