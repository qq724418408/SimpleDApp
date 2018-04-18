package com.forms.bean;

import com.forms.dhttp.base.RootRsp;

import java.util.List;

public class UsersBean extends RootRsp {

	private int totalPageNum;
	private List<UserBean> userList;

	public int getTotalPageNum() {
		return totalPageNum;
	}

	public void setTotalPageNum(int totalPageNum) {
		this.totalPageNum = totalPageNum;
	}

	public List<UserBean> getUserList() {
		return userList;
	}

	public void setUserList(List<UserBean> userList) {
		this.userList = userList;
	}
}
