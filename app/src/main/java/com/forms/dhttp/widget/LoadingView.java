package com.forms.dhttp.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forms.xxxapp.R;
import com.forms.dhttp.http.excutor.DynamicRetry;
import com.forms.dhttp.http.excutor.listener.Progress;

public class LoadingView extends LinearLayout implements Progress, View.OnClickListener {

    LinearLayout llMain;
    LinearLayout llLoading;
    LinearLayout llFailed;
    TextView tvFailed;
    LinearLayout llNoData;
    TextView tvNoData;

    private boolean showStateBar = false;

    private Object tag;
    private DynamicRetry retry;
    private TextView loadStateBar;

    private ImageView ivLoadError;
    private ImageView ivLoadNodata;

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loadingview, this);
        loadStateBar = (TextView) findViewById(R.id.ldStateBar);
//        loadStateBar.setHeight(ContextUtil.getApplication().getStateBarHeigh());// TODO: 2017/7/19  状态栏高度
        llMain = (LinearLayout) view.findViewById(R.id.llMain);
        llLoading = (LinearLayout) view.findViewById(R.id.llLoading);
        llFailed = (LinearLayout) view.findViewById(R.id.llFailed);
        llNoData = (LinearLayout) view.findViewById(R.id.llNoData);
        tvFailed = (TextView) view.findViewById(R.id.tvFailed);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);
        ivLoadError = (ImageView) view.findViewById(R.id.ivLoadError);
        ivLoadNodata = (ImageView) view.findViewById(R.id.ivLoadNodata);

        llNoData.setOnClickListener(this);
        llFailed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        reTry();
    }

    @Override
    public void setTag(Object tag) {
        this.tag = tag;
    }

    public LoadingView setFailedImage(int resid) {
        ivLoadError.setImageResource(resid);
        return this;
    }

    public LoadingView setNoDataImage(int resid) {
        ivLoadNodata.setImageResource(resid);
        return this;
    }

    public LoadingView setFailedWarning(int textid) {
        tvFailed.setText(textid);
        return this;
    }

    public LoadingView setNoDataWarning(int textid) {
        tvNoData.setText(textid);
        return this;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    /**
     * 设置无数据时的提示文字
     *
     * @param noDataText
     * @return
     */
    public void setNoDataText(String noDataText) {
        if (!TextUtils.isEmpty(noDataText)) {
            this.tvNoData.setText(noDataText);
        } else {
            this.tvNoData.setText(getContext().getString(R.string.no_data));
        }
    }

    /**
     * 设置失败时的提示文字
     *
     * @param failedText
     */
    public void setFailedText(String failedText) {
        if (!TextUtils.isEmpty(failedText)) {
            this.tvFailed.setText(failedText);
        } else {
            this.tvFailed.setText(getContext().getString(R.string.login_error_with_connect_failed));
        }
    }

    public void setFailedImg(int resourse) {
        this.ivLoadError.setImageResource(resourse);
    }

    public void setNodataImg(int resourse) {
        this.ivLoadNodata.setImageResource(resourse);
    }

    public void setGone() {
        if (showStateBar)
            loadStateBar.setVisibility(View.GONE);
        llNoData.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        llFailed.setVisibility(GONE);
        setVisibility(GONE);
    }

    public void reTry() {
        if (retry != null) {
            retry.onRetry();
        }
    }

    public void cancelProgress() {
        setGone();
        try {
            if (tag != null) {
//				ThreadPoolUtil.getInstance().cancel(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgress() {
        if (showStateBar)
            loadStateBar.setVisibility(View.VISIBLE);
        llNoData.setVisibility(GONE);
        llLoading.setVisibility(VISIBLE);
        llFailed.setVisibility(GONE);
        setVisibility(VISIBLE);
    }

    @Override
    public void hidden() {
        setGone();
    }

    // @Override
    // public void onComplete() {
    // Timber.d("加载完成");
    // setGone();
    // }
    @Override
    public void onDataEmpty() {
        if (showStateBar)
            loadStateBar.setVisibility(View.VISIBLE);
        llNoData.setVisibility(VISIBLE);
        llLoading.setVisibility(GONE);
        llFailed.setVisibility(GONE);
        setVisibility(VISIBLE);
    }

    @Override
    public String type() {
        return Progress.LOADINGVIEW;
    }

    @Override
    public void onFailed() {
        if (showStateBar)
            loadStateBar.setVisibility(View.VISIBLE);
        llNoData.setVisibility(GONE);
        llLoading.setVisibility(GONE);
        llFailed.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
    }

    @Override
    public void addRetryListener(DynamicRetry retry) {
        this.retry = retry;
    }

    @Override
    public DynamicRetry getRetryListener() {
        return retry;
    }

    public void showStateBar(boolean showStateBar) {
        this.showStateBar = showStateBar;
    }

}
