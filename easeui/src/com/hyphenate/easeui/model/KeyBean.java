package com.hyphenate.easeui.model;

import java.util.List;

/**
 * Created by DELL on 2018/7/18.
 */

public class KeyBean {
    /**
     * aesKey : string
     * chatSKey : string
     * newest : true
     * version : 0
     */

    private String aesKey;
    private String chatSKey;
    private boolean newest;
    private int version;
    private String chatPubKey;

    public String getChatPubKey() {
        return chatPubKey;
    }

    public void setChatPubKey(String chatPubKey) {
        this.chatPubKey = chatPubKey;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getChatSKey() {
        return chatSKey;
    }

    public void setChatSKey(String chatSKey) {
        this.chatSKey = chatSKey;
    }

    public boolean isNewest() {
        return newest;
    }

    public void setNewest(boolean newest) {
        this.newest = newest;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
