package com.forms.dhttp.http.excutor;

import android.util.Log;

import com.forms.dhttp.dbean.HttpList;
import com.forms.dhttp.http.DHttpUtilsList;
import com.forms.dhttp.http.excutor.listener.Progress;

import java.util.ArrayList;

/**
 * Created by xiaxinqing on 2017/6/27.
 * 动态流程队列管理
 */

public class Dmanager {


    private ArrayList<HttpList> httpList;
    private static Dmanager dmanager;

    private Dmanager() {
        httpList = new ArrayList<>();
    }

    public static Dmanager getInstance() {
        synchronized (Dmanager.class) {
            if (dmanager == null)
                dmanager = new Dmanager();
        }
        return dmanager;
    }

    public void addRequest(HttpList http) {
        if (httpList.size() > 0) {
            boolean canAdd = true;
            for (int i = 0; i < httpList.size(); i++) {
                HttpList https = httpList.get(i);
                if (https.getUrlId().equals(http.getUrlId()))
                    canAdd = false;
            }
            if (canAdd) {
                httpList.add(0, http);
                DHttpExcutorList.getInstance().logMsg("addRequest() called with: http = [" + http + "]" + "--" + httpList.size());
            }
        } else {
            httpList.add(0, http);
            DHttpExcutorList.getInstance().logMsg("addRequest() called with: http = [" + http + "]" + "--" + httpList.size());
        }
    }

    /**
     * 继续执行下一个
     *
     * @return true 继续下一个，false 队列执行完毕
     */
    public boolean next() {
        DHttpExcutorList.getInstance().logMsg("开始检查队列是否存在未执行请求");
        if (canNext()) {
            DHttpExcutorList.getInstance().managerNext(httpList.remove(0));
            return true;
        }
        return false;
    }

    /**
     * 继续执行下一个
     *
     * @return true 继续下一个，false 队列执行完毕|未找到改ID
     */
    public boolean next(String urlID) {
        DHttpExcutorList.getInstance().logMsg("执行-->" + urlID);
        if (canNext()) {
            for (int i = 0; i < httpList.size(); i++) {
                HttpList https = httpList.get(i);
                if (https.getUrlId().equals(urlID)) {
                    DHttpExcutorList.getInstance().managerNext(https);
                    httpList.remove(https);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 取消该请求，并且显示失败/重试框（或者隐藏）
     */
    public void removeHttp(String urlId) {
        DHttpExcutorList.getInstance().logMsg("清除指定请求-->" + urlId);
        DHttpExcutorList.getInstance().cancelThis(urlId);
        if (httpList.size() > 0) {
            for (int i = 0; i < httpList.size(); i++) {
                HttpList https = httpList.get(i);
                if (https.getUrlId().equals(urlId)) {
                    if (https.getProgress() != null) {
                        showOnFailed(https.getProgress());
                        https.getProgress().setTag(null);
                    }
                    httpList.remove(https);
                    cancelRequest(urlId);
                    break;
                }
            }
        }
    }

    /**
     * 取消队列不显示失败框
     *
     * @param urlID
     */
    public void deleteHttp(String... urlID) {
        DHttpExcutorList.getInstance().logMsg("清除指定请求-->" + urlID.toString());
        for (int i = 0; i < urlID.length; i++) {
            String url = urlID[i];
            DHttpExcutorList.getInstance().cancelThis(url);
            Log.e("TAG", "delete--start--" + url);
            if (httpList.size() > 0) {
                outer:
                for (int j = 0; j < httpList.size(); j++) {
                    HttpList https = httpList.get(j);
                    if (https.getUrlId().equals(url)) {
//                            showOnFailed(https.getProgress());
                        if (https.getProgress() != null)
                            https.getProgress().setTag(null);
                        httpList.remove(https);
                        cancelRequest(url);
                        DHttpExcutorList.getInstance().logMsg("delete--end--" + url);
                        break outer;
                    }
                }
            }
        }
    }

    /**
     * 取消所有队列请求，并且显示失败/重试框（或者隐藏）
     */
    public void deleteAllHttp() {
        DHttpExcutorList.getInstance().logMsg("开始清空队列，并且取消当前请求");
        DHttpExcutorList.getInstance().cancelThis();
        if (httpList.size() > 0) {
            for (int i = 0; i < httpList.size(); i++) {
                HttpList https = httpList.get(i);
                if (https.getProgress() != null)
                    https.getProgress().setTag(null);
                cancelRequest(https.getUrlId());
            }
            httpList.clear();
            DHttpExcutorList.getInstance().logMsg("清除队列" + httpList.size());
        }
    }

    /**
     * 取消所有队列请求，并且显示失败/重试框（或者隐藏）
     */
    public void removeAllHttp() {
        DHttpExcutorList.getInstance().logMsg("开始清空队列，并且取消当前请求");
        DHttpExcutorList.getInstance().cancelThis();
        if (httpList.size() > 0) {
            for (int i = 0; i < httpList.size(); i++) {
                HttpList https = httpList.get(i);
                if (https.getProgress() != null) {
                    showOnFailed(https.getProgress());
                    https.getProgress().setTag(null);
                }
                cancelRequest(https.getUrlId());
            }
            httpList.clear();
        }
    }

    private void showOnFailed(Progress progress) {
        if (progress != null && progress.type().equals(Progress.LOADINGVIEW)) {
            progress.onFailed();
        } else if (progress != null) {
            progress.hidden();
        }
    }

    private boolean canNext() {
        if (httpList.size() > 0) {
            DHttpExcutorList.getInstance().logMsg("队列剩余--" + (httpList.size() - 1));
            return true;
        }
        DHttpExcutorList.getInstance().logMsg("执行完毕");
        return false;
    }

    public int getHttpNum() {
        return httpList.size() > 0 ? httpList.size() : 0;
    }

    private void cancelRequest(String urlID) {
        DHttpUtilsList.cancelRequest(urlID);
    }
}
