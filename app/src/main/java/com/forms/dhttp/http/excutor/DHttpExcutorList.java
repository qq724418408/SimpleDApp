package com.forms.dhttp.http.excutor;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.forms.okhttplibrary.builder.PostMultipartBuilder;
import com.forms.config.ResultCode;
import com.forms.dhttp.base.BaseResponse;
import com.forms.dhttp.config.DConfing;
import com.forms.dhttp.dbean.ContentResponse;
import com.forms.dhttp.dbean.DMessage;
import com.forms.dhttp.dbean.DynamicUri;
import com.forms.dhttp.dbean.Function;
import com.forms.dhttp.dbean.HttpList;
import com.forms.dhttp.dbean.TMessage;
import com.forms.dhttp.http.DHttpUtilsList;
import com.forms.dhttp.http.HttpUtilsCallBak;
import com.forms.dhttp.http.excutor.listener.Progress;
import com.forms.dhttp.utils.LogUtil;
import com.forms.dhttp.utils.SharedPreferencesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态url 请求流程封装
 * 加入队列
 *
 * @author 夏新庆
 */
public class DHttpExcutorList {

    static DHttpExcutorList mOkHttp;
    private Context context;//appliactionContext
    private static Builder builder = new Builder();
    private List<Function> functions;
    private static String serverUrl;
    private static String serverRootPath;
    private volatile boolean isRunning = false;

    /*************************参数********************************/
    private Map<String, File> fileMap;
    private Progress progress;
    private Map<String, String> params;
    private List<PostMultipartBuilder.FileObject> fileObjects;
    private String id;
    private DHttpCallBack callback;
    private boolean checkLoginState;//是否检测登录状态
    private boolean logining = false;

    /*************************动态保存数据********************************/
    private SharedPreferencesUtil preferencesUtils;
    private static final String ISON = "IS_ON.key";
    private static final String ENTTRY = "Entry.key";
    private final String LOGINTOKEN = "loginToken.key";
    private String entry;
    private String accessToken;

    private DHttpExcutorList() {
        super();
        preferencesUtils = SharedPreferencesUtil.getInstance();
    }

    public static DHttpExcutorList getInstance() {
        synchronized (DHttpExcutorList.class) {
            if (mOkHttp == null) {
                mOkHttp = new DHttpExcutorList();
            }
        }
        return mOkHttp;
    }

    public synchronized void sendPost(@NonNull String urlID, Map<String, String> params,
                                      Progress progress, @NonNull boolean checkLoginState, @NonNull DHttpCallBack callBackResponse) {
        if (!hasNetwork(context)) {
            isRunning = false;
            showOnFailed(progress);
            callBackResponse.onError(urlID, DConfing.ErrorFromNet);
            return;
        }
        if (progress != null) {
            progress.showProgress();
            progress.setTag(urlID);
        }
        if (isRunning) {
            logMsg("任务繁忙--" + urlID);
            HttpList http = new HttpList(urlID, params, progress, checkLoginState, callBackResponse);
           Dmanager.getInstance().addRequest(http);
        } else {
            this.fileMap = null;
            this.fileObjects = null;
            this.progress = progress;
            this.params = params;
            if (this.params == null)
                this.params = new HashMap<>();
            this.id = urlID;
            this.callback = callBackResponse;
            this.checkLoginState = checkLoginState;
            initHttp();
        }

    }

    public synchronized void sendPost(@NonNull String urlID, Map<String, String> params, Map<String, File> fileMap,
                                      Progress progress, @NonNull boolean checkLoginState, @NonNull DHttpCallBack callBackResponse) {
        if (!hasNetwork(context)) {
            isRunning = false;
            showOnFailed(progress);
            callBackResponse.onError(urlID, DConfing.ErrorFromNet);
            return;
        }
        if (progress != null) {
            progress.showProgress();
            progress.setTag(urlID);
        }
        if (isRunning) {
            logMsg("任务繁忙--" + urlID);
            HttpList http = new HttpList(urlID, params, progress, checkLoginState, callBackResponse, fileMap);
            Dmanager.getInstance().addRequest(http);
        } else {
            this.fileMap = fileMap;
            this.progress = progress;
            this.params = params;
            this.fileObjects = null;
            if (this.params == null)
                this.params = new HashMap<>();
            this.id = urlID;
            this.callback = callBackResponse;
            this.checkLoginState = checkLoginState;
            initHttp();
        }
    }

