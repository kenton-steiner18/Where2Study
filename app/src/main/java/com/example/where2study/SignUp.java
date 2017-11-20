package com.example.where2study;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUp";
    //defining view objects
    private ProgressDialog progressDialog;
    private TextInputEditText emailField;
    private TextInputEditText user_name;
    private TextInputEditText passwordField;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth.AuthStateListener authchange;
    private Boolean mAllowNavigation = true;
    private FirebaseAuth auth;
    private String email, name, password, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the current page
        auth = FirebaseAuth.getInstance();
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        progressDialog = new ProgressDialog(this);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        user_name = findViewById(R.id.name_field);
        Button bSignUp = findViewById(R.id.sign_up_create_account);

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hasNetworkConnection()) {
                    attemptRegister();
                } else {
                    showDialog();
                }
            }
        });

        authchange = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:user signed in");

                    if (mAllowNavigation) {
                        mAllowNavigation = false;
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username", user.getDisplayName());
                        editor.putString("email", user.getEmail());
                        editor.putString("userid", user.getUid());
                        editor.commit();
                        finish();
                    }

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    //Attempt to register the user after the create account button is click
    private void attemptRegister() {

        // Reset errors.
        emailField.setError(null);
        passwordField.setError(null);
        user_name.setError(null);

        // Store values at the time of the login attempt.
        email = emailField.getText().toString();
        password = passwordField.getText().toString();
        name = user_name.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(name)) {
            user_name.setError(getString(R.string.error_field_required));
            focusView = user_name;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordField.setError(getString(R.string.error_invalid_password));
            focusView = passwordField;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.error_field_required));
            focusView = emailField;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailField.setError(getString(R.string.error_invalid_email));
            focusView = emailField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.setMessage("Registering...");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(SignUp.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();


                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    // Add onStart and onStop methods to start and stop the authlistener
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "SignUp Activity has started.");
        auth.addAuthStateListener(authchange);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authchange != null) {
            auth.removeAuthStateListener(authchange);
        }
    }

    //Methods for checking validity of email and password
    public static boolean isEmailValid(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("((?=.*[a-z])(?=.*d)(?=.*[A-Z]).{6,16})");
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
                        SignUp.this.finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SignUp.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
