package com.forms.dhttp.base;

/**
 * 网络请求 JSON 格式通用 BEAN 类
 *
 * @param <T>
 */
public class BaseResponse<T> extends RootRsp {

	protected T message; //
	protected String result; //
//	protected String VIEW_ID; //
	protected String DYNAMIC_TOKEN; //
//	protected String STATIC_TOKEN; //

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

//	public String getVIEW_ID() {
//		return VIEW_ID;
//	}
//
//	public void setVIEW_ID(String VIEW_ID) {
//		this.VIEW_ID = VIEW_ID;
//	}

	public String getDYNAMIC_TOKEN() {
		return DYNAMIC_TOKEN;
	}

	public void setDYNAMIC_TOKEN(String DYNAMIC_TOKEN) {
		this.DYNAMIC_TOKEN = DYNAMIC_TOKEN;
	}

//	public String getSTATIC_TOKEN() {
//		return STATIC_TOKEN;
//	}
//
//	public void setSTATIC_TOKEN(String STATIC_TOKEN) {
//		this.STATIC_TOKEN = STATIC_TOKEN;
//	}
}