    public synchronized void sendPost(@NonNull String urlID, Map<String, String> params, List<PostMultipartBuilder.FileObject> fileObjects,
                                      Progress progress, @NonNull boolean checkLoginState, @NonNull DHttpCallBack callBackResponse) {
        if (!hasNetwork(context)) {
            isRunning = false;
            showOnFailed(progress);
            callBackResponse.onError(urlID, DConfing.ErrorFromNet);
            return;
        }
        if (progress != null) {
            progress.showProgress();
            progress.setTag(urlID);
        }
        if (isRunning) {
            logMsg("任务繁忙--添加至请求队列" + urlID);
            HttpList http = new HttpList(urlID, params, progress, checkLoginState, callBackResponse, fileObjects);
           Dmanager.getInstance().addRequest(http);
        } else {
            this.fileMap = null;
            this.fileObjects = fileObjects;
            this.progress = progress;
            this.params = params;
            if (this.params == null)
                this.params = new HashMap<>();
            this.id = urlID;
            this.callback = callBackResponse;
            this.checkLoginState = checkLoginState;
            initHttp();
        }

    }

    synchronized void managerNext(@NonNull HttpList http) {
        if (!isRunning) {
            logMsg("managerNext--next 开始执行--" + http.getUrlId());
        } else {
           Dmanager.getInstance().addRequest(http);
            logMsg("managerNext--任务繁忙--重新加入队列" + http.getUrlId());
            return;
        }
        this.fileMap = http.getFileMap();
        this.progress = http.getProgress();
        this.params = http.getParam();
        this.fileObjects = http.getFileObjects();
        if (this.params == null)
            this.params = new HashMap<>();
        this.id = http.getUrlId();
        this.callback = http.getCallBackResponse();
        this.checkLoginState = http.isCheckLoginState();
        initHttp();
    }

    public synchronized void downloadFile(String url, String fileDir, String fileName, Progress progress, final DHttpCallBack callBack) {
        DHttpUtilsList.downloadFileBase(url, fileDir, fileName, progress, callBack);
    }

    private synchronized void initHttp() {
        if (callback == null) {
            isRunning = false;
            return;
        }
        isRunning = true;
        logMsg("initHttp 开始执行--" + id);
        Integer ison = preferencesUtils.getValue(context, ISON, Integer.class);
        if (ison != -1 && functions != null && functions.size() > 0) {
            next();
        } else {
            if (DHttpExcutorList.builder.menuListParam.size() > 0)
                this.params.putAll(DHttpExcutorList.builder.menuListParam);
            sendRequestMenuList();
        }
    }

