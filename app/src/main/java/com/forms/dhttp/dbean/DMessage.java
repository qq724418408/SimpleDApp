package com.forms.dhttp.dbean;

import com.forms.dhttp.base.RootRsp;

import java.util.List;

public class DMessage extends RootRsp {

	private DynamicUri dynamicUri;
	private List<Function> functions;

	public DynamicUri getDynamicUri() {
		return dynamicUri;
	}

	public void setDynamicUri(DynamicUri dynamicUri) {
		this.dynamicUri = dynamicUri;
	}

	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}
}
