package com.example.where2study;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.where2study.Objects.Post;
import com.example.where2study.Objects.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ItemClickListener {

    private FirebaseAuth mAuth;
    private ProgressDialog databaseProgress;

    private static final String TAG = "MAINACTIVITY";

    private String username, email, userid, currentLatitude, currentLongitude;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    private Thread t1, t2;

    public User user;
    final private List<Post> mPosts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        databaseProgress = ProgressDialog.show(MainActivity.this, "", "Loading posts...");
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main_activity);

        FloatingActionButton fab = findViewById(R.id.action_new_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasNetworkConnection()) {
                    startActivity(new Intent(MainActivity.this, NewPost.class));
                    finish();
                } else {
                    showDialog();
                }
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                userid = settings.getString("userid", "");
                username = settings.getString("username", "");
                email = settings.getString("email", "");

                FirebaseDatabase userRef = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = userRef.getReference("user");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.hasChild(userid)) {
                            user = new User(username, email, userid);
                            myRef.child(userid).setValue(user);
                        } else {
                            User.userid = userid;
                            User.email = email;
                            User.username = username;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference listofposts = FirebaseDatabase.getInstance().getReference().child("posts");
                Log.d(TAG, "REFERENCE: " + listofposts.toString());
                final PostArrayAdapter adapter = new PostArrayAdapter(MainActivity.this, mPosts);
                listofposts.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "CHILDREN COUNT:" + dataSnapshot.getChildrenCount());
                        for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                            Post note = noteSnapshot.getValue(Post.class);
                            mPosts.add(note);
                            Log.d("ASDF", mPosts.toString());
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
                    }
                });

                RecyclerView postBoard = (RecyclerView) findViewById(R.id.post_board_view);
                postBoard.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                // Create adapter passing in the sample user data
                // Attach the adapter to the recyclerview to populate items
                databaseProgress.dismiss();
                postBoard.setAdapter(adapter);
                adapter.setClickListener(MainActivity.this);
            }
        });

        t1.start();
        t2.start();

    }

    @Override
    public void onClick(View view, int position) {
        if (hasNetworkConnection()) {
            final Post post = mPosts.get(position);
            Intent i = new Intent(this, ViewPost.class);
            SharedPreferences postinfo = getSharedPreferences("postinfo", 0);
            SharedPreferences.Editor editor = postinfo.edit();
            editor.putString("postid", post.getPostid());
            editor.putString("userid", post.getUser());
            editor.putString("classname", post.getClassName());
            editor.putString("location", post.getLocation());
            editor.putString("starttime", post.getTheTime());
            editor.putString("endtime", post.getEndTime());
            editor.putString("description", post.getDescription());
            editor.putInt("numseats", post.getSeats());
            editor.commit();
            startActivity(i);
        } else {
            showDialog();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    currentLatitude = String.valueOf(mLastLocation.getLatitude());
                    currentLongitude = String.valueOf(mLastLocation.getLongitude());
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResuming");

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        TextView user_name = findViewById(R.id.drawer_user_name);
        user_name.setText(username);
        TextView user_email = findViewById(R.id.drawer_user_email);
        user_email.setText(email);
        return true;
    }

    public void signOut() {

        // Firebase sign out

        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, Login.class));
        MainActivity.this.finish();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signout) {
            signOut();
            finish();
        } else if (id == R.id.nav_profile) {
            if (hasNetworkConnection()) {
                Intent i = new Intent(MainActivity.this, Profile.class);
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", username);
                editor.putString("email", email);
                editor.putString("userid", userid);
                editor.commit();
                startActivity(i);
            } else {
                showDialog();
            }
        } else if (id == R.id.nav_gmaps) {
            if (hasNetworkConnection()) {
                Uri gmmIntentUri = Uri.parse("geo:" + currentLatitude + "," + currentLongitude + "?q=library");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } else {
                showDialog();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        if (!hasNetworkConnection()) {
            showDialog();
        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You do not have a network connection.  Connect? ")
                .setPositiveButton("Connection Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      MainActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
