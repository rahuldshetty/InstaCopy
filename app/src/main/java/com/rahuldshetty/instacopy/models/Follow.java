package com.rahuldshetty.instacopy.models;

public class Follow {

    String status;

    // status - follow / not following

    public Follow(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Follow(String status) {
        this.status = status;
    }
}
