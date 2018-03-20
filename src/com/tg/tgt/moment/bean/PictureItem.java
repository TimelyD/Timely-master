package com.tg.tgt.moment.bean;

import java.io.Serializable;

/**
 *
 * @author yiyang
 */
public class PictureItem implements Serializable{
    private static final long serialVersionUID = 8290088924514054838L;
    private String picture;

    @Override
    public String toString() {
        return "PictureItem{" +
                "picture='" + picture + '\'' +
                '}';
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
