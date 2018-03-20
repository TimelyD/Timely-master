package com.tg.tgt.http.model2;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 *
 * @author yiyang
 */
@Entity
public class GroupUserModel {

    /**
     * blocks : false
     * createTime : 2017-11-03T02:16:42.160Z
     * groupAdmin : false
     * groupId : 0
     * groupNickname : string
     * groupOwner : false
     * id : 0
     * mute : false
     * showNickname : false
     * updateTime : 2017-11-03T02:16:42.160Z
     * userId : 0
     */

    private boolean blocks;
    private String createTime;
    private boolean groupAdmin;
    private long groupId;
    private String groupNickname;
    private boolean groupOwner;

    private Long id;
    private boolean mute;
    private boolean showNickname;
    private String updateTime;
    @Id
    private Long userId;
    /**
     * nickname : string
     * picture : string
     * userId : 0
     * username : string
     */

    private String nickname;
    private String picture;
    private String username;
    private String initialLetter;
    @Generated(hash = 1806537986)
    public GroupUserModel(boolean blocks, String createTime, boolean groupAdmin,
            long groupId, String groupNickname, boolean groupOwner, Long id,
            boolean mute, boolean showNickname, String updateTime, Long userId,
            String nickname, String picture, String username,
            String initialLetter) {
        this.blocks = blocks;
        this.createTime = createTime;
        this.groupAdmin = groupAdmin;
        this.groupId = groupId;
        this.groupNickname = groupNickname;
        this.groupOwner = groupOwner;
        this.id = id;
        this.mute = mute;
        this.showNickname = showNickname;
        this.updateTime = updateTime;
        this.userId = userId;
        this.nickname = nickname;
        this.picture = picture;
        this.username = username;
        this.initialLetter = initialLetter;
    }
    @Generated(hash = 1540219462)
    public GroupUserModel() {
    }
    public boolean getBlocks() {
        return this.blocks;
    }
    public void setBlocks(boolean blocks) {
        this.blocks = blocks;
    }
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public boolean getGroupAdmin() {
        return this.groupAdmin;
    }
    public void setGroupAdmin(boolean groupAdmin) {
        this.groupAdmin = groupAdmin;
    }
    public long getGroupId() {
        return this.groupId;
    }
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
    public String getGroupNickname() {
        return this.groupNickname;
    }
    public void setGroupNickname(String groupNickname) {
        this.groupNickname = groupNickname;
    }
    public boolean getGroupOwner() {
        return this.groupOwner;
    }
    public void setGroupOwner(boolean groupOwner) {
        this.groupOwner = groupOwner;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean getMute() {
        return this.mute;
    }
    public void setMute(boolean mute) {
        this.mute = mute;
    }
    public boolean getShowNickname() {
        return this.showNickname;
    }
    public void setShowNickname(boolean showNickname) {
        this.showNickname = showNickname;
    }
    public String getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public Long getUserId() {
        return this.userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getPicture() {
        return this.picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getInitialLetter() {
        return this.initialLetter;
    }
    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }


}
