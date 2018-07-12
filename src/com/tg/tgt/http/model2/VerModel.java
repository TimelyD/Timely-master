package com.tg.tgt.http.model2;

/**
 *
 * @author yiyang
 */
public class VerModel {

    /**
     * content : string
     * contentEn : string
     * createTime : 2017-11-27T03:14:02.621Z
     * id : 0
     * platform : 1
     * size : string
     * status : 0
     * updateTime : 2017-11-27T03:14:02.621Z
     * url : string
     * version : string
     */

    private String content;
    private String contentEn;
    private String createTime;
    private String id;
    private String platform;
    private String size;
    private String status;
    private String updateTime;
    private String url;
    private String version;
    private int isUpdate;//0不强制，1强制

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentEn() {
        return contentEn;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
