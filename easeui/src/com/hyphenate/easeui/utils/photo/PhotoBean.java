package com.hyphenate.easeui.utils.photo;

/**
 *
 * @author yiyang
 */
public class PhotoBean extends MediaBean {
    private static final long serialVersionUID = -5601368759251690021L;

    public PhotoBean(String path) {
        super(path);
    }
    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
