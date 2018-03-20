package com.tg.tgt.http;

import java.io.Serializable;

/**
 *
 * @author yiyang
 */
public class BaseHttpResult implements Serializable{
    private static final long serialVersionUID = -541770978308896116L;
    /**
     * error : false
     * results : []
     */

    protected int msg_code;

    public int getMsg_code() {
        return msg_code;
    }

    public void setMsg_code(int msg_code) {
        this.msg_code = msg_code;
    }


    protected String tocon;

    public String getTocon() {
        return tocon;
    }

    public void setTocon(String tocon) {
        this.tocon = tocon;
    }
    //    private int code;
//    private String message;

    @Override
    public String toString() {
        return "BaseHttpResult{" +
                "msg_code=" + msg_code +
                ", tocon='" + tocon + '\'' +
                '}';
    }
//    private T content;
//
//    public T getContent() {
//        return content;
//    }
//
//    public void setContent(T content) {
//        this.content = content;
//    }
//
//
//
//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }


}
