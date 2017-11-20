package com.example.where2study;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class Profile extends AppCompatActivity {

    private String user_name, email;
    private TextView username, email_field;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username = findViewById(R.id.user_name);
        email_field = findViewById(R.id.user_email);
        auth = FirebaseAuth.getInstance();
        FirebaseUser current = auth.getCurrentUser();
        user_name = current.getDisplayName();
        email = current.getEmail();
        username.setText(user_name);
        email_field.setText(email);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
