package com.forms.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.forms.bean.UserBean;
import com.forms.config.UrlConfig;
import com.forms.utils.GlideCircleTransform;
import com.forms.xxxapp.R;

import java.util.List;

/**
 * Created by bubbly on 2018/4/3.
 */
public class UserListAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    private Context context;

    public UserListAdapter(Context context, List<UserBean> data) {
        super(R.layout.item_user_list_recyclerview, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        helper.setText(R.id.tvUserName, item.getUserName());
        helper.setText(R.id.tvUserId, item.getUserId());
        helper.addOnClickListener(R.id.cardView);
        helper.addOnClickListener(R.id.ivHeadPortrait);
        helper.addOnClickListener(R.id.tvDelete);
        if (!TextUtils.isEmpty(item.getProfilePhoto())) {
            ImageView ivHeadPortrait = helper.getView(R.id.ivHeadPortrait);
            Glide.with(context)
                    .load(UrlConfig.BASE_SERVER + item.getProfilePhoto())
                    .error(R.mipmap.icon_mine_head_portrait)
                    .placeholder(R.mipmap.icon_mine_head_portrait)
                    .transform(new GlideCircleTransform(context))
                    .into(ivHeadPortrait);
        }
//        if (null != user && user.getUserId().equals(item.getUserId())) {
//            helper.setText(R.id.tvStatus, "当前登录用户");
//        } else {
//            helper.setText(R.id.tvStatus, "");
//        }
    }
}
