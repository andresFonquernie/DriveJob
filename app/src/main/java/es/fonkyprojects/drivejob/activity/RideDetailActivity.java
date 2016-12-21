package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class RideDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RideDetailActivity";

    public static final String EXTRA_RIDE_KEY = "ride_key";

    private DatabaseReference mRideReference;
    private DatabaseReference mRideUserReference;

    private ValueEventListener mRideListener;
    private String mRideKey;
    private String authorID;
    public Map<String, Boolean> ridersJoin = new HashMap<>();

    private ImageView authorImage;
    private TextView authorView;
    private TextView timeGoingView;
    private TextView placeGoingView;
    private TextView timeReturnView;
    private TextView placeReturnView;
    private TextView priceView;
    private TextView passengersView;
    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        // Get post key from intent
        mRideKey = getIntent().getStringExtra(EXTRA_RIDE_KEY);
        if (mRideKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mRideReference = FirebaseDatabase.getInstance().getReference()
                .child("rides").child(mRideKey);
        mRideUserReference = FirebaseDatabase.getInstance().getReference()
                .child("ride-user").child(mRideKey);

        // Initialize Views
        authorImage = (ImageView) findViewById(R.id.ride_author_photo);;
        authorView = (TextView) findViewById(R.id.ride_author);;
        timeGoingView = (TextView) findViewById(R.id.ride_timeGoing);
        placeGoingView = (TextView) findViewById(R.id.ride_placeGoing);
        timeReturnView = (TextView) findViewById(R.id.ride_timeReturn);
        placeReturnView = (TextView) findViewById(R.id.ride_placeReturn);
        priceView = (TextView) findViewById(R.id.ride_prize);
        passengersView = (TextView) findViewById(R.id.ride_passengers);
        joinButton = (Button) findViewById(R.id.btn_join);

        joinButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        ValueEventListener rideListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Ride ride = dataSnapshot.getValue(Ride.class);
                // Add information to views
                Log.i(TAG, ride.toString());
                timeGoingView.setText(ride.timeGoing);
                placeGoingView.setText(ride.placeGoing);
                timeReturnView.setText(ride.timeReturn);
                placeReturnView.setText(ride.placeReturn);
                priceView.setText(String.valueOf(ride.price));
                passengersView.setText(String.valueOf(ride.passengers));
                authorID = ride.authorID;
                authorView.setText(ride.author);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadRide:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(RideDetailActivity.this, "Failed to load ride",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };

        mRideReference.addValueEventListener(rideListener);

        // Keep copy of post listener so we can remove it when app stops
        mRideListener = rideListener;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(authorID.equals(FirebaseUser.getUid())) {
            getMenuInflater().inflate(R.menu.mnu_myride, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If changes OK, return new value in the result Intent back to the calling activity
        switch (item.getItemId()) {
            case R.id.mnu_edit:
                Intent intent = new Intent(RideDetailActivity.this, RideEditActivity.class);
                intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, mRideKey);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mRideListener != null) {
            mRideReference.removeEventListener(mRideListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_join) {
            joinRide();
        }
    }

    private void joinRide() {
        final String uid = FirebaseUser.getUid();
        RideUser rideUser = new RideUser(uid, mRideKey);

        ridersJoin.put(FirebaseUser.getUid(), true);

        // Push the comment, it will appear in the list
        mRideUserReference.push().setValue(ridersJoin);

    }
}
