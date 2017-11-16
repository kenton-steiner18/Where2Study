package com.example.where2study;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewPost extends AppCompatActivity {

    private TextView location, description, starttime, endtime, userid, numseats, classname;
    private String postid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        location = findViewById(R.id.locationvalue);
        description = findViewById(R.id.descriptionvalue);
        starttime = findViewById(R.id.starttimevalue);
        endtime = findViewById(R.id.endtimevalue);
        numseats = findViewById(R.id.numseatsvalue);
        classname = findViewById(R.id.classnamevalue);
        userid = findViewById(R.id.useridvalue);


        SharedPreferences settings = getSharedPreferences("postinfo", 0);
        postid = settings.getString("userid", "");
        location.setText(settings.getString("location", ""));
        starttime.setText(settings.getString("starttime", ""));
        endtime.setText(settings.getString("endtime", ""));
        description.setText(settings.getString("description", ""));
        userid.setText(settings.getString("userid", ""));
        classname.setText(settings.getString("classname", ""));
        int numofseats = settings.getInt("numseats", 0);
        numseats.setText(Integer.toString(numofseats));

    }
}
