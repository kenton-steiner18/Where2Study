package com.example.where2study;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.where2study.Objects.Post;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.where2study.Objects.User.userid;


public class NewPost extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "NEWPOST";
    //int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public FirebaseAuth auth;
    public FirebaseUser currentuser;

    public String class_name, description, location, end_time, postid;
    public int seats;

    public TextView num_seats, study_loc, study_class, study_desc, study_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        auth = FirebaseAuth.getInstance();
        currentuser = auth.getCurrentUser();

        findViewById(R.id.cancel_post).setOnClickListener(this);
        findViewById(R.id.create_post).setOnClickListener(this);
        num_seats = findViewById(R.id.number_seats);
        study_loc = findViewById(R.id.location);
        study_class = findViewById(R.id.class_name);
        study_desc = findViewById(R.id.study_desc);
        study_end = findViewById(R.id.end_time);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_post:
                Log.d(TAG, "Post has been cancelled");
                startActivity(new Intent(NewPost.this, MainActivity.class));
                finish();
                break;
            case R.id.create_post:
                if (hasNetworkConnection()) {
                    savePost();
                    startActivity(new Intent(NewPost.this, MainActivity.class));
                    finish();
                } else {
                    showDialog();
                }
                break;
        }
    }

    public void savePost() {
        class_name = study_class.getText().toString();
        location = study_loc.getText().toString();
        description = study_desc.getText().toString();
        seats = Integer.parseInt(num_seats.getText().toString());
        end_time = study_end.getText().toString();
        userid = currentuser.getDisplayName();
        Post current = new Post(class_name, description, location, end_time, seats, userid);
        Log.d(TAG, "The post for " + userid + "about " + class_name + " has been saved");
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        String postId = mDatabase.push().getKey();
        current.setPostid(postId);
        mDatabase.child(postId).setValue(current);
    }

    /**
     * Determines if the current device has a network connection
     * @return - true or false, if the device is connected to a network
     */
    private boolean hasNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Show an alert dialog to take the user to the Network settings screen or quit the app
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You do not have a network connection.  Connect? ")
                .setPositiveButton("Connection Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        NewPost.this.finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPost.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
