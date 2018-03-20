package com.tg.tgt.http.model2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @author yiyang
 */
public class NewsModel implements Parcelable {

    /**
     * author : string
     * brief : string
     * content : string
     * createTime : 2017-11-27T03:14:02.431Z
     * height : 0
     * id : 0
     * newType : 0
     * picture : string
     * title : string
     * width : 0
     */


    private String author;
    /**简介*/
    private String brief;
    private String content;
    private String createTime;
    private int height;
    private String id;
    /**1:常规,2:分享,3广告*/
    private String newType;
    private String picture;
    private String title;
    private int width;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.brief);
        dest.writeString(this.content);
        dest.writeString(this.createTime);
        dest.writeInt(this.height);
        dest.writeString(this.id);
        dest.writeString(this.newType);
        dest.writeString(this.picture);
        dest.writeString(this.title);
        dest.writeInt(this.width);
    }

    public NewsModel() {
    }

    protected NewsModel(Parcel in) {
        this.author = in.readString();
        this.brief = in.readString();
        this.content = in.readString();
        this.createTime = in.readString();
        this.height = in.readInt();
        this.id = in.readString();
        this.newType = in.readString();
        this.picture = in.readString();
        this.title = in.readString();
        this.width = in.readInt();
    }

    public static final Parcelable.Creator<NewsModel> CREATOR = new Parcelable.Creator<NewsModel>() {
        @Override
        public NewsModel createFromParcel(Parcel source) {
            return new NewsModel(source);
        }

        @Override
        public NewsModel[] newArray(int size) {
            return new NewsModel[size];
        }
    };
}
