package com.example.where2study;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
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
public class Login extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Login";

    // Declare instance of Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase Database Instance
    private DatabaseReference mDatabase;

    // UI references.
    private EditText mEmailField, mPasswordField;
    private Button mSignIn, mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up the login form.
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mSignIn = findViewById(R.id.sign_in);
        mSignUp = findViewById(R.id.sign_up);

        // On-click listeners for the buttons on the homepage
        mSignIn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
    }

    // Check if the User is currently logged in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    /**
     * Sign the user into their account
     */
    private void signIn() {
        Log.d(TAG, "signIn:");
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        // Initialize the email and password variables from the fields entered by the user
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

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
        showProgressDialog();

        // Initialize the email and password variables
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

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

        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
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

