package com.rahuldshetty.instacopy.models;

public class Like {

    boolean likestatus;

    Like(){

    }

    public boolean isLikestatus() {
        return likestatus;
    }


    public void setLikestatus(boolean likestatus) {
        this.likestatus = likestatus;
    }

    public Like(boolean likestatus) {
        this.likestatus = likestatus;
    }


}
