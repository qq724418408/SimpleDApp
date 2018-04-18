package com.forms.dhttp.http.excutor;


import android.support.annotation.CallSuper;

import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;

/**
 * 动态url回调层解析
 *
 * @param <T>
 * @author 夏新庆
 */
public abstract class DHttpCallBack<T> {

    private Type classType;

    public DHttpCallBack(TypeReference typeReference) {
        classType = typeReference.getType();
    }

    public DHttpCallBack() {
    }

    Type getClassType() {
        return classType;
    }

    @CallSuper
    public void onSuccess(T response) {
        clearParams();
    }

    @CallSuper
    public void onEmpty(String response) {
        clearParams();
    }

    /**
     * @param request   配置提示信息
     * @param exception (Hard coded，仅供开发者查看)
     */
    @CallSuper
    public void onError(String request, String exception) {
        clearParams();
    }

    public void onProgress(double progess) {
    }

    @CallSuper
    public void otherCode(String code, String msg, T content) {
        clearParams();
    }

    /**
     * 动态url 流程出错
     */
    @CallSuper
    public void onDynamicError(String onDynamicError) {
        clearParams();
    }

    @CallSuper
    public void loginSuccessed() {//需要accesstoken 登录，并且登录成功
        clearParams();
    }

    @CallSuper
    public void loginFailed(String msg) {//需要accesstoken 登录，并且登录失败
        clearParams();
//        SpreaditApplication application = ContextUtil.getApplication();
//        application.startActivity(new Intent(application, LoginActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void clearParams() {
        DHttpExcutorList.getInstance().clearParams();
    }
}