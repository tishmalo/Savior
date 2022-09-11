package com.example.savior.Model;

public class UserModel {

    private String email, userid;

    public UserModel() {
    }

    public UserModel(String email, String userid) {
        this.email = email;
        this.userid = userid;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getuserid() {
        return userid;
    }

    public void setuserid(String userid) {
        this.userid = userid;
    }
}
