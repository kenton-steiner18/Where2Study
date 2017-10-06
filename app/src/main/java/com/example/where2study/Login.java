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
public class Login extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Login";

    // Declare instance of Firebase Authentication
    private FirebaseAuth mAuth;

    //Firebase Database Instance
    private DatabaseReference mDatabase;

    // UI references.
    private EditText mEmail, mPassword;
    private Button mSignIn, mSignUp, changePassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPassword);
        progressDialog= new ProgressDialog(this);
        mSignIn = findViewById(R.id.login_page_button_sign_in);
        mSignUp = findViewById(R.id.login_page_CreateAccount);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        mSignIn.setOnClickListener(this);
    }


    /**
     * Activity for creating a new account
     */
    public void createAccount(View v) {
        startActivity(new Intent(Login.this,SignUp.class));
        Login.this.finish();
    }

    /**
     * Sign the user into their account
     */
    private void signIn() {
        Log.d(TAG, "signIn:");

        // Initialize the email and password variables from the fields entered by the user
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        //Check if the fields are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "You must enter an email address", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "You must enter a password", Toast.LENGTH_LONG).show();
            return;
        }

        //Display progress dialog if fields are not empty
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        progressDialog.dismiss();
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
     * If the login is successful
     *
     * @param user - the user who's account is currently being logged into
     */
    private void onAuthSuccess(FirebaseUser user) {

        //Go to home screen for logged in users
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        //calling register method on click
        signIn();
    }
    }

