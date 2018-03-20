package com.tg.tgt.http.model;

import com.tg.tgt.http.BaseHttpResult;

/**
 *
 * @author yiyang
 */
public class IsCodeResult extends BaseHttpResult {

    private static final long serialVersionUID = 378521991352466716L;
    /**
     * chatidsex : 男
     * chatidstate :
     * chatidcover : http://live.qevedkc.net/time/public/head.png
     * chatidnickname : 13412312312
     * chatid : 13412312312
     * infocode : null
     * iscode : 0
     */

    //备注
    private String con;
    //对方对我的备注
    private String con2;
    //0没有备注1备注过
    private int type;

    public IsCodeResult() {
    }

    public IsCodeResult(String con, String chatidcover, String chatidnickname, String chatid, int iscode) {
        this.con = con;
        this.chatidcover = chatidcover;
        this.chatidnickname = chatidnickname;
        this.chatid = chatid;
        this.iscode = iscode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getCon2() {
        return con2;
    }

    public void setCon2(String con2) {
        this.con2 = con2;
    }

    private String chatidsex;
    private String chatidstate;
    private String chatidcover;
    private String chatidnickname;
    private String chatid;
    private String infocode;
    private int iscode;

    public String getChatidsex() {
        return chatidsex;
    }

    public void setChatidsex(String chatidsex) {
        this.chatidsex = chatidsex;
    }

    public String getChatidstate() {
        return chatidstate;
    }

    public void setChatidstate(String chatidstate) {
        this.chatidstate = chatidstate;
    }

    public String getChatidcover() {
        return chatidcover;
    }

    public void setChatidcover(String chatidcover) {
        this.chatidcover = chatidcover;
    }

    public String getChatidnickname() {
        return chatidnickname;
    }

    public void setChatidnickname(String chatidnickname) {
        this.chatidnickname = chatidnickname;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public int getIscode() {
        return iscode;
    }

    public void setIscode(int iscode) {
        this.iscode = iscode;
    }

    @Override
    public String toString() {
        return "IsCodeResult{" +
                "remark='" + con + '\'' +
                ", type=" + type +
                ", chatidsex='" + chatidsex + '\'' +
                ", chatidstate='" + chatidstate + '\'' +
                ", chatidcover='" + chatidcover + '\'' +
                ", chatidnickname='" + chatidnickname + '\'' +
                ", chatid='" + chatid + '\'' +
                ", infocode='" + infocode + '\'' +
                ", iscode=" + iscode +
                '}';
    }
}
