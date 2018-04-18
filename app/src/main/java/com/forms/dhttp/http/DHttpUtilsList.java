package com.forms.dhttp.http;

import android.text.TextUtils;
import android.util.Log;

import com.forms.okhttplibrary.builder.PostMultipartBuilder;
import com.forms.okhttplibrary.callback.DownloadFileCallback;
import com.forms.okhttplibrary.callback.PostMultipleCallback;
import com.forms.okhttplibrary.callback.PostParamsCallback;
import com.forms.okhttplibrary.exception.HttpException;
import com.forms.okhttplibrary.util.HttpUtil;
import com.forms.dhttp.config.DConfing;
import com.forms.dhttp.http.excutor.DHttpCallBack;
import com.forms.dhttp.http.excutor.DHttpExcutorList;
import com.forms.dhttp.http.excutor.listener.Progress;
import com.forms.dhttp.utils.LogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;

/**
 * http动态url
 *
 * @author 夏新庆
 */
public class DHttpUtilsList {

    public static void sendRequest(String url, Object httpTag, Map<String, String> params, final HttpUtilsCallBak callBack) {
        LogUtil.e("called with:  url = [" + url + "], httpTag = [" + httpTag + "], params = [" + params + "]");
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Accept", "application/vnd.captech-v1.0+json");
        headMap.put("content-type", "charset=utf-8");
        headMap.put("ACCESS-TOKEN", DHttpExcutorList.getInstance().getAccessToken());
        postParam(url, httpTag, params, callBack, headMap);
    }

    private static void postParam(String url, Object httpTag, Map<String, String> params, final HttpUtilsCallBak callBack, Map<String, String> heardMap) {
        HttpUtil.getInstance().postParamsRequest(url, heardMap, params, httpTag, new PostParamsCallback() {
            @Override
            public void onSuccess(Call call, String s) {
                if (!TextUtils.isEmpty(s))
                    callBack.onSuccess(s);//成功
                else
                    callBack.onError(DConfing.ErrorFromNoData, s);
            }

            @Override
            public void onError(Call call, String s) {//连上了 ，但是失败了
                callBack.onError(DConfing.ErrorFromServer, s);
            }

            @Override
            public void getHeader(Headers headers) {
                String accessToken = headers.get("ACCESS-TOKEN");
                Log.e("TAG", "TOKEN--" + accessToken);
                if (accessToken != null)
                    DHttpExcutorList.getInstance().saveAccessToken(accessToken);
            }

            @Override
            public void onFailed(Call call, String s) {//没连接到服务器
                callBack.onError(DConfing.ErrorFromServer, s);
            }
        });
    }

    public static void sendRequestWithFile(String url,
                                           Map<String, String> params, Map<String, File> fileMap, Object tag,
                                           final HttpUtilsCallBak callBack) {
        Map<String, String> heardMap = new HashMap<>();
        heardMap.put("Accept", "application/vnd.captech-v1.0+json");
        heardMap.put("content-type", "charset=utf-8");
        heardMap.put("ACCESS-TOKEN", DHttpExcutorList.getInstance().getAccessToken());
        HttpUtil.getInstance().postMultipartRequest(url, heardMap, params, fileMap, tag, new PostMultipleCallback() {
            @Override
            public void onSuccess(Call call, String s) {
                if (!TextUtils.isEmpty(s))
                    callBack.onSuccess(s);//成功
                else
                    callBack.onError(DConfing.ErrorFromNoData, s);
            }

            @Override
            public void onError(Call call, String s) {//连上了 ，但是失败了
                callBack.onError(DConfing.ErrorFromServer, s);
            }

            @Override
            public void getHeader(Headers headers) {
                String accessToken = headers.get("ACCESS-TOKEN");
                Log.e("TAG", "TOKEN--" + accessToken);
                if (accessToken != null)
                    DHttpExcutorList.getInstance().saveAccessToken(accessToken);
            }

            @Override
            public void onFailed(Call call, String s) {//没连接到服务器
                callBack.onError(DConfing.ErrorFromServer, s);
            }
        });
    }

    public static void sendRequestWithFile(String url,
                                           Map<String, String> params, List<PostMultipartBuilder.FileObject> fileObjects, Object tag,
                                           final HttpUtilsCallBak callBack) {
        Map<String, String> heardMap = new HashMap<>();
        heardMap.put("Accept", "application/vnd.captech-v1.0+json");
        heardMap.put("content-type", "charset=utf-8");
        heardMap.put("ACCESS-TOKEN", DHttpExcutorList.getInstance().getAccessToken());
        HttpUtil.getInstance().postMultipartRequest(url, heardMap, params, fileObjects, tag, new PostMultipleCallback() {
            @Override
            public void onSuccess(Call call, String s) {
                if (!TextUtils.isEmpty(s))
                    callBack.onSuccess(s);//成功
                else
                    callBack.onError(DConfing.ErrorFromNoData, s);
            }

            @Override
            public void onError(Call call, String s) {//连上了 ，但是失败了
                callBack.onError(DConfing.ErrorFromServer, s);
            }

            @Override
            public void getHeader(Headers headers) {
                String accessToken = headers.get("ACCESS-TOKEN");
                Log.e("TAG", "TOKEN--" + accessToken);
                if (accessToken != null)
                    DHttpExcutorList.getInstance().saveAccessToken(accessToken);
            }

            @Override
            public void onFailed(Call call, String s) {//没连接到服务器
                callBack.onError(DConfing.ErrorFromServer, s);
            }
        });
    }

    public static void downloadFileBase(String url, String fileDir, String fileName, final Progress progress, final DHttpCallBack callBack) {
        if (progress != null)
            progress.showProgress();
        Map<String, String> heardMap = new HashMap<>();
        heardMap.put("Accept", "application/vnd.captech-v1.0+json");
        heardMap.put("content-type", "charset=utf-8");
        heardMap.put("ACCESS-TOKEN", DHttpExcutorList.getInstance().getAccessToken());
        HttpUtil.getInstance().downloadFileRequest(url, heardMap, url, new DownloadFileCallback(fileDir, fileName) {
            @Override
            public void onSuccess(Call call, File file) {
                if (progress != null)
                    progress.hidden();
                if (file != null)
                    callBack.onSuccess(file);
                else
                    callBack.onError(call.request().toString(), null);
            }

            @Override
            public void onError(Call call, String s) {
                if (progress != null)
                    if (progress.type().equals(Progress.LOADINGVIEW))
                        progress.onFailed();
                    else
                        progress.hidden();
                callBack.onError(call.request().toString(), s);
            }

            @Override
            public void getHeader(Headers headers) {
                String accessToken = headers.get("ACCESS-TOKEN");
                Log.e("TAG", "TOKEN--" + accessToken);
                if (accessToken != null)
                    DHttpExcutorList.getInstance().saveAccessToken(accessToken);
            }

            @Override
            public void onProgress(int progress) {
                super.onProgress(progress);
                callBack.onProgress(progress);
            }

            @Override
            public void onFailed(Call call, String s) {
                if (progress != null)
                    if (progress.type().equals(Progress.LOADINGVIEW))
                        progress.onFailed();
                    else
                        progress.hidden();
                callBack.onError(call.request().toString(), s);
            }
        });
    }

    public static void cancelRequest(String urlID) {
        try {
            HttpUtil.getInstance().cancelRequest(urlID);
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }
}