package com.forms.dhttp.dbean;


import com.forms.dhttp.base.RootRsp;

public class DynamicUri extends RootRsp {

	private boolean on;
	private String entry;

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
}
