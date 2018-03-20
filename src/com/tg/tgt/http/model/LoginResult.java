package com.tg.tgt.http.model;

import com.tg.tgt.http.BaseHttpResult;

/**
 *
 * @author yiyang
 */
public class LoginResult extends BaseHttpResult {

    private static final long serialVersionUID = -5703861914920209371L;
    /**
     * uid : 41
     * chatid : 13412312312
     * sex : 男
     * password : 4297f44b13955235245b2497399d7a93
     * nickname : 13412312312
     * infocode : 0
     * state :
     * cover : http://live.qevedkc.net/time/public/head.png
     */

    private String uid;
    private String chatid;
    private String sex;
    private String password;
    private String nickname;
    private String infocode;
    private String state;
    private String cover;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "uid='" + uid + '\'' +
                ", chatid='" + chatid + '\'' +
                ", sex='" + sex + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", infocode='" + infocode + '\'' +
                ", state='" + state + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
