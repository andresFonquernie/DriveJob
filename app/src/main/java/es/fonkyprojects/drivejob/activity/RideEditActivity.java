package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Ride;

public class RideEditActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "RideEditActivity";
    public static final String EXTRA_RIDE_KEY = "ride_key";

    private DatabaseReference mDatabase;
    private DatabaseReference mRideReference;

    private ValueEventListener mRideListener;
    private String mRideKey;
    private String authorID;
    private String authorUsername;

    @Bind(R.id.input_placeGoing) EditText etPlaceFrom;
    @Bind(R.id.input_placeReturn) EditText etPlaceTo;
    private TextView etTimeGoing;
    private TextView etTimeReturn;
    @Bind(R.id.input_price) EditText etPrice;
    @Bind(R.id.input_passengers) EditText etPassengers;
    @Bind(R.id.btn_edit) Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_edit);
        ButterKnife.bind(this);

        // Get post key from intent
        mRideKey = getIntent().getStringExtra(EXTRA_RIDE_KEY);
        if (mRideKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }


        // Initialize Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRideReference = FirebaseDatabase.getInstance().getReference()
                .child("rides").child(mRideKey);

        // Initialize Views
        etTimeGoing = (EditText) findViewById(R.id.input_timeGoing);
        etTimeReturn = (EditText) findViewById(R.id.input_timeReturn);
        etPlaceFrom = (EditText) findViewById(R.id.input_placeGoing);
        etPlaceTo = (EditText) findViewById(R.id.input_placeReturn);
        etPrice = (EditText) findViewById(R.id.input_price);
        etPassengers = (EditText) findViewById(R.id.input_passengers);
        btnEdit = (Button) findViewById(R.id.btn_edit);

        btnEdit.setOnClickListener(this);
        etTimeGoing.setOnClickListener(this);
        etTimeReturn.setOnClickListener(this);
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
                etTimeGoing.setText(ride.timeGoing);
                etTimeReturn.setText(ride.timeReturn);
                etPlaceFrom.setText(ride.timeReturn);
                etPlaceTo.setText(ride.placeReturn);
                etPrice.setText(String.valueOf(ride.price));
                etPassengers.setText(String.valueOf(ride.passengers));
                authorID = ride.authorID;
                authorUsername = ride.author;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadRide:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(RideEditActivity.this, "Failed to load ride",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };

        mRideReference.addValueEventListener(rideListener);

        // Keep copy of post listener so we can remove it when app stops
        mRideListener = rideListener;
    }


    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mRideListener != null) {
            mRideReference.removeEventListener(mRideListener);
        }
    }

    private void editRide(){

        final String placeG = etPlaceFrom.getText().toString();
        final String placeR = etPlaceTo.getText().toString();
        final String timeG = etTimeGoing.getText().toString();
        final String timeR = etTimeReturn.getText().toString();
        final int price = Integer.parseInt(etPrice.getText().toString());
        final int passenger = Integer.parseInt(etPassengers.getText().toString());

        if(validate(timeG, placeG, timeR, placeR, price, passenger)) {

            btnEdit.setEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

            // Edit ride
            postRide(authorID, authorUsername, timeG, placeG, timeR, placeR, price, passenger);

            // Go to RideDetailActivity
            Intent intent = new Intent(RideEditActivity.this, RideDetailActivity.class);
            intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, mRideKey);
            startActivity(intent);
            finish();

        }
    }

    // [START write_fan_out]
    private void postRide(String userId, String username, String timeG, String placeG, String timeR, String placeR, int price,int passengers) {
        // Create new ride at // /ride/$rideid
        Ride ride = new Ride(userId, username, timeG, placeG, timeR, placeR, price, passengers);

        mDatabase.child("rides").child(mRideKey).setValue(ride);
    }

    private void showTime(final String text){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(RideEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if(text.equals("going"))
                    etTimeGoing.setText( selectedHour + ":" + selectedMinute);
                else if(text.equals("return"))
                    etTimeReturn.setText( selectedHour + ":" + selectedMinute);
            }

        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private boolean validate(String timeG, String placeG, String timeR, String placeR, int price, int passengers) {
        boolean valid = true;

        if (placeG.isEmpty()) {
            etPlaceFrom.setError("Not null");
            valid = false;
        } else {
            etPlaceFrom.setError(null);
        }

        if (placeR.isEmpty()) {
            etPlaceTo.setError("Not null");
            valid = false;
        } else {
            etPlaceTo.setError(null);
        }

        if (timeG.isEmpty()) {
            etTimeGoing.setError("Not null");
            valid = false;
        } else {
            etTimeGoing.setError(null);
        }

        if (timeR.isEmpty()) {
            etTimeReturn.setError("Not null");
            valid = false;
            Log.w(TAG, "TimeR");
        } else {
            etTimeReturn.setError(null);
        }

        if (price == 0) {
            etPrice.setError("Not 0");
            valid = false;
        } else {
            etPrice.setError(null);
        }

        if (passengers == 0) {
            etPassengers.setError("Not 0");
            valid = false;
            Log.w(TAG, "Passengers");
        } else {
            etPassengers.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_edit) {
            editRide();
        } else if (i == R.id.input_timeGoing) {
            showTime("going");
        } else if (i == R.id.input_timeReturn) {
            showTime("return");
        }
    }
}
