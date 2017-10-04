package com.example.where2study;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    private FirebaseAuth auth;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (user == null) {
            //Start the HomeScreen activity or the main activity depending on if the user is logged in or not
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run () {
                    Intent mainIntent = new Intent(SplashScreen.this, HomeScreen.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            //Start the HomeScreen activity or the main activity depending on if the user is logged in or not
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run () {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
}
}
