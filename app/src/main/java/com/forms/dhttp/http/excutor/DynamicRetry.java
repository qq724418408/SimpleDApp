package com.forms.dhttp.http.excutor;

/**
 * 重试
 *
 * @author xxq
 */
public abstract class DynamicRetry {

    public void onFailed() {
    }

    public void onEmpty() {
    }

    public abstract void onRetry();
}
