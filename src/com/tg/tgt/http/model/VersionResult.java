package com.tg.tgt.http.model;

import com.tg.tgt.http.BaseHttpResult;

import java.util.List;

/**
 *
 * @author yiyang
 */
public class VersionResult extends BaseHttpResult {

    private static final long serialVersionUID = -8675881373768302315L;
    /**
     * id : 1
     * remark : 修复按钮交互问题||新增聊天加密、安全畅聊、随心销毁
     * picture : http://live.qevedkc.net/time/timely.apk
     * addtime : 2017-05-26
     * version : v1.2.0
     * size : 36.1
     * cons : ["修复按钮交互问题","新增聊天加密、安全畅聊、随心销毁"]
     */

    private String id;
    private String con;
    private String url;
    private String addtime;
    private String version;
    private String size;
    private List<String> cons;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<String> getCons() {
        return cons;
    }

    public void setCons(List<String> cons) {
        this.cons = cons;
    }


}
