package com.tg.tgt.moment.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 *
 * @author yiyang
 */
public class CommentItem implements Serializable{
    private static final long serialVersionUID = 3379865826994879536L;
    private String id;
    private String nickname;
    private String remark;
    private String userId;
    private String username;
    private String content;
    private String parentUserId;
    private String parentNickname;
    private String parentRemark;
    private String parentUsername;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentUserId() {
        return parentUserId;
    }

    public void setParentUserId(String parentUserId) {
        this.parentUserId = parentUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String safeGetRemark() {
        return TextUtils.isEmpty(remark)?nickname:remark;
    }

    public String getParentNickname() {
        return parentNickname;
    }

    public void setParentNickname(String parentNickname) {
        this.parentNickname = parentNickname;
    }

    public String getParentRemark() {
        return parentRemark;
    }

    public String safeGetParentRemark() {
        return TextUtils.isEmpty(parentRemark)?parentNickname:parentRemark;
    }

    public void setParentRemark(String parentRemark) {
        this.parentRemark = parentRemark;
    }

    public String getParentUsername() {
        return parentUsername;
    }

    public void setParentUsername(String parentUsername) {
        this.parentUsername = parentUsername;
    }

    @Override
    public String toString() {
        return "CommentItem{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", remark='" + remark + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", parentUserId='" + parentUserId + '\'' +
                ", parentNickname='" + parentNickname + '\'' +
                ", parentRemark='" + parentRemark + '\'' +
                ", parentUsername='" + parentUsername + '\'' +
                '}';
    }
}
