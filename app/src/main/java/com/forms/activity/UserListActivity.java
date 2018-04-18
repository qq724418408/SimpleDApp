package com.forms.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.forms.adapter.UserListAdapter;
import com.forms.base.BaseActivity;
import com.forms.bean.UserBean;
import com.forms.bean.UsersBean;
import com.forms.config.DHttpRequest;
import com.forms.dhttp.dbean.ContentResponse;
import com.forms.dhttp.http.excutor.DHttpCallBack;
import com.forms.dhttp.widget.LoadingDialog;
import com.forms.utils.ToastUtil;
import com.forms.view.CustomLoadMoreView;
import com.forms.xxxapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bubbly
 */
public class UserListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private UserBean user;
    private ImageView ivBack;
    //private View notDataView;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvUserList;
    private UserListAdapter adapter;
    private List<UserBean> data = new ArrayList<>();
    private String deleteUserId;
    private int pageNum = 1;
    private int totalPageNum;
    private int deletePosition;
    private boolean mIsRefreshing = false;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Bundle bundle = getIntent().getExtras();
        user = (UserBean) bundle.getSerializable("user");
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(this);
        rvUserList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mIsRefreshing;
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.cardView:
                        if (!data.isEmpty()) {
                            UserBean userBean = data.get(position);
                            String userId = userBean.getUserId();
                            ToastUtil.show(UserListActivity.this, TextUtils.isEmpty(userBean.getUserName()) ? "用户名未设置" : "用户名：" + userBean.getUserName());
                        }
                        break;
                    case R.id.tvDelete:
                        deleteUserId = data.get(position).getUserId();
                        deletePosition = position;
                        if (user.getUserId().equals(deleteUserId)) {
                            ToastUtil.show(UserListActivity.this, "不可以删除自己");
                            return;
                        }
                        deleteUser(deleteUserId, deletePosition);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        adapter = new UserListAdapter(this, data);
        rvUserList.setHasFixedSize(true);
        rvUserList.setLayoutManager(new LinearLayoutManager(this));
        data = new ArrayList<>();
        adapter.setOnLoadMoreListener(this, rvUserList);
        adapter.disableLoadMoreIfNotFullPage();
        adapter.setLoadMoreView(new CustomLoadMoreView());
        rvUserList.setAdapter(adapter);
        dialog = new LoadingDialog(this);
        getUserList(pageNum);
    }

    private void getUserList(int pageNum) {
        DHttpRequest.getUserList(pageNum + "", null, new DHttpCallBack<UsersBean>(new TypeReference<ContentResponse<UsersBean>>() {
        }) {
            @Override
            public void onSuccess(UsersBean response) {
                super.onSuccess(response);
                swipeRefresh.setEnabled(true);
                mIsRefreshing = false;
                adapter.setEnableLoadMore(true);
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                    data.clear();
                }
                if (response.getUserList().isEmpty()) {
                    if (data.isEmpty()) {
                        //adapter.setEmptyView(notDataView);
                    } else {
                        adapter.loadMoreEnd(false);
                    }
                } else {
                    totalPageNum = response.getTotalPageNum();
                    List<UserBean> users = response.getUserList();
                    data.addAll(users);
                    adapter.setNewData(data);
                    adapter.loadMoreComplete();
                }
            }

            @Override
            public void onEmpty(String response) {
                super.onEmpty(response);
                swipeRefresh.setEnabled(true);
                mIsRefreshing = false;
                adapter.setEnableLoadMore(true);
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                if (data.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    //adapter.setEmptyView(notDataView);
                } else {
                    adapter.loadMoreFail();
                }
            }

            @Override
            public void onError(String request, String exception) {
                super.onError(request, exception);
                swipeRefresh.setEnabled(true);
                mIsRefreshing = false;
                adapter.setEnableLoadMore(true);
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                if (data.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    //adapter.setEmptyView(notDataView);
                } else {
                    adapter.loadMoreFail();
                }
            }

            @Override
            public void otherCode(String code, String msg, UsersBean content) {
                super.otherCode(code, msg, content);
                swipeRefresh.setEnabled(true);
                mIsRefreshing = false;
                adapter.setEnableLoadMore(true);
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                if (data.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    //adapter.setEmptyView(notDataView);
                } else {
                    adapter.loadMoreFail();
                }
            }

            @Override
            public void onDynamicError(String onDynamicError) {
                super.onDynamicError(onDynamicError);
                swipeRefresh.setEnabled(true);
                mIsRefreshing = false;
                adapter.setEnableLoadMore(true);
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                if (data.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    //adapter.setEmptyView(notDataView);
                } else {
                    adapter.loadMoreFail();
                }
            }
        });
    }

    public void deleteUser(String userId, final int position) {
        DHttpRequest.deleteUser(userId, dialog, new DHttpCallBack<String>(new TypeReference<ContentResponse<String>>() {
        }) {
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                ToastUtil.show(UserListActivity.this, "删除成功");
                adapter.remove(position);
                adapter.notifyItemRemoved(position);
                if (data.isEmpty()) {
                    //adapter.setEmptyView(notDataView);
                }
            }

            @Override
            public void onEmpty(String response) {
                super.onEmpty(response);
                ToastUtil.show(UserListActivity.this, "删除不成功");
            }

            @Override
            public void onError(String request, String exception) {
                super.onError(request, exception);
                ToastUtil.show(UserListActivity.this, "删除不成功");
            }

            @Override
            public void onDynamicError(String onDynamicError) {
                super.onDynamicError(onDynamicError);
                ToastUtil.show(UserListActivity.this, "删除不成功");
            }

            @Override
            public void otherCode(String code, String msg, String response) {
                super.otherCode(code, msg, response);
                ToastUtil.show(UserListActivity.this, "删除不成功");
            }
        });
    }

    @Override
    public void initView() {
        ivBack = findView(R.id.ivBack);
        rvUserList = findView(R.id.rvUserList);
        swipeRefresh = findView(R.id.swipeRefresh);
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

    @Override
    public void onRefresh() {
        pageNum = 1;
        mIsRefreshing = true;
        adapter.setEnableLoadMore(false);
        getUserList(pageNum);
    }

    @Override
    public void onLoadMoreRequested() {
        pageNum++;
        if (pageNum <= totalPageNum) {
            mIsRefreshing = true;
            swipeRefresh.setEnabled(false);
            getUserList(pageNum);
        } else {
            mIsRefreshing = false;
            adapter.loadMoreEnd();
        }
    }
}
