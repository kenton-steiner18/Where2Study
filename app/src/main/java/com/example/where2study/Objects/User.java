package com.example.where2study.Objects;


public class User {

    public static String username;
    public static String email;
    public static String userid;
    public static String fname;
    public static String lname;

    public User(String username, String email, String userid) {
        User.email = email;
        User.username = username;
        User.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getUserid() {
        return userid;
    }

    public static void setUserid(String userid) {
        User.userid = userid;
    }





}

