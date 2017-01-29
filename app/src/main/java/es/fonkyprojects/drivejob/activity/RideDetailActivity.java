package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.restMethods.Rides.RideGetTask;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class RideDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RideDetailActivity";

    public static final String EXTRA_RIDE_KEY = "ride_key";

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

        if (mRideKey != null) {
            //GET request
            String result = null;
            Log.e(TAG, mRideKey);
            try {
                result = new RideGetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/ride/" + mRideKey).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(!result.equals("Error") && result!=null){
                Log.e(TAG, "RESULT: " + result);
                Ride ride = new Gson().fromJson(result, Ride.class);
                timeGoingView.setText("Time Going: " + ride.getTimeGoing());
                timeReturnView.setText("Time Return: " + ride.getTimeReturn());
                placeGoingView.setText("From " + ride.getPlaceGoing());
                placeReturnView.setText("To: " + ride.getPlaceReturn());
                priceView.setText("Price: " + String.valueOf(ride.getPrice()));
                passengersView.setText("Seats: " + String.valueOf(ride.getPassengers()));
                authorID = ride.getAuthorID();
                authorView.setText(ride.getAuthor());
            }
        }

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
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_join) {
            joinRide();
        }
    }

    private void joinRide() {
        final String uid = FirebaseUser.getUid();
        RideUser rideUser = new RideUser(uid, mRideKey);
    }
}
