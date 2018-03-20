package com.tg.tgt.http.model2;

import android.text.TextUtils;

/**
 *
 * @author yiyang
 */
public class MomentsLogModel {

    /**
     * createTime : 2017-11-24T08:30:55.523Z
     * fromId : 0
     * fromName : string
     * id : 0
     * relationStatus : 0
     * remark : string
     */

    private String createTime;
    private String fromId;
    private String fromName;
    private String id;
    /**关系状态 0:无关系 1:好友, 2:黑名单, 3:拒绝好友*/
    private String relationStatus;
    private String remark;
    private String nickname;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    private String picture;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(String relationStatus) {
        this.relationStatus = relationStatus;
    }

    public String getRemark() {
        return remark;
    }
    public String safeGetRemark() {
        return TextUtils.isEmpty(remark)?nickname:remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
