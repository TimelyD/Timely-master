package com.tg.tgt.http.model2;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class FriendMomentModel {

    /**
     * id : 11
     * createTime :
     * updateTime :
     * userId : 58
     * content : 12312
     * momentsType : 1
     * picture :
     * picture : [{"picture":""}]
     * commentEntities : []
     * likeEntities : []
     */

    private long id;
    private String createTime;
    private String updateTime;
    private long userId;
    private String content;
    /**
     * 动态类型 1:普通动态, 2分享
     */
    private String momentsType;
    private String url;
    private List<PictureBean> picture;
    private List<?> commentEntities;
    private List<?> likeEntities;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMomentsType() {
        return momentsType;
    }

    public void setMomentsType(String momentsType) {
        this.momentsType = momentsType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<PictureBean> getPicture() {
        return picture;
    }

    public void setPicture(List<PictureBean> picture) {
        this.picture = picture;
    }

    public List<?> getCommentEntities() {
        return commentEntities;
    }

    public void setCommentEntities(List<?> commentEntities) {
        this.commentEntities = commentEntities;
    }

    public List<?> getLikeEntities() {
        return likeEntities;
    }

    public void setLikeEntities(List<?> likeEntities) {
        this.likeEntities = likeEntities;
    }

    public static class PictureBean {
        /**
         * picture :
         */

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
