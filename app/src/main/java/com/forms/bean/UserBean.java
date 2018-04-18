package com.forms.bean;

import com.forms.dhttp.base.RootRsp;

public class UserBean extends RootRsp {

	private String userId;
	private String userName;
	private String pwd;
	private String profilePhoto; // 头像

	public UserBean() {
	}
	
	public UserBean(String userName) {
		super();
		this.userName = userName;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserBean(String userName, String pwd) {
		super();
		this.userName = userName;
		this.pwd = pwd;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public String toString() {
		return "UserBean [userName=" + userName + ", pwd=" + pwd + "]";
	}

}
