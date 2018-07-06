package com.tg.tgt.moment.bean;

import java.io.Serializable;

/**
 * Created by DELL on 2018/7/5.
 */

public class CollectBean implements Serializable {
    /**
     * content : string
     * createTime : 2018-07-05T14:21:32.093Z
     * crtTime : 2018-07-05T14:21:32.093Z
     * filePath : string
     * id : 0
     * isFrom : string
     * picturePath : string
     * remark : string
     * type : 0
     * updateTime : 2018-07-05T14:21:32.093Z
     * userId : 0
     */

    private String content;
    private String createTime;
    private String crtTime;
    private String filePath;
    private int id;
    private String isFrom;
    private String picturePath;
    private String remark;
    private int type;
    private String updateTime;
    private int userId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(String crtTime) {
        this.crtTime = crtTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsFrom() {
        return isFrom;
    }

    public void setIsFrom(String isFrom) {
        this.isFrom = isFrom;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CollectBean{" +
                "content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", crtTime='" + crtTime + '\'' +
                ", filePath='" + filePath + '\'' +
                ", id=" + id +
                ", isFrom='" + isFrom + '\'' +
                ", picturePath='" + picturePath + '\'' +
                ", remark='" + remark + '\'' +
                ", type=" + type +
                ", updateTime='" + updateTime + '\'' +
                ", userId=" + userId +
                '}';
    }
}
