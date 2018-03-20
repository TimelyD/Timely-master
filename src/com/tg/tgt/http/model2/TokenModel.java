package com.tg.tgt.http.model2;

/**
 *
 * @author yiyang
 */
public class TokenModel {

    /**
     * refreshToken : string
     * token : string
     */

    private String refreshToken;
    private String token;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
