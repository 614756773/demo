package com.hotpot.demo.model;

public class UserInfo {
    /**
     * 用户名
     */
    private String login;

    /**
     * 用户id
     */
    private String id;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 位置
     */
    private String location;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
