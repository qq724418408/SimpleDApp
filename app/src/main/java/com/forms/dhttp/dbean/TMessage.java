package com.forms.dhttp.dbean;

import com.forms.dhttp.base.RootRsp;

/**
 * Created by Administrator on 2016/3/28.
 */
public class TMessage extends RootRsp {

	private String self;
	private String target;
	private String exception;

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
