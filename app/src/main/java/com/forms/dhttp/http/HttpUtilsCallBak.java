package com.forms.dhttp.http;

/**
 * 动态url 解耦合专用
 *
 * @author 夏新庆
 */
public abstract class HttpUtilsCallBak {

    public abstract void onSuccess(String response);

//    public abstract void onFailed(String errorMsg, String e);

    public abstract void onError(String errorMsg, String e);

//    public abstract void onEmpty(String error);
}
