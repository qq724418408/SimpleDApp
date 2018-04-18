package com.forms.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.forms.xxxapp.R;
import com.forms.base.BaseActivity;
import com.forms.bean.UserBean;
import com.forms.config.DHttpRequest;
import com.forms.dhttp.dbean.ContentResponse;
import com.forms.dhttp.http.excutor.DHttpCallBack;
import com.forms.dhttp.http.excutor.listener.Progress;
import com.forms.dhttp.utils.LogUtil;
import com.forms.dhttp.widget.LoadingDialog;
import com.forms.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bubbly
 */
public class LoginActivity extends BaseActivity {

    private TextView tvRegister;
    private TextView tvLogin;
    private ImageView ivQQLogin;
    private ImageView ivWXLogin;
    private EditText etUserName;
    private EditText etPwd;
    private Progress dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setSteepStatusBar(true);
        dialog = new LoadingDialog(this);
    }

    @Override
    protected void setListener() {
        tvRegister.setOnClickListener(this);
        ivQQLogin.setOnClickListener(this);
        ivWXLogin.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        tvRegister = findView(R.id.tvRegister);
        ivQQLogin = findView(R.id.ivQQLogin);
        ivWXLogin = findView(R.id.ivWXLogin);
        tvLogin = findView(R.id.btnLogin);
        etUserName = findView(R.id.etUserName);
        etPwd = findView(R.id.etPwd);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.ivQQLogin:
            case R.id.ivWXLogin:
                ToastUtil.show(LoginActivity.this, "敬请期待");
                break;
            case R.id.tvRegister:
                startActivity(RegisterActivity.class);
                break;
            case R.id.btnLogin:
                String userName = etUserName.getText().toString();
                String pwd = etPwd.getText().toString();
                login(userName, pwd);
                break;
            default:
                break;
        }
    }

    private void login(String userName, String pwd) {
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.show(LoginActivity.this, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.show(LoginActivity.this, "请输入密码");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userId", userName);
        params.put("pwd", pwd);
        DHttpRequest.login(params, dialog, new DHttpCallBack<UserBean>(new TypeReference<ContentResponse<UserBean>>() {
        }) {
            @Override
            public void onSuccess(UserBean response) {
                super.onSuccess(response);
                LogUtil.e(response.toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", response);
                startActivity(MainActivity.class, bundle);
                finish();
            }

            @Override
            public void onEmpty(String response) {
                super.onEmpty(response);
                LogUtil.e(response);
                ToastUtil.show(LoginActivity.this, response);
            }

            @Override
            public void onError(String request, String exception) {
                super.onError(request, exception);
                LogUtil.e(exception);
                ToastUtil.show(LoginActivity.this, exception);
            }

            @Override
            public void otherCode(String code, String msg, UserBean content) {
                super.otherCode(code, msg, content);
                LogUtil.e(msg);
                ToastUtil.show(LoginActivity.this, msg);
            }

            @Override
            public void onDynamicError(String onDynamicError) {
                super.onDynamicError(onDynamicError);
                LogUtil.e(onDynamicError);
                ToastUtil.show(LoginActivity.this, onDynamicError);
            }
        });

    }
}
