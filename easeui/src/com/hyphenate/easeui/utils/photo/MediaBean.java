package com.hyphenate.easeui.utils.photo;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 *
 * @author yiyang
 */
public class MediaBean implements Comparable<MediaBean>, Serializable{
    private static final long serialVersionUID = -7813072627545017349L;
    private int id;
    private String msg_id;
    private String path;  //路径

    private long dataModify;

    /**当图片选择后，索引值*/
    public int selectPosition;
    /**当前图片在列表中顺序*/
    public int position;

    public long getDataModify() {
        return dataModify;
    }

    public void setDataModify(long dataModify) {
        this.dataModify = dataModify;
    }

    public MediaBean(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    @Override
    public int compareTo(@NonNull MediaBean another) {
        return (int) (another.dataModify-dataModify);
    }
}
