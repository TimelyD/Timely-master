package com.tg.tgt.moment.bean;

import java.util.List;

/**
 * Created by DELL on 2018/7/7.
 */

public class PicBean {
    /**
     * id : 57
     * createTime : null
     * updateTime : null
     * momentsId : null
     * picture : http://192.168.2.78:9998/group1/M00/00/19/wKgCTlp3y9KANFGYAAAN7wesN00184.gif
     * thumbnail : http://192.168.2.78:9998/group1/M00/00/19/wKgCTlp3y9KANFGYAAAN7wesN00184_150x150.gif
     */

    private int id;
    private Object createTime;
    private Object updateTime;
    private Object momentsId;
    private String picture;
    private String thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Object createTime) {
        this.createTime = createTime;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }

    public Object getMomentsId() {
        return momentsId;
    }

    public void setMomentsId(Object momentsId) {
        this.momentsId = momentsId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
