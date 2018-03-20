package com.tg.tgt.http.model;

import com.tg.tgt.http.BaseHttpResult;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author yiyang
 */
public class SearchResult extends BaseHttpResult {


    private static final long serialVersionUID = 6016219826958512416L;
    /**
     * total : 6
     * search : [{"id":"42","nickname":"hgcc","repassword":"0","password":"4297f44b13955235245b2497399d7a93",
     * "state":"","sex":"男","country":"","province":"","city":"","address":"","cover":"http://live.qevedkc
     * .net/time/public/user/15e66505a1d3f08fc279ba81d92b48df.jpg","email":"","mobile":"","addtime":"1495012147",
     * "age":"未设置","lang":"0","chatid":"13412312311","infocode":"0"},{"id":"41","nickname":"jdjnd",
     * "repassword":"4297f44b13955235245b2497399d7a93","password":"4297f44b13955235245b2497399d7a93",
     * "state":"all饿了饿了龙","sex":"男","country":"","province":"","city":"","address":"","cover":"http://live.qevedkc
     * .net/time/public/user/0e1ba52f017d6c2e03ae4df6355bda3d.png","email":"","mobile":"","addtime":"1494769596",
     * "age":"未设置","lang":"0","chatid":"13412312312","infocode":"111111"},{"id":"37","nickname":"13427388336",
     * "repassword":"0","password":"4297f44b13955235245b2497399d7a93","state":"","sex":"男","country":"",
     * "province":"","city":"","address":"","cover":"http://live.qevedkc
     * .net/time/public/user/94b36a6486c96e2f3dfc481f0d9d4b96.jpg","email":"","mobile":"","addtime":"1494666483",
     * "age":"未设置","lang":"0","chatid":"13427388336","infocode":"111111"},{"id":"36","nickname":"zhu110",
     * "repassword":"0","password":"e10adc3949ba59abbe56e057f20f883e","state":"","sex":"男","country":"",
     * "province":"","city":"","address":"","cover":"http://live.qevedkc
     * .net/time/public/user/eaa3330082de005c6517d6a5e6422704.png","email":"","mobile":"","addtime":"1494657581",
     * "age":"未设置","lang":"0","chatid":"13823715991","infocode":"0"},{"id":"35","nickname":"13832715991",
     * "repassword":"0","password":"e10adc3949ba59abbe56e057f20f883e","state":"","sex":"男","country":"",
     * "province":"","city":"","address":"","cover":"http://live.qevedkc.net/time/public/head.png","email":"",
     * "mobile":"","addtime":"1494657180","age":"未设置","lang":"0","chatid":"13832715991","infocode":"0"},{"id":"9",
     * "nickname":"zhu119","repassword":"0","password":"e10adc3949ba59abbe56e057f20f883e","state":"宝宝苦但不说","sex":"女",
     * "country":null,"province":null,"city":null,"address":null,"cover":"http://live.qevedkc
     * .net/time/public/user/091bcdc96f03016ff5f7fe1e621328f5.png","email":null,"mobile":null,"addtime":"1493389720",
     * "age":"未设置","lang":"0","chatid":"13918275991","infocode":"777777"}]
     */

    private int total;
    private List<SearchBean> search;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SearchBean> getSearch() {
        return search;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "total='" + total + '\'' +
                ", search=" + search +
                '}';
    }

    public void setSearch(List<SearchBean> search) {
        this.search = search;
    }

    public static class SearchBean implements Serializable{
        private static final long serialVersionUID = -4845142920730077970L;
        /**
         * id : 42
         * nickname : hgcc
         * repassword : 0
         * password : 4297f44b13955235245b2497399d7a93
         * state :
         * sex : 男
         * country :
         * province :
         * city :
         * address :
         * cover : http://live.qevedkc.net/time/public/user/15e66505a1d3f08fc279ba81d92b48df.jpg
         * email :
         * mobile :
         * addtime : 1495012147
         * age : 未设置
         * lang : 0
         * chatid : 13412312311
         * infocode : 0
         */

        private String id;
        private String nickname;
        private String repassword;
        private String password;
        private String state;
        private String sex;
        private String country;
        private String province;
        private String city;
        private String address;
        private String cover;
        private String email;
        private String mobile;
        private String addtime;
        private String age;
        private String lang;
        private String chatid;
        private String infocode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getRepassword() {
            return repassword;
        }

        public void setRepassword(String repassword) {
            this.repassword = repassword;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
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
    }
}
