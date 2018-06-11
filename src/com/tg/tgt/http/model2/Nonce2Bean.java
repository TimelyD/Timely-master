package com.tg.tgt.http.model2;

/**
 * Created by DELL on 2018/6/8.
 */

public class Nonce2Bean {

    /**
     * code : 0
     * msg : ok
     * data : {"value":"35eccc25b0104b2c81ab49d8ab9662c8","key":"8e16378f98c74b4dbcc67051e970fca1"}
     */

    private String code;
    private String msg;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * value : 35eccc25b0104b2c81ab49d8ab9662c8
         * key : 8e16378f98c74b4dbcc67051e970fca1
         */

        private String value;
        private String key;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
