package com.example.where2study;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class SignUp extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

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
    private static final int RC_SIGN_IN = 9001;
    public GoogleApiClient mGoogleApiClient;

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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
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
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
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
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String name = user_name.getText().toString();

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(TAG, "The login was: " + result.isSuccess());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, "Current User: " + account.getDisplayName());
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void googleSignIn() {
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d(TAG, "Here is the connection from Login: " +  mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
        }
    }

    // Update User Interface upon successful sign in with Google
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Log.d(TAG, "this wasn't null");
            Log.d(TAG, currentUser.getDisplayName());
            Log.d(TAG, currentUser.getEmail());
            Log.d(TAG, currentUser.getUid());
            Intent i = new Intent(SignUp.this, MainActivity.class);
            i.putExtra("username", currentUser.getDisplayName());
            i.putExtra("id", currentUser.getUid());
            i.putExtra("email", currentUser.getEmail());
            startActivity(i);
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
    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
