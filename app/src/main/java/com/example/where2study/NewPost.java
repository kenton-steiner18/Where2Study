package com.example.where2study;


import android.content.Intent;
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
                savePost();
                startActivity(new Intent(NewPost.this, MainActivity.class));
                finish();
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

    /*public void setStudyLocation() {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 *//* requestCode *//*).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    *//**
     * Called after the autocomplete activity has finished to return its result.
     *//*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());

                // Format the place's details and display them in the TextView.
                placeDetails.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                findViewById(R.id.gmap_location).setVisibility(View.INVISIBLE);
                findViewById(R.id.place_details).setVisibility(View.VISIBLE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }

        }
    }

    *//**
     * Helper method to format information about a place nicely.
     *//*
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }*/
}
