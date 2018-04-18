package com.forms.config;

/**
 * 返回码
 *
 * @author create by xxq
 */
public enum ResultCode {

    SUCCESS("10000", "请求成功"),
//    FAILED("10001", "请求失败，请稍后重试"),
    //动态url返回结果
//    DSUCCESS("success", "请求成功"),
//    DEMPTY("empty", "数据为空"),//动态url
    NODATA("10013", "暂无数据"),
    SYSTEMERROR("10001", "系统错误");
    private String code;
    private String msg;

    private ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
