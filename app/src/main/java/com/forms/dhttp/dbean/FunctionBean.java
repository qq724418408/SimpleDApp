package com.forms.dhttp.dbean;


import com.forms.dhttp.base.RootRsp;

/**
 * Created by xiaxinqing on 2017/5/9.
 */

public class FunctionBean extends RootRsp {

    private String funcName;
    private String funcNameS;
    private String funcNameT;
    private String funcNameE;
    private String funcID; // 9999为更多那个图标
    private String funcUrl;
    private String funcImageUrl;
    private String funcType; // 0：账户 1：理财 2：生活 9：更多
    private int funcResId; // 本地资源文件
    private int editState = 0; //编辑状态,默认正常状态
    private boolean isAdded;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getFuncNameS() {
        return funcNameS;
    }

    public void setFuncNameS(String funcNameS) {
        this.funcNameS = funcNameS;
    }

    public String getFuncNameT() {
        return funcNameT;
    }

    public void setFuncNameT(String funcNameT) {
        this.funcNameT = funcNameT;
    }

    public String getFuncNameE() {
        return funcNameE;
    }

    public void setFuncNameE(String funcNameE) {
        this.funcNameE = funcNameE;
    }

    public String getFuncID() {
        return funcID;
    }

    public void setFuncID(String funcID) {
        this.funcID = funcID;
    }

    public String getFuncUrl() {
        return funcUrl;
    }

    public void setFuncUrl(String funcUrl) {
        this.funcUrl = funcUrl;
    }

    public String getFuncImageUrl() {
        return funcImageUrl;
    }

    public void setFuncImageUrl(String funcImageUrl) {
        this.funcImageUrl = funcImageUrl;
    }

    public String getFuncType() {
        return funcType;
    }

    public void setFuncType(String funcType) {
        this.funcType = funcType;
    }

    public int getFuncResId() {
        return funcResId;
    }

    public void setFuncResId(int funcResId) {
        this.funcResId = funcResId;
    }

    public int getEditState() {
        return editState;
    }

    public void setEditState(int editState) {
        this.editState = editState;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    @Override
    public String toString() {
        return "FunctionBean{" +
                "funcNameS='" + funcNameS + '\'' +
                ", funcNameT='" + funcNameT + '\'' +
                ", funcNameE='" + funcNameE + '\'' +
                ", funcID='" + funcID + '\'' +
                ", funcUrl='" + funcUrl + '\'' +
                ", funcImageUrl='" + funcImageUrl + '\'' +
                ", funcType='" + funcType + '\'' +
                '}';
    }
}
