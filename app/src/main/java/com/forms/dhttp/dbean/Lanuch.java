package com.forms.dhttp.dbean;

import com.forms.dhttp.base.RootRsp;

/**
 * Created by xiaxinqing on 2017/5/9.
 */

public class Lanuch extends RootRsp {

    private String imgId;
    private String startPageImageUrl;
    private String countDown;
    private String describe;

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getStartPageImageUrl() {
        return startPageImageUrl;
    }

    public void setStartPageImageUrl(String startPageImageUrl) {
        this.startPageImageUrl = startPageImageUrl;
    }

    public String getCountDown() {
        return countDown;
    }

    public void setCountDown(String countDown) {
        this.countDown = countDown;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "Lanuch{" +
                "imgId='" + imgId + '\'' +
                ", startPageImageUrl='" + startPageImageUrl + '\'' +
                ", countDown='" + countDown + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
