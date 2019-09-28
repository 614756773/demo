package com.hotpot.demo.model;


/**
 * github返回的认证结果
 */
public class GithubToken {
    private String accessToken;

    private String scope;

    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * @param str 格式形如：access_token=370784e6ae7c9cd86d0faad2b2038153a705c240&scope=&token_type=bearer
     */
    public static GithubToken buildByString (String str) {
        GithubToken result = new GithubToken();
        String[] split = str.split("=");
        result.setAccessToken(split[1].substring(0, split[1].indexOf("&")));
        result.setScope(split[2].substring(0, split[2].indexOf("&")));
        result.setTokenType(split[3]);
        return result;
    }
}
