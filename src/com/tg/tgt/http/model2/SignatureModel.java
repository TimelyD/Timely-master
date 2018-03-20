package com.tg.tgt.http.model2;

/**
 *
 * @author yiyang
 */
public class SignatureModel {
    /**
     * createTime : 2017-10-27T03:52:39.625Z
     * id : 0
     * signature : string
     * updateTime : 2017-10-27T03:52:39.625Z
     * userId : 0
     */

    private String createTime;
    private long id;
    private String signature;
    private String updateTime;
    private long userId;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
