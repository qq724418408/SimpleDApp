package com.forms.dhttp.http.excutor.listener;

import com.forms.dhttp.http.excutor.DynamicRetry;

/**
 * Created by xiaxinqing on 2017/4/28.
 */

public interface Progress {

    String DIALOG = "dialog.key";
    String LOADINGVIEW = "loading.key";

    /**
     * 传入view 为dialogView时 return DIALOG
     * 传入view为loadView时， return LOADINGVIEW
     *
     * @return
     */
    String type();

    void onFailed();//关闭view

    void onDataEmpty();//关闭view

    void showProgress();//显示view

    void hidden();//关闭view

    void addRetryListener(DynamicRetry retry);

    DynamicRetry getRetryListener();

    void setTag(Object tag);

    Object getTag();
}
