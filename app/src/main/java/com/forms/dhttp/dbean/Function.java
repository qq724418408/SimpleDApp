package com.forms.dhttp.dbean;

import com.forms.dhttp.base.RootRsp;

public class Function extends RootRsp {

	private String id;
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
