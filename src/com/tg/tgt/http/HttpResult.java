package com.tg.tgt.http;

/**
 *
 * @author yiyang
 */
public class HttpResult<T> {

    /**
     * code : 0
     * msg : ok
     * data :
     */

    private int code;
    private String msg;
    private String dfsfileaccessprefix;
    private T data;

    public String getDfsfileaccessprefix() {
        return dfsfileaccessprefix;
    }

    public void setDfsfileaccessprefix(String dfsfileaccessprefix) {
        this.dfsfileaccessprefix = dfsfileaccessprefix;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
