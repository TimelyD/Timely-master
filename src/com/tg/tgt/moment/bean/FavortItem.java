package com.tg.tgt.moment.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 *
 * @author yiyang
 */
public class FavortItem implements Serializable{
    private static final long serialVersionUID = -8466041510622359683L;

    /**
     * id : 2
     * userId : 43
     * username : yy002
     * nickname : 你大爷2
     * remark :
     */

    private long id;
    private long userId;
    private String username;
    private String nickname;
    private String remark;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String safeGetRemark() {
        return TextUtils.isEmpty(remark)?nickname:remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "FavortItem{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
