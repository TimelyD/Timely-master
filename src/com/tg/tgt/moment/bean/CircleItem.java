package com.tg.tgt.moment.bean;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.tg.tgt.App;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author yiyang
 */
public class CircleItem implements MultiItemEntity, Serializable {
    public final static int TYPE_URL = 2;
    public final static int TYPE_IMG = 1;
    public final static int TYPE_AD = 3;
    public final static int TYPE_NEWS = 4;
    private static final long serialVersionUID = -6100804733440249589L;

    private String id;
    private String userId;
    private String username;
    private String nickname;
    private String remark;
    private String picture;

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    private String content;
    private String createTime;
    private String updateTime;
    private int momentsType;//1:链接  2:图片 3:视频
    private String url;
    private String urlPicture;
    private String urlTitle;
    //pictureEntities首张图片宽高
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<PhotoInfo> getPictureEntities() {
        return pictureEntities;
    }

    public void setPictureEntities(List<PhotoInfo> pictureEntities) {
        this.pictureEntities = pictureEntities;
    }

    private List<PhotoInfo> pictureEntities;
    private List<FavortItem> likeModels;
    private List<CommentItem> commentModels;
    private String videoUrl;
    private String videoImgUrl;
    private boolean isExpand;
    private boolean like;

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return momentsType;
    }

    public void setType(int type) {
        this.momentsType = type;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getUrlTitle() {
        return urlTitle;
    }

    public void setUrlTitle(String urlTitle) {
        this.urlTitle = urlTitle;
    }

    public List<FavortItem> getLikeModels() {
        return likeModels;
    }

    public void setLikeModels(List<FavortItem> likeModels) {
        this.likeModels = likeModels;
    }

    public List<CommentItem> getCommentModels() {
        return commentModels;
    }

    public void setCommentModels(List<CommentItem> commentModels) {
        this.commentModels = commentModels;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoImgUrl() {
        return videoImgUrl;
    }

    public void setVideoImgUrl(String videoImgUrl) {
        this.videoImgUrl = videoImgUrl;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public boolean hasFavort(){
        if(likeModels !=null && likeModels.size()>0){
            return true;
        }
        return false;
    }

    public boolean hasComment(){
        if(commentModels !=null && commentModels.size()>0){
            return true;
        }
        return false;
    }

    public String getCurUserFavortId(){
        String favortid = "";
        String curUserId = App.getMyUid();
        if(!TextUtils.isEmpty(curUserId) && hasFavort()){
            for(FavortItem item : likeModels){
                if(curUserId.equals(String.valueOf(item.getUserId()))){
                    favortid = String.valueOf(item.getId());
                    return favortid;
                }
            }
        }
        return favortid;
    }

    public boolean isCurUserFavor(){
        return TextUtils.isEmpty(getCurUserFavortId());
    }

    @Override
    public int getItemType() {
        return getType();
    }

    @Override
    public String toString() {
        return "CircleItem{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", remark='" + remark + '\'' +
                ", picture='" + picture + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", momentsType=" + momentsType +
                ", url='" + url + '\'' +
                ", urlPicture='" + urlPicture + '\'' +
                ", urlTitle='" + urlTitle + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", pictureEntities=" + pictureEntities +
                ", likeModels=" + likeModels +
                ", commentModels=" + commentModels +
                ", videoUrl='" + videoUrl + '\'' +
                ", videoImgUrl='" + videoImgUrl + '\'' +
                ", isExpand=" + isExpand +
                ", like=" + like +
                '}';
    }
}
