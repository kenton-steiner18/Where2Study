package com.example.where2study.Objects;


import java.sql.Time;

public class Post {

    private Time start_time, end_time;
    private String classname, description, location, postid;
    private int seats;


    public Time getStartTime() {return start_time;}

    public void setStarttime(Time start_time) {
        this.start_time = start_time;
    }

    public Time getendtime() {return end_time;}

    public void setEndtime (Time end_time) {this.end_time = end_time;  }

    public String getPostId() {
        return postid;
    }

    public void setPostId(String postid) {
        this.postid = postid;
    }

    public String getClassName() {
        return classname;
    }

    public void setClassName(String classname) {
        this.classname = classname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) { this.location = location; }

    public int getSeats() {return seats; }

    public void setSeats(int seats) { this.seats = seats; }



}

