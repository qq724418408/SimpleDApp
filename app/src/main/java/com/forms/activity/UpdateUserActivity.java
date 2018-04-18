package com.forms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.forms.xxxapp.R;
import com.forms.base.BaseActivity;
import com.forms.bean.UserBean;

/**
 * @author bubbly
 */
public class UpdateUserActivity extends BaseActivity {

    private UserBean user;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        Bundle bundle = getIntent().getExtras();
        user = (UserBean) bundle.getSerializable("user");
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        ivBack = findView(R.id.ivBack);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            default:
                break;
        }
    }
}
