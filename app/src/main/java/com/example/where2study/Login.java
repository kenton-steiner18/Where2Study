package com.example.where2study;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity {

    private static final String TAG = "Login";

    // Declare instance of Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase Database Instance
    private DatabaseReference mDatabase;

    // UI references.
    private EditText mEmail, mPassword;
    private Button mSignIn, mSignUp, changePassword;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmail = findViewById(R.id.field_email);
        mPassword = findViewById(R.id.field_password);
        //mProgressDialog= findViewById(R.id.progress_dialog);
        mSignIn = findViewById(R.id.sign_in);
        mSignUp = findViewById(R.id.sign_up);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
    }


    /**
     * Activity for creating a new account
     */
    public void createAccount() {
        startActivity(new Intent(Login.this,SignUp.class));
    }

    /**
     * Sign the user into their account
     */
    private void signIn() {
        Log.d(TAG, "signIn:");
        if (!validateForm()) {
            return;
        }
        //showProgressDialog();

        // Initialize the email and password variables from the fields entered by the user
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        //hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(Login.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * To create a new account
     */
    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }
        //showProgressDialog();

        // Initialize the email and password variables
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        //hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(Login.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * If the login is successful
     *
     * @param user - the user who's account is currently being logged into
     */
    private void onAuthSuccess(FirebaseUser user) {
        String username = getUsername(user.getEmail());

        //Create a new User
        //createUser(user.getUid(), username, user.getEmail());

        //Go to home screen for logged in users
        startActivity(new Intent(this, HomeScreen.class));
        finish();
    }

    /**
     * Takes a user's email and returns the username they can use to login
     *
     * @param email - the email address of the user
     */
    private String getUsername(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }



    /**
     * Validate the user entered a correct email and password during the sign up
     */
    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEmail.getText().toString())) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (TextUtils.isEmpty(mPassword.getText().toString())) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    /**
     * Create a new User in the database
     *
     * @param username - string the user can use to login instead of email
     * @param name - the user's name
     * @param email - user's email address
     */
    private void createUser(String username, String name, String email) {
        //User user = new User(name, email);

        // Write is to the database
        //mDatabase.child("users").child(username).setValue(user);
    }

    // On-click events that call signIn and signUp methods

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in) {
            signIn();
        } else if (i == R.id.sign_up) {
            signUp();
        }
    }
}

