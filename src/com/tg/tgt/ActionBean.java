package com.tg.tgt;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 *
 * @author yiyang
 */
@Entity
public class ActionBean {

    public static final String TYPE_LIKE = "LIKE";
    public static final String TYPE_COMMENT = "COMMENT";
    /**
     * type : COMMENT
     * id : 43
     * username : yy002
     * nickname : 你大爷2
     * picture : http://192.168.2.78:9998/group1/M00/00/01/wKgCTlny6KqAbwK6AAFHEig1f8w892.jpg
     * momentId :
     * content :
     * title :
     * pic :
     */

    private String type;
    private long id;
    private String username;
    private String nickname;
    private String picture;
    /**动态编号*/
    private String momentsId;
    /**附加信息,比如评论的内容*/
    private String reason;
    /**标题,如果momentsPicture存在则显示momentsPicture*/
    private String title;
    /**动态图片*/
    private String momentsPicture;

    /**用来判断该动态是否被读过*/
    private boolean isRead;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Generated(hash = 1322067442)
    public ActionBean(String type, long id, String username, String nickname, String picture,
            String momentsId, String reason, String title, String momentsPicture,
            boolean isRead) {
        this.type = type;
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.picture = picture;
        this.momentsId = momentsId;
        this.reason = reason;
        this.title = title;
        this.momentsPicture = momentsPicture;
        this.isRead = isRead;
    }

    @Generated(hash = 649937523)
    public ActionBean() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMomentsId() {
        return momentsId;
    }

    public void setMomentsId(String momentsId) {
        this.momentsId = momentsId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMomentsPicture() {
        return momentsPicture;
    }

    public void setMomentsPicture(String momentsPicture) {
        this.momentsPicture = momentsPicture;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
