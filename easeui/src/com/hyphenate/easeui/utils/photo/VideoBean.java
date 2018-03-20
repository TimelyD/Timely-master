package com.hyphenate.easeui.utils.photo;

/**
 *
 * @author yiyang
 */
public class VideoBean extends MediaBean {
    public VideoBean(String path) {
        super(path);
    }
    public VideoBean(String path, String thumbPath, long length) {
        super(path);
        this.thumbPath = thumbPath;
        this.length = length;
    }

    private String thumbPath;

    private long length;

    private long size;

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
