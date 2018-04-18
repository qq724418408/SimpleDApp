package com.forms.base;

import android.app.Application;

import com.forms.dhttp.utils.SharedPreferencesUtil;

/**
 * @author Administrator
 */
public class BaseApplication extends Application {

	private static BaseApplication baseApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		baseApplication = this;
		SharedPreferencesUtil.getInstance().init("xxx.name");
	}

}