    private void sendRequestMenuList() {
        if (callback == null) {
            isRunning = false;
            return;
        }
        logMsg("请求menulist");
        //DHttpUtilsList.sendRequest(UrlConfig.SERVER, id, addCommonParams(context, params), new HttpUtilsCallBak() {
        DHttpUtilsList.sendRequest(serverUrl + serverRootPath, id, addCommonParams(context, params), new HttpUtilsCallBak() {

            @Override
            public void onSuccess(String response) {
                logMsg("sendRequestMenuList_onSuccess--" + response);
                try {
                    // 避免异常情况，故拆开解析,请求返回已经判空
                    BaseResponse<String> baseResponse = JSON.parseObject(response, new TypeReference<BaseResponse<String>>() {}.getType());
                    String result = baseResponse.getResult();// 此时判断返回类型，即可避免解析异常。
                    logMsg("result = " + result);
                    if (!TextUtils.isEmpty(result)) {
                        switch (result) {
                            case "success":
                                saveToken(baseResponse);
                                if (!TextUtils.isEmpty(baseResponse.getMessage())) {
                                    DMessage message = JSON.parseObject(baseResponse.getMessage(), DMessage.class);
                                    List<Function> functions = message.getFunctions();
                                    DynamicUri dynamicUri = message.getDynamicUri();
                                    String entry = dynamicUri.getEntry();
                                    boolean isOn = dynamicUri.isOn();
                                    if (isOn && TextUtils.isEmpty(entry)) {
                                        isRunning = false;
                                        callDnaymicError(DConfing.ErrorFromServer, "初始化list失败");
                                        return;
                                    }
                                    initIsonWithMenulist(isOn, functions, entry);
                                    next();
                                } else {
                                    callDnaymicError(DConfing.ErrorFromServer, "服务器数据异常");
                                }
                                break;
                            case "timeout":
                                callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                break;
                            case "exception":
                                callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                break;
                            case "EF000005":
                                callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                break;
                            case "EF000003":// TODO: 2017/5/23 单设备登录
                                logOut();
                                callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                break;
                            default:
                                callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                break;
                        }
                    }
                } catch (Exception e) {
                    callDnaymicError(DConfing.ErrorFromServer, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMsg, String e) {
                callDnaymicError(errorMsg, e);
            }

        });
    }

    private void next() {
        remoMenuListParams();
        //xiaxinqing
        if (checkLoginState && !builder.loginState) {
            this.fileMap = null;
            this.params.clear();
            this.params.put("accessToken", getLoginToken());
            this.fileObjects = null;
            this.id = builder.loginTokenId;
            logining = true;
        } else {
            logining = id.equals(builder.loginTokenId);
        }
        logMsg("下一步---校验登录状态-->" + logining + checkLoginState + id);
        String url = "";
        if (functions != null && functions.size() > 0) {
            for (Function function : functions) {
                if (id.equals(function.getId())) {
                    url = function.getUrl();
                    break;
                }
            }
        }
        // 开启动态url
        if (!TextUtils.isEmpty(url)) {
            if (getIsOn(preferencesUtils.getValue(context, ISON, Integer.class))) {
                requestTargetUrl(url);
            } else {
                requestRealInterface(url);
            }
        } else {
            // TODO: 2017/7/11 开发中用到，接口调试完毕后不会出现该异常
            callDnaymicError(DConfing.ErrorFromServer, "动态url数据异常--未找到对应的接口ID");
        }
    }

    private void remoMenuListParams() {
        if (DHttpExcutorList.builder.menuListParam == null || DHttpExcutorList.builder.menuListParam.isEmpty())
            return;
        for (String key : DHttpExcutorList.builder.menuListParam.keySet()) {
            if (!params.isEmpty() && params.containsKey(key)) {
                params.remove(key);
            }
        }
    }

    private void requestRealInterface(String url) {
        if (callback == null) {
            isRunning = false;
            return;
        }
        logMsg("请求真实url");
        if (this.fileMap != null) {
            postParamWithFile(url);
        } else if (this.fileObjects != null) {
            postParamWithFileType(url);
        } else {
            postParam(url);
        }
    }

    private void requestTargetUrl(String url) {
        if (callback == null) {
            isRunning = false;
            return;
        }
        params.put("key", url);
        params.put("t", "d");
        logMsg("请求目标url entry = " + entry);
        if (TextUtils.isEmpty(entry)) {
            callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
            return;
        }

        DHttpUtilsList.sendRequest(serverUrl + entry, id, addCommonParams(context, params),
                new HttpUtilsCallBak() {

                    @Override
                    public void onSuccess(String response) {
                        logMsg("请求目标URL——Success \n" + response);
                        try {
                            // 同上
                            BaseResponse<String> baseResponse = JSON.parseObject(response, new TypeReference<BaseResponse<String>>() {}.getType());
                            String result = baseResponse.getResult();
                            logMsg("result = " + result);
                            if (!TextUtils.isEmpty(result)) {
                                switch (result) {
                                    case "success":
                                        saveToken(baseResponse);
                                        if (!TextUtils.isEmpty(baseResponse.getMessage())) {
                                            TMessage message = JSON.parseObject(baseResponse.getMessage(), TMessage.class);
                                            String target = message.getTarget();
                                            String self = message.getSelf();
                                            logMsg("entery =" + self + "\n target= " + target);
                                            if (TextUtils.isEmpty(target) || TextUtils.isEmpty(self)) {
                                                callDnaymicError(DConfing.ErrorFromServer, "动态流程失败，请求目标URL失败");
                                                return;
                                            }
                                            entry = self;
//                                            preferencesUtils.setValue(context, ENTTRY, self);
                                            requestRealInterface(target);
                                        } else {
                                            callDnaymicError(DConfing.ErrorFromServer, "服务器数据异常" + "\n方法ID = " + id + "\nentry = " + entry);
                                        }
                                        break;
                                    case "timeout":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                        break;
                                    case "exception":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                        break;
                                    case "EF000005":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                        break;
                                    case "EF000003":// TODO: 2017/5/23 单设备登录
                                        logOut();
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                        break;
                                    default:
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + entry);
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            callDnaymicError(DConfing.ErrorFromServer, e.toString() + "\n方法ID = " + id + "\nentry = " + entry);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String errorMsg, String e) {
                        callDnaymicError(errorMsg, e + "\n方法ID = " + id + "\nentry = " + entry);
                    }

//                    @Override
//                    public void onEmpty(String error) {
//                        showOnEmpty();
//                        clearLoginInfo();
//                        callBackResponse.onEmpty(error + "\n方法ID = " + id + "\nentry = " + entry);
//                    }

                });

    }

    private <T> void postParamWithFile(final String url) {
        logMsg("请求真实url携带参数");
        DHttpUtilsList.sendRequestWithFile(serverUrl + url, addCommonParams(context, params), fileMap, id,
                new HttpUtilsCallBak() {

                    @Override
                    public void onSuccess(String response) {
                        isRunning = false;
                        if (id.equals(DHttpExcutorList.builder.logOutId)) {
                            accessToken = "";
                            clearLoginInfo();
                            loginFailedDeleToken();
                        }
                        if (callback == null)
                            return;
                        // response =
                        // "{\"message\":{}},\"result\":\"success\",\"DYNAMIC_TOKEN\":\"EHS5EX0JWK2N49O9LX5H4X0UP3D8QZ92\",\"STATIC_TOKEN\":\"OYSMMW01MEO8U9KRH4EYNH36GIZ89P66\"}";
                        try {
                            // 同上
                            logMsg("result ———— >" + response);
                            BaseResponse<String> baseResponse = JSON.parseObject(response, new TypeReference<BaseResponse<String>>() {}.getType());
                            String result = baseResponse.getResult();
                            logMsg("result = " + result);
                            dismissDialog();
                            if (!TextUtils.isEmpty(result)) {
                                switch (result) {
                                    case "success":
                                        saveToken(baseResponse);
                                        if (!TextUtils.isEmpty(baseResponse.getMessage())) {
                                            ContentResponse<T> rtnMsg = JSON.parseObject(baseResponse.getMessage(), callback.getClassType());
                                            if (rtnMsg != null) {
                                                String rtnCode = rtnMsg.getRtnCode();
                                                String msg = rtnMsg.getRtnMsg();
                                                if (rtnCode.equals(ResultCode.SUCCESS.getCode())) {
                                                    updateFunction(rtnMsg.getFunctions());
                                                    updateLoginToken(rtnMsg.getAccessToken());
                                                    T content = rtnMsg.getContent();
                                                    if (callback == null)
                                                        return;
                                                    if (logining)
                                                        callback.loginSuccessed();
                                                    else
                                                        callback.onSuccess(content);
                                                    Dmanager.getInstance().next();
                                                } else if (ResultCode.NODATA.getCode().equals(rtnCode)) {
                                                    if (logining) {
                                                        logOut();
                                                        return;
                                                    }
                                                    showOnEmpty();
                                                    if (callback == null)
                                                        return;
                                                    callback.onEmpty(msg);
                                                    Dmanager.getInstance().next();
                                                }
//                                                else if (ResultCode.SYSTEMERROR.getCode().equals(rtnCode)) {
//                                                    clearLoginInfo();
//                                                    callDnaymicError(rtnCode + msg);
//                                                }
                                                else {
                                                    if (logining) {
                                                        logOut();
                                                        return;
                                                    }
                                                    if (callback == null)
                                                        return;
                                                    callback.otherCode(rtnCode, msg, rtnMsg.getContent());
                                                    if (checkException(rtnCode))
                                                        Dmanager.getInstance().next();
                                                }
                                            } else {
                                                callError(DConfing.ErrorFromServer + "\n方法ID = " + id + "\nentry = " + url, null);
                                            }
                                        } else {
                                            callError(DConfing.ErrorFromServer, "服务器数据异常" + "\n方法ID = " + id + "\nentry = " + url);
                                        }
                                        break;
                                    case "timeout":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "exception":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "EF000005":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "EF000003":// TODO: 2017/5/23 单设备登录
                                        logOut();
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    default:
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            callDnaymicError(DConfing.ErrorFromServer, e.toString() + "\n方法ID = " + id + "\nentry = " + url);
                        }

                    }

                    @Override
                    public void onError(String errorMsg, String e) {
                        callDnaymicError(errorMsg, e + "\n方法ID = " + id + "\nentry = " + url);
                    }

                });
    }

    private <T> void postParamWithFileType(final String url) {
        logMsg("请求真实url携带参数");
        DHttpUtilsList.sendRequestWithFile(serverUrl + url, addCommonParams(context, params), fileObjects, id,
                new HttpUtilsCallBak() {

                    @Override
                    public void onSuccess(String response) {
                        isRunning = false;
                        if (id.equals(DHttpExcutorList.builder.logOutId)) {
                            accessToken = "";
                            clearLoginInfo();
                            loginFailedDeleToken();
                        }
                        if (callback == null)
                            return;
                        // response =
                        // "{\"message\":{}},\"result\":\"success\",\"DYNAMIC_TOKEN\":\"EHS5EX0JWK2N49O9LX5H4X0UP3D8QZ92\",\"STATIC_TOKEN\":\"OYSMMW01MEO8U9KRH4EYNH36GIZ89P66\"}";
                        try {
                            // 同上
                            logMsg("result ———— >" + response);
                            BaseResponse<String> baseResponse = JSON.parseObject(response, new TypeReference<BaseResponse<String>>() {}.getType());
                            String result = baseResponse.getResult();
                            logMsg("result = " + result);
                            dismissDialog();
                            if (!TextUtils.isEmpty(result)) {
                                switch (result) {
                                    case "success":
                                        saveToken(baseResponse);
                                        if (!TextUtils.isEmpty(baseResponse.getMessage())) {
                                            ContentResponse<T> rtnMsg = JSON.parseObject(baseResponse.getMessage(),
                                                    callback.getClassType());
                                            if (rtnMsg != null) {
                                                String rtnCode = rtnMsg.getRtnCode();
                                                String msg = rtnMsg.getRtnMsg();
                                                if (rtnCode.equals(ResultCode.SUCCESS.getCode())) {
                                                    updateFunction(rtnMsg.getFunctions());
                                                    updateLoginToken(rtnMsg.getAccessToken());
                                                    T content = rtnMsg.getContent();
                                                    if (callback == null)
                                                        return;
                                                    if (logining)
                                                        callback.loginSuccessed();
                                                    else
                                                        callback.onSuccess(content);
                                                    Dmanager.getInstance().next();
                                                } else if (ResultCode.NODATA.getCode().equals(rtnCode)) {
                                                    if (logining) {
                                                        logOut();
                                                        return;
                                                    }
                                                    showOnEmpty();
                                                    if (callback == null)
                                                        return;
                                                    callback.onEmpty(msg);
                                                    Dmanager.getInstance().next();
                                                }
//                                                else if (ResultCode.SYSTEMERROR.getCode().equals(rtnCode)) {
//                                                    clearLoginInfo();
//                                                    callDnaymicError(rtnCode + msg);
//                                                }
                                                else {
                                                    if (logining) {
                                                        logOut();
                                                        return;
                                                    }
                                                    if (callback == null)
                                                        return;
                                                    callback.otherCode(rtnCode, msg, rtnMsg.getContent());
                                                    if (checkException(rtnCode))
                                                        Dmanager.getInstance().next();
                                                }
                                            } else {
                                                callError(DConfing.ErrorFromServer + "\n方法ID = " + id + "\nentry = " + url, null);
                                            }
                                        } else {
                                            callError(DConfing.ErrorFromServer, "服务器数据异常" + "\n方法ID = " + id + "\nentry = " + url);
                                        }
                                        break;
                                    case "timeout":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "exception":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "EF000005":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "EF000003":// TODO: 2017/5/23 单设备登录
                                        logOut();
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    default:
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            callDnaymicError(DConfing.ErrorFromServer, e.toString() + "\n方法ID = " + id + "\nentry = " + url);
                        }

                    }

                    @Override
                    public void onError(String errorMsg, String e) {
                        callDnaymicError(errorMsg, e + "\n方法ID = " + id + "\nentry = " + url);
                    }

                });
    }

    private <T> void postParam(final String url) {
        logMsg("请求真实url携带参数");
        DHttpUtilsList.sendRequest(serverUrl + url, id, addCommonParams(context, params),
                new HttpUtilsCallBak() {

                    @Override
                    public void onSuccess(String response) {
                        isRunning = false;
                        if (id.equals(DHttpExcutorList.builder.logOutId)) {
                            accessToken = "";
                            clearLoginInfo();
                            loginFailedDeleToken();
                        }
                        if (callback == null)
                            return;
                        try {
                            // 同上
                            logMsg("result ———— >" + response);
                            BaseResponse<String> baseResponse = JSON.parseObject(response, new TypeReference<BaseResponse<String>>() {}.getType());
                            String result = baseResponse.getResult();
                            logMsg("result = " + result);
                            dismissDialog();
                            if (!TextUtils.isEmpty(result)) {
                                switch (result) {
                                    case "success":
                                        saveToken(baseResponse);
                                        if (!TextUtils.isEmpty(baseResponse.getMessage())) {
                                            ContentResponse<T> rtnMsg = JSON.parseObject(baseResponse.getMessage(), callback.getClassType());
                                            if (rtnMsg != null) {
                                                String rtnCode = rtnMsg.getRtnCode();
                                                String msg = rtnMsg.getRtnMsg();
                                                if (rtnCode.equals(ResultCode.SUCCESS.getCode())) {
                                                    updateFunction(rtnMsg.getFunctions());
                                                    updateLoginToken(rtnMsg.getAccessToken());
                                                    T content = rtnMsg.getContent();
                                                    if (callback == null)
                                                        return;
                                                    if (logining)
                                                        callback.loginSuccessed();
                                                    else
                                                        callback.onSuccess(content);
                                                    Dmanager.getInstance().next();
                                                } else if (ResultCode.NODATA.getCode().equals(rtnCode)) {
                                                    if (logining) {
                                                        logOut();
                                                        return;
                                                    }
                                                    showOnEmpty();
                                                    if (callback == null)
                                                        return;
                                                    callback.onEmpty(msg);
                                                    Dmanager.getInstance().next();
                                                } else {
                                                    if (logining) {
                                                        logOut();
                                                        return;
                                                    }
                                                    if (callback == null)
                                                        return;
                                                    callback.otherCode(rtnCode, msg, rtnMsg.getContent());
                                                    if (checkException(rtnCode))
                                                        Dmanager.getInstance().next();
                                                }
                                            } else {
                                                callError(DConfing.ErrorFromServer, "\n方法ID = " + id + "\nentry = " + url);
                                            }
                                        } else {
                                            callError(DConfing.ErrorFromServer, "服务器数据异常" + "\n方法ID = " + id + "\nentry = " + url);
                                        }
                                        break;
                                    case "timeout":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "exception":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "EF000005":
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    case "EF000003":// TODO: 2017/5/23 单设备登录
                                        logOut();
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                    default:
                                        callDnaymicError(DConfing.ErrorFromDynamic, "\n方法ID = " + id + "\nentry = " + url);
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            callDnaymicError(DConfing.ErrorFromServer, e.toString() + "\n方法ID = " + id + "\nentry = " + url);
                        }

                    }

                    @Override
                    public void onError(String errorMsg, String e) {

                        callDnaymicError(DConfing.ErrorFromServer, errorMsg + e + "\n方法ID = " + id + "\nentry = " + url);
                    }

//                    @Override
//                    public void onEmpty(String error) {
////                        clearLoginInfo();
//                        if (TextUtils.isEmpty(error)) {
//                            error = "未知错误";
//                        }
//                        showOnEmpty();
//                        callBackResponse.onEmpty(error + "\n方法ID = " + id + "\nentry = " + url);
//                    }
                });
    }

    /**
     * @param s 配置提示信息
     * @param e (Hard coded，仅供开发者查看)
     */
    private void callError(String s, String e) {
        logMsg("队列剩余 --" + Dmanager.getInstance().getHttpNum());
        isRunning = false;
        Dmanager.getInstance().removeAllHttp();
        logMsg("callError --" + s + "\n" + e);
        if (id.equals(DHttpExcutorList.builder.logOutId)) {
            accessToken = "";
            loginFailedDeleToken();
        }
        showOnFailed(progress);
        clearLoginInfo();
        loginFailed();
        if (callback == null)
            return;
        if (logining)
            callback.loginFailed(s);
        else
            callback.onError(s, e);
    }

    private void callDnaymicError(String msg, String proMsg) {
        logMsg("队列剩余 --" + Dmanager.getInstance().getHttpNum());
        isRunning = false;
        Dmanager.getInstance().removeAllHttp();
        logMsg("callDnaymicError --" + msg + "\n" + proMsg);
        if (id.equals(DHttpExcutorList.builder.logOutId)) {
            accessToken = "";
            loginFailedDeleToken();
        }
        showOnFailed(progress);
        clearLoginInfo();
        loginFailed();
        if (callback == null)
            return;
        if (logining)
            callback.loginFailed(msg);
        else
            callback.onDynamicError(msg);
    }


    private void logOut() {
        isRunning = false;
        Dmanager.getInstance().removeAllHttp();
        logMsg("队列剩余 --" + Dmanager.getInstance().getHttpNum());
        accessToken = "";
        accessToken = null;
        showOnFailed(progress);
        clearLoginInfo();
        loginFailed();
        if (callback == null)
            return;
        if (logining)
            callback.loginFailed(null);
        else
            callback.onDynamicError(null);
    }

    private boolean checkException(String code) {
        if (!DHttpExcutorList.builder.exceptionCodeList.isEmpty() && DHttpExcutorList.builder.exceptionCodeList.contains(code)) {
            isRunning = false;
            Dmanager.getInstance().removeAllHttp();
            accessToken = "";
            showOnFailed(progress);
            clearLoginInfo();
            loginFailed();
            return false;
        }
        if (DHttpExcutorList.builder.otherCodeList.size() > 0 && DHttpExcutorList.builder.otherCodeList.contains(code)) {
            showOnFailed(progress);
        }
        return true;
    }

    private void showOnFailed(Progress progress) {
        if (progress != null && progress.type().equals(Progress.LOADINGVIEW)) {
            progress.onFailed();
        } else if (progress != null) {
            progress.hidden();
        }
    }

    public void cancelThis(String urlId) {
        if (!TextUtils.isEmpty(id) && id.equals(urlId)) {
            if (progress != null) {
                progress.setTag(null);
                progress.hidden();
            }
            DHttpUtilsList.cancelRequest(urlId);
        }
    }

    public void cancelThis() {
        if (progress != null) {
            progress.setTag(null);
            progress.hidden();
        }
        DHttpUtilsList.cancelRequest(id);
    }

    private void dismissDialog() {
        if (progress != null) {
            progress.hidden();
        }
    }

    private void showOnEmpty() {
        if (progress != null && progress.type().equals(Progress.LOADINGVIEW)) {
            progress.onDataEmpty();
        } else if (progress != null) {
            progress.hidden();
        }
    }

    public void clearParams() {
        this.fileMap = null;
        this.fileObjects = null;
        this.progress = null;
        this.params = null;
//        this.id = null;
        this.callback = null;
        this.checkLoginState = false;
    }

    public void saveAccessToken(String accessToken) {
        this.accessToken = accessToken;
        preferencesUtils.setValue(context, "ACCESS_TOKEN.key", accessToken);
    }

    public String getAccessToken() {
        String accessToken;
        accessToken = preferencesUtils.getValue(context, "ACCESS_TOKEN.key", String.class);
        if (TextUtils.isEmpty(accessToken))
            accessToken = "";
        return accessToken;
    }

    private void clearLoginInfo() {
        logMsg("移除token");
        preferencesUtils.delete(context, "DYNAMIC_TOKEN.key");
        preferencesUtils.delete(context, LOGINTOKEN);
//        preferencesUtils.delete("VIEW_ID.key");
        preferencesUtils.setValue(context, ISON, -1);
        if (functions != null) {
            functions.clear();
            functions = null;
        }
//        preferencesUtils.setObject(context, MENULIST, null);
    }

    private Map<String, String> addCommonParams(Context context, Map<String, String> params) {
        Map<String, String> map = new HashMap<String, String>();
        String dynamicToken = preferencesUtils.getValue(context, "DYNAMIC_TOKEN.key", String.class);
        logMsg("dynamicToken -- > " + dynamicToken);
        dynamicToken = (dynamicToken == null) ? "" : dynamicToken;
        map.put("DYNAMIC_TOKEN", dynamicToken);
        map.put("accessToken", getLoginToken());
        if (params == null || params.isEmpty()) {
            return map;
        }
        map.putAll(params);
        return map;
    }

    private void saveToken(BaseResponse baseResponse) {
        logMsg("替换动态token");
        if (baseResponse == null) {
            return;
        }
        String dynamicToken = baseResponse.getDYNAMIC_TOKEN();
        if (!TextUtils.isEmpty(dynamicToken)) {
            preferencesUtils.setValue(context, "DYNAMIC_TOKEN.key", dynamicToken);
        }
    }

    private void initIsonWithMenulist(boolean isOn, List<Function> functions, String entry) {
        logMsg("初始化 list entery = " + entry);
        if (isOn) {
            preferencesUtils.setValue(context, ISON, 0);
        } else {
            preferencesUtils.setValue(context, ISON, 1);
        }
        updateFunction(functions);
        if (!TextUtils.isEmpty(entry)) {
            this.entry = entry;
            preferencesUtils.setValue(context, ENTTRY, entry);
        }
    }

    public void updateFunction(List<Function> functions) {
        if (functions != null)
            if (this.functions != null) {
                this.functions.clear();
                this.functions.addAll(functions);
            } else {
                this.functions = functions;
            }
    }

    private void updateLoginToken(String token) {
        if (!TextUtils.isEmpty(token))
            loginSuccessed(token);
    }

    private boolean getIsOn(int id) {
        boolean ison;
        switch (id) {
            case 0:
                ison = true;
                break;
            case 1:
                ison = false;
                break;
            default:
                ison = false;
                break;
        }
        return ison;
    }

    void logMsg(String msg) {
        if (builder.logState) {
            LogUtil.e("TAG--" + msg);
        }
    }

    public void loginSuccessed() {
        builder.loginState = true;
    }

    public void loginSuccessed(String loginToken) {
        logMsg("登录成功-->" + loginToken);
        builder.loginState = true;
        preferencesUtils.setValue(context, LOGINTOKEN, loginToken);
    }

    public void loginFailed() {
        logMsg("登录状态失效");
        builder.loginState = false;
    }

    public void loginFailedDeleToken() {
        builder.loginState = false;
        preferencesUtils.delete(context, LOGINTOKEN);
    }

    public String getLoginToken() {
        String token = preferencesUtils.getValue(context, LOGINTOKEN, String.class);
        if (TextUtils.isEmpty(token))
            token = "";
        return token;
    }

    private boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static class Builder {
        private HashMap<String, String> menuListParam = new HashMap<>();
        private ArrayList<String> exceptionCodeList = new ArrayList<>();
        private ArrayList<String> otherCodeList = new ArrayList<>();
        private String logOutId;
        private String loginTokenId;
        private boolean loginState = false;
        private boolean logState;

        public static Builder buildConfigServer(Application context, String server, String serverRootPath) {
            DHttpExcutorList.getInstance().context = context;
            DHttpExcutorList.serverUrl = server;
            DHttpExcutorList.serverRootPath = serverRootPath;
//            DConfing.ErrorFromNet = getString(R.string.error_with_failed_to_connect_network);
//            DConfing.ErrorFromServer = getString(R.string.login_error_with_connect_failed);
//            DConfing.ErrorFromDynamic = getString(R.string.login_error_with_connect_failed);
//            DConfing.ErrorFromNoData = getString(R.string.no_data);
            DHttpExcutorList.getInstance().functions = null;
            DHttpExcutorList.getInstance().logMsg("初始化list");
            return DHttpExcutorList.builder;
        }

        private static String getString(int id) {
            return DHttpExcutorList.getInstance().context.getResources().getString(id);
        }

        public Builder logState(boolean logState) {
            DHttpExcutorList.builder.logState = logState;
            return this;
        }

        public Builder addParam(String key, String value) {
            if (!TextUtils.isEmpty(key))
                if (value != null)
                    DHttpExcutorList.builder.menuListParam.put(key, value);
            return this;
        }

        public Builder autoLogOut(String logOutId) {
            DHttpExcutorList.builder.logOutId = logOutId;
            return this;
        }

        public Builder exceptionCode(String rtnCode) {
            if (!DHttpExcutorList.builder.exceptionCodeList.contains(rtnCode))
                DHttpExcutorList.builder.exceptionCodeList.add(rtnCode);
            return this;
        }

        public Builder otherCode(String rtnCode) {
            if (!DHttpExcutorList.builder.otherCodeList.contains(rtnCode))
                DHttpExcutorList.builder.otherCodeList.add(rtnCode);
            return this;
        }

        public Builder loginTokenId(String loginTokenId) {
            DHttpExcutorList.builder.loginTokenId = loginTokenId;
            return this;
        }

        public Builder loginState(boolean loginState) {
            DHttpExcutorList.builder.loginState = loginState;
            return this;
        }
    }
}

