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

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.restMethods.Rides.RideDeleteTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RideGetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class RideDetailActivity extends AppCompatActivity{

    private static final String TAG = "RideDetailActivity";

    public static final String EXTRA_RIDE = "ride";
    public static final String EXTRA_RIDE_KEY = "ride_key";
    public static final String EXTRA_USER_ID = "userId";



    private String mRideKey;
    private String authorID;
    private Ride ride;
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

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRide(view);
            }
        });
        authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfile(view);
            }
        });
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
                ride = new Gson().fromJson(result, Ride.class);
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


    private void joinRide(View view) {
        final String uid = FirebaseUser.getUid();
        RideUser rideUser = new RideUser(uid, mRideKey);
    }

    private void goProfile(View view){
        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
        intent.putExtra(RideDetailActivity.EXTRA_USER_ID, authorID);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(FirebaseUser.getUid().equals(authorID)) {
            getMenuInflater().inflate(R.menu.mnu_myride, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If changes OK, return new value in the result Intent back to the calling activity
        Intent intent;
        switch (item.getItemId()) {
            case R.id.mnu_edit:
                intent = new Intent(RideDetailActivity.this, RideEditActivity.class);
                intent.putExtra(RideDetailActivity.EXTRA_RIDE, ride);
                startActivity(intent);
                finish();
                break;
            case R.id.mnu_delete:
                RideDeleteTask rdt = new RideDeleteTask(this);
                String result = "";
                try {
                    result =  rdt.execute(Constants.BASE_URL + "ride/" + mRideKey).get();
                    if(result.equals("Ok")){
                        intent = new Intent(RideDetailActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(this, "Error deleting", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }

}
