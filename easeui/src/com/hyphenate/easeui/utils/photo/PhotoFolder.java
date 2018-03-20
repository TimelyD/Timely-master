package com.hyphenate.easeui.utils.photo;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class PhotoFolder {
    /* 文件夹名 */
    private String name;
    /* 文件夹路径 */
    private String dirPath;
    /* 文件夹首个文件地址 */
    private String path;
    /* 该文件夹下图片列表 */
    private List<MediaBean> photoList;
    /* 标识是否选中该文件夹 */
    private boolean isSelected;

    /* 文件夹中总文件数 */
    private int num;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public List<MediaBean> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<MediaBean> photoList) {
        this.photoList = photoList;
    }

    @Override
    public String toString() {
        return "PhotoFolder{" +
                "name='" + name + '\'' +
                ", dirPath='" + dirPath + '\'' +
                ", photoList=" + photoList +
                ", isSelected=" + isSelected +
                ", num=" + num +
                '}';
    }
}
