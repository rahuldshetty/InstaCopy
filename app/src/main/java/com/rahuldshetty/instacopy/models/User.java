package com.rahuldshetty.instacopy.models;

public class User {

    public User(){

    }

    public User(String name, String email, String username, String photourl, String desc) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.photourl = photourl;
        this.desc = desc;
    }

    String name,email,username,photourl,desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
