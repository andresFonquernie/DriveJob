package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class MyProfileActivity extends Activity {

    private static final String TAG = "MyProfileActivity";

    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;


    private CollapsingToolbarLayout userToolbar;
    private ImageView userImage;
    private TextView userEmail;
    private TextView userPhone;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseUser.getUid());

        // Initialize Views
        userImage = (ImageView) findViewById(R.id.userImage);;
        userEmail = (TextView) findViewById(R.id.emailText);;
        userPhone = (TextView) findViewById(R.id.phoneText);
        userToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                // Add information to views
                //userImage.setText(user.image);
                userEmail.setText(user.getEmail());
                //userPhone.setText(user.phone);
                userToolbar.setTitle(user.getUsername() + " " +  user.getSurname());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(MyProfileActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };

        mUserReference.addValueEventListener(userListener);

        // Keep copy of post listener so we can remove it when app stops
        mUserListener = userListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mUserListener != null) {
            mUserReference.removeEventListener(mUserListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mnu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_settings:
                //Intent itemSettings = new Intent(this, MySettingsActivity.class);
                //startActivity(itemSettings);
                break;
            case R.id.mnu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
