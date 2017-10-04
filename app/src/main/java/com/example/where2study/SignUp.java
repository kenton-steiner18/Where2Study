package com.example.where2study;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUp";
    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button bSignUp;
    private ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /* initializing firebase auth object */
        auth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        bSignUp = findViewById(R.id.CreateAccount);

        progressDialog = new ProgressDialog(this);

        bSignUp.setOnClickListener(this);
    }

        private void register(){
            Log.d(TAG, "SignUp:");

            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            //Check if the fields are empty
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email is required.  Please enter an email address.", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Password is required.  Please enter a password.", Toast.LENGTH_LONG).show();
                return;
            }

            //Display progress dialog if fields are not empty
            progressDialog.setMessage("Registering....");
            progressDialog.show();

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "SignUp:onComplete:" + task.isSuccessful());
                        //checking if success
                        if(task.isSuccessful()){
                            //display some message here
                            Toast.makeText(SignUp.this,"Successfully registered",Toast.LENGTH_LONG).show();
                        }else{
                            //display some message here
                            Toast.makeText(SignUp.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        //calling register method on click
        register();
    }
}


