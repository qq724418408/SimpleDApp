package com.forms.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.forms.xxxapp.R;
import com.forms.base.BaseActivity;
import com.forms.config.DHttpRequest;

/**
 * @author Administrator
 */
public class WelcomeActivity extends BaseActivity {

    private ImageView ivWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setAllowFullScreen(true);
        DHttpRequest.initHttp(getApplication(), "");
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void initData() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(3000);
        ivWelcome.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(LoginActivity.class);
                finish();
            }
        });
    }

    @Override
    public void initView() {
        ivWelcome = findView(R.id.ivWelcome);
    }

    @Override
    public void widgetClick(View v) {

    }
}
