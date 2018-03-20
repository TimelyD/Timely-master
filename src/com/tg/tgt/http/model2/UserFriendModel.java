package com.tg.tgt.http.model2;

/**
 *
 * @author yiyang
 */
public class UserFriendModel {

    /**
     * email : string
     * id : 0
     * nickname : string
     * picture : string
     * remark : string
     * safe : false
     * sex : 1
     * signature : string
     * sn : string
     */

    private String email;
    private String id;
    private String nickname;
    private String picture;
    private String remark;
    private boolean safe;
    private String sex;
    private String signature;
    private String sn;
    /**
     * 关系状态 0:无关系 1:好友, 2:黑名单, 3:拒绝好友 = ['0', '1', '2', '3']stringEnum:"0", "1", "2", "3",
     */
    private String relationStatus;

    public String getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(String relationStatus) {
        this.relationStatus = relationStatus;
    }
    //只用在解析好友添加处理部分

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private String reason;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
