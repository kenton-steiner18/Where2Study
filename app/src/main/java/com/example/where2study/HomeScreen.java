package com.example.where2study;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity{

    //UI Elements
    private Button mSignIn, mSignUp, mAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        //Set up the buttons on the homescreen
        mSignIn=findViewById(R.id.home_sign_in_button);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent signin = new Intent(view.getContext(), Login.class);
                view.getContext().startActivity(signin);
            }
        });
        mSignUp=findViewById(R.id.home_sign_up_button);
        mAbout=findViewById(R.id.about);

    }


    // When the user hits the New User button on the homescreen
    public void loadNewUser(View v) {
        Intent newUser = new Intent(HomeScreen.this, SignUp.class);
        HomeScreen.this.startActivity(newUser);
        HomeScreen.this.finish();
    }

}
