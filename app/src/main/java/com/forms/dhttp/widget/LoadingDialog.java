package com.forms.dhttp.widget;

import android.content.Context;
import android.content.DialogInterface;

import com.forms.okhttplibrary.util.HttpUtil;
import com.forms.dhttp.http.excutor.Dmanager;
import com.forms.dhttp.http.excutor.DynamicRetry;
import com.forms.dhttp.http.excutor.listener.Progress;

public class LoadingDialog extends LProgressDialog implements Progress {

    private Object tag;

    @Override
    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    public LoadingDialog(Context context) {
        super(context);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelProgress();
            }
        });
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelProgress();
            }
        });
    }

    public void reTry() {
//		retryHandler.retry();
    }

    public void cancelProgress() {
        try {
            if (tag != null) {
                Dmanager.getInstance().removeHttp((String) tag);
                HttpUtil.getInstance().cancelRequest(tag);
                tag = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Override
    // public void onComplete() {
    // Timber.d("加载完成");
    // dismiss();
    // }

    @Override
    public String type() {
        return Progress.DIALOG;
    }

    @Override
    public void onFailed() {
        // TODO Auto-generated method stub
        dismiss();
    }

    @Override
    public void onDataEmpty() {
        dismiss();
    }

    @Override
    public void showProgress() {
        show();
    }

    @Override
    public void hidden() {
        dismiss();
    }

    @Override
    public void addRetryListener(DynamicRetry retry) {

    }

    @Override
    public DynamicRetry getRetryListener() {
        return null;
    }

}
