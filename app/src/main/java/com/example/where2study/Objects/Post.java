package com.example.where2study.Objects;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Post {

    private String classname, description, location, end_time, time_of_post, date_of_post, userid, postid;
    private int seats;
    private long millis = new Date().getTime();

    public Post() { }

    public Post(String name_of_class, String details, String place, String end, int seats, String user) {
        String currentTime = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.US).format(millis);
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        this.classname = name_of_class;
        this.description = details;
        this.location = place;
        this.end_time = end;
        this.seats = seats;
        this.time_of_post = currentTime;
        this.date_of_post = formattedDate;
        this.userid = user;
    }

    public String getPostid() { return postid; }

    public void setPostid(String postid) {this.postid = postid; }

    public String getUser() { return userid; }

    public void setUser(String user) { this.userid = user; }

    public String getDate() { return date_of_post;}

    public void setDate(String date) {this.date_of_post = date;}

    public String getTheTime() { return time_of_post;}

    public void setTheTime(String time) {this.time_of_post = time;}

    public String getEndTime() {return end_time;}

    public void setEndTime (String end_time) {this.end_time = end_time;  }

    public String getClassName() { return classname; }

    public void setClassName(String classname) {this.classname = classname;}

    public String getDescription() {return description; }

    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public int getSeats() {return seats; }

    public void setSeats(int seats) { this.seats = seats; }
}

