package com.example.where2study;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static final String TAG = "Login";

    // Declare instance of Firebase Authentication
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final int RC_SIGN_IN = 9001;
    public GoogleApiClient mGoogleApiClient;

    // UI references.
    private EditText mEmail, mPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        progressDialog= new ProgressDialog(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Log.d(TAG, "Building new Session in login activity");
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText(R.string.action_google_sign_in);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.link_signup).setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Current account: " + mAuth);
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username", currentUser.getDisplayName());
            editor.putString("email", currentUser.getEmail());
            editor.putString("userid", currentUser.getUid());
            editor.commit();
            updateUI(currentUser);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            progressDialog.setMessage("Signing In...");
            progressDialog.show();
            if (result.isSuccess()) {
                Log.d(TAG, "The login was: " + result.isSuccess());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, "Current User: " + account.getDisplayName());
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "Successfully Signed In!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void googleSignIn() {
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        if (hasNetworkConnection()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
            Log.d(TAG, "Here is the connection from Login: " + mGoogleApiClient.isConnected());
        } else {
            showDialog();
        }
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
            case R.id.btn_login:
                if (hasNetworkConnection()) {
                    emailSignIn();
                } else {
                    showDialog();
                }
                break;
            case R.id.link_signup:
                createAccount(v);
                break;
            case R.id.sign_in_button:
                if (hasNetworkConnection()) {
                    googleSignIn();
                } else {
                    showDialog();
                }
                break;
        }
    }

    /**
     * Activity for creating a new account
     */
    public void createAccount(View v) {
        startActivity(new Intent(Login.this,SignUp.class));
        finish();
    }

    /**
     * Sign the user into their account
     */
    private void emailSignIn() {
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
        Log.d("NETWORK", "" + hasNetworkConnection());
        if (hasNetworkConnection()) {

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
        } else {
            showDialog();
        }
        }


    /**
     * If the login is successful
     *
     * @param user - the user who's account is currently being logged into
     */
    private void onAuthSuccess(FirebaseUser user) {
        //Go to home screen for logged in users
        startActivity(new Intent(this, MainActivity.class));
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", user.getDisplayName());
        editor.putString("email", user.getEmail());
        editor.putString("userid", user.getUid());
        editor.commit();
        finish();
    }


    private void updateUI(FirebaseUser currentUser) {
        Log.d(TAG, "Current user " + currentUser);
        if (currentUser != null) {
            Log.d(TAG, "User is logged in");
            Intent i = new Intent(Login.this, MainActivity.class);
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username", currentUser.getDisplayName());
            editor.putString("email", currentUser.getEmail());
            editor.putString("userid", currentUser.getUid());
            editor.commit();
            startActivity(i);
            finish();
        }
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
                        Login.this.finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Login.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

