package com.forms.dhttp.dbean;

import com.forms.okhttplibrary.builder.PostMultipartBuilder;
import com.forms.dhttp.base.RootRsp;
import com.forms.dhttp.http.excutor.DHttpCallBack;
import com.forms.dhttp.http.excutor.listener.Progress;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaxinqing on 2017/6/27.
 */
public class HttpList extends RootRsp {

    private String urlId;
    private Map<String, String> param;
    private Progress progress;
    private boolean checkLoginState;
    private DHttpCallBack callBackResponse;
    private Map<String, File> fileMap;
    private List<PostMultipartBuilder.FileObject> fileObjects;

    public HttpList(String urlId, Map<String, String> param, Progress progress, boolean checkLoginState, DHttpCallBack callBackResponse, Map<String, File> fileMap) {
        this.urlId = urlId;
        this.param = param;
        this.progress = progress;
        this.callBackResponse = callBackResponse;
        this.fileMap = fileMap;
        this.checkLoginState = checkLoginState;
    }

    public HttpList(String urlId, Map<String, String> param, Progress progress, boolean checkLoginState, DHttpCallBack callBackResponse) {
        this.urlId = urlId;
        this.param = param;
        this.progress = progress;
        this.callBackResponse = callBackResponse;
        this.checkLoginState = checkLoginState;
    }

    public HttpList(String urlId, Map<String, String> param, Progress progress, boolean checkLoginState, DHttpCallBack callBackResponse, List<PostMultipartBuilder.FileObject> fileObjects) {
        this.urlId = urlId;
        this.param = param;
        this.progress = progress;
        this.callBackResponse = callBackResponse;
        this.fileObjects = fileObjects;
        this.checkLoginState = checkLoginState;
    }

    public List<PostMultipartBuilder.FileObject> getFileObjects() {
        return fileObjects;
    }

    public void setFileObjects(List<PostMultipartBuilder.FileObject> fileObjects) {
        this.fileObjects = fileObjects;
    }

    public void setCheckLoginState(boolean checkLoginState) {
        this.checkLoginState = checkLoginState;
    }

    public boolean isCheckLoginState() {
        return checkLoginState;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public DHttpCallBack getCallBackResponse() {
        return callBackResponse;
    }

    public void setCallBackResponse(DHttpCallBack callBackResponse) {
        this.callBackResponse = callBackResponse;
    }

    public Map<String, File> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, File> fileMap) {
        this.fileMap = fileMap;
    }
}
