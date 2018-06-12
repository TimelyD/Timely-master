package com.tg.tgt.http.model2;

/**
 *
 * @author yiyang
 */
public class LoginModel {

    /**
     * address : string
     * age : 0
     * easemob : string
     * nickname : string
     * picture : string
     * qrCode : string
     * refreshToken : string
     * safePassword : string
     * sex : MEN
     * signature : string
     * signatureId : 0
     * token : string
     * userId : 0
     */

    private String address;
    private int age;
    private String easemob;
    private String nickname;
    private String picture;
    private String qrCode;
    private String refreshToken;
    private String safePassword;
    private String sex;
    private String signature;
    private long signatureId;
    private String token;
    private String userId;
    private String sn;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEasemob() {
        return easemob;
    }

    public void setEasemob(String easemob) {
        this.easemob = easemob;
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getSafePassword() {
        return safePassword;
    }

    public void setSafePassword(String safePassword) {
        this.safePassword = safePassword;
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

    public long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(long signatureId) {
        this.signatureId = signatureId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
