package com.example.where2study;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
    private ProgressDialog signOutProgress;

    private static final String TAG = "MAINACTIVITY";

    private String username, email, userid, currentLatitude, currentLongitude;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;


    public User user;
    final private List<Post> mPosts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseProgress = new ProgressDialog(MainActivity.this);
        databaseProgress.setMessage("Loading posts...");
        databaseProgress.show();

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
                    Log.d(TAG, "jdiwsjf" + userid);

                } else {
                    User.userid = userid;
                    User.email = email;
                    User.username = username;
                    Log.d(TAG, "asdfwers: " + userid + "");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        setContentView(R.layout.activity_main_activity);

        FloatingActionButton fab = findViewById(R.id.action_new_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewPost.class));
                finish();
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

        DatabaseReference listofposts = FirebaseDatabase.getInstance().getReference().child("posts");
        Log.d(TAG, "REFERENCE: " + listofposts.toString());
        final PostArrayAdapter adapter = new PostArrayAdapter(this, mPosts);
        listofposts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "CHILDREN COUNT:" + dataSnapshot.getChildrenCount());
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
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
        postBoard.setLayoutManager(new LinearLayoutManager(this));
        // Create adapter passing in the sample user data
        // Attach the adapter to the recyclerview to populate items
        postBoard.setAdapter(adapter);
        databaseProgress.dismiss();
        adapter.setClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
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
        //Log.i(TAG, post.getPostid());
        startActivity(i);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signOut() {

        // Firebase sign out
        signOutProgress = new ProgressDialog(MainActivity.this);
        signOutProgress.setMessage("Signing Out");
        signOutProgress.show();
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
        signOutProgress.dismiss();
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
            Intent i = new Intent(MainActivity.this, Profile.class);
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username", username);
            editor.putString("email",email);
            editor.putString("userid", userid);
            editor.commit();
            startActivity(i);
        } else if (id == R.id.nav_gmaps) {
            Uri gmmIntentUri = Uri.parse("geo:" + currentLatitude + "," + currentLongitude + "?q=library");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
