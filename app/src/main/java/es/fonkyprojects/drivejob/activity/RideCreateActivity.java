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
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.User;

public class RideCreateActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "NewRideActivity";

    private DatabaseReference mDatabase;

    @Bind(R.id.input_placeGoing) EditText etPlaceFrom;
    @Bind(R.id.input_placeReturn) EditText etPlaceTo;
    private TextView etTimeGoing;
    private TextView etTimeReturn;
    @Bind(R.id.input_price) EditText etPrice;
    @Bind(R.id.input_passengers) EditText etPassengers;
    @Bind(R.id.btn_create) Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        etTimeGoing = (TextView) findViewById(R.id.input_timeGoing);
        etTimeReturn = (TextView) findViewById(R.id.input_timeReturn);


        btnCreate.setOnClickListener(this);
        etTimeGoing.setOnClickListener(this);
        etTimeReturn.setOnClickListener(this);
    }

    private void createRide(){

        final String placeG = etPlaceFrom.getText().toString();
        final String placeR = etPlaceTo.getText().toString();
        final String timeG = etTimeGoing.getText().toString();
        final String timeR = etTimeReturn.getText().toString();
        final int price = Integer.parseInt(etPrice.getText().toString());
        final int passenger = Integer.parseInt(etPassengers.getText().toString());


        if(validate(timeG, placeG, timeR, placeR, price, passenger)) {

            btnCreate.setEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();


            // [START single_value_read]
            final String userId = FirebaseUser.getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            User user = dataSnapshot.getValue(User.class);

                            // [START_EXCLUDE]
                            if (user == null) {
                                // User is null, error out
                                Log.e(TAG, "User " + userId + " is unexpectedly null");
                                Toast.makeText(RideCreateActivity.this,
                                        "Error: could not fetch user.",
                                        Toast.LENGTH_SHORT).show();
                                btnCreate.setEnabled(true);
                            } else {
                                // Write new post
                                String postKey = writeNewRide(userId, user.username, timeG, placeG, timeR, placeR, price, passenger);

                                // Go to RideDetailActivity
                                Intent intent = new Intent(RideCreateActivity.this, RideDetailActivity.class);
                                intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, postKey);
                                startActivity(intent);
                                finish();
                            }

                            // [END_EXCLUDE]
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            Toast.makeText(RideCreateActivity.this,
                                    "Error: No Internet connection",
                                    Toast.LENGTH_SHORT).show();
                            // [START_EXCLUDE]
                            btnCreate.setEnabled(true);
                            // [END_EXCLUDE]
                        }
                    });
            // [END single_value_read]
        }
    }

    // [START write_fan_out]
    private String writeNewRide(String userId, String username, String timeG, String placeG, String timeR, String placeR, int price,int passengers) {
        // Create new ride at // /ride/$rideid
        String key = mDatabase.child("rides").push().getKey();
        Ride ride = new Ride(userId, username, timeG, placeG, timeR, placeR, price, passengers);

        mDatabase.child("rides").child(key).setValue(ride);

        return key;
    }

    private void showTime(final String text){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(RideCreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        } else {
            etPassengers.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_create) {
            createRide();
        } else if (i == R.id.input_timeGoing) {
            showTime("going");
        } else if (i == R.id.input_timeReturn) {
            showTime("return");
        }
    }
}
