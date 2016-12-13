package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class CreateRideActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "NewRideActivity";

    private DatabaseReference mDatabase;

    @Bind(R.id.input_timeGoing) EditText etTimeGoing;
    @Bind(R.id.input_placeGoing) EditText etPlaceGoing;
    @Bind(R.id.input_timeReturn) EditText etTimeReturn;
    @Bind(R.id.input_placeReturn) EditText etPlaceReturn;
    @Bind(R.id.input_price) EditText etPrice;
    @Bind(R.id.input_passengers) EditText etPassengers;
    @Bind(R.id.btn_create) Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnCreate.setOnClickListener(this);
        etTimeGoing.setOnClickListener(this);
        etTimeReturn.setOnClickListener(this);
    }

    private void createRide(){

        final String timeG = etTimeGoing.getText().toString();
        final String placeG = etPlaceGoing.getText().toString();
        final String timeR = etTimeReturn.getText().toString();
        final String placeR = etPlaceReturn.getText().toString();
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
                                Toast.makeText(CreateRideActivity.this,
                                        "Error: could not fetch user.",
                                        Toast.LENGTH_SHORT).show();
                                btnCreate.setEnabled(true);
                            } else {
                                // Write new post
                                writeNewRide(userId, user.username, timeG, placeG, timeR, placeR, price, passenger);

                                // Go to MainActivity
                                startActivity(new Intent(CreateRideActivity.this, MenuActivity.class));
                                finish();
                            }

                            // [END_EXCLUDE]
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            Toast.makeText(CreateRideActivity.this,
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
    private void writeNewRide(String userId, String username, String timeG, String placeG, String timeR, String placeR, int price,int passengers) {
        // Create new ride at // /ride/$rideid
        String key = mDatabase.child("rides").push().getKey();
        Ride ride = new Ride(userId, username, timeG, placeG, timeR, placeR, price, passengers);

        mDatabase.child("rides").child(key).setValue(ride);
    }

    private void showTime(final String text){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CreateRideActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        if (timeG.isEmpty()) {
            etTimeGoing.setError("Not null");
            valid = false;
        } else {
            etTimeGoing.setError(null);
        }

        if (placeG.isEmpty()) {
            etPlaceGoing.setError("Not null");
            valid = false;
        } else {
            etPlaceGoing.setError(null);
        }

        if (timeR.isEmpty()) {
            etTimeReturn.setError("Not null");
            valid = false;
        } else {
            etTimeReturn.setError(null);
        }

        if (placeR.isEmpty()) {
            etPlaceReturn.setError("Not null");
            valid = false;
        } else {
            etPlaceReturn.setError(null);
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
