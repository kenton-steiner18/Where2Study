package com.example.where2study;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends BaseActivity {

    //UI Elements
    private Button mSignIn, mSignUp, mAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        //Set up the buttons on the homescreen
        mSignIn=findViewById(R.id.sign_in);
        mSignUp=findViewById(R.id.sign_up);
        mAbout=findViewById(R.id.about);

    }

    // When the user hits the sign in button on the homescreen
    public void loadSignIn (View v) {
        Intent intent = new Intent(HomeScreen.this, Login.class);
        startActivity(intent);
    }
}
