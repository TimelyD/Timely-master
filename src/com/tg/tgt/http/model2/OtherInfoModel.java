package com.tg.tgt.http.model2;

/**
 *
 * @author yiyang
 */
public class OtherInfoModel {

    /**
     * email : string
     * id : 0
     * nickname : string
     * picture : string
     * remark : string
     * safe : false
     * sex : MEN
     * signature : string
     * sn : string
     * relationStatus : RELATION_NOT
     */

    private String email;
    private long id;
    private String nickname;
    private String picture;
    private String remark;
    private boolean safe;
    private String sex;
    private String signature;
    private String sn;
    private String relationStatus;

    public String getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(String relationStatus) {
        this.relationStatus = relationStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
