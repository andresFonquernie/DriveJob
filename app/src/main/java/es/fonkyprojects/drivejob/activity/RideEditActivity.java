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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.MapLocation;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePutTask;
import es.fonkyprojects.drivejob.utils.Constants;

public class RideEditActivity extends Activity {

    private static final String TAG = "RideEditActivity";
    public static final String EXTRA_RIDE = "ride";

    @Bind(R.id.edit_placeGoing) EditText etEditPlaceFrom;
    @Bind(R.id.edit_placeReturn) EditText etEditPlaceTo;
    @Bind(R.id.edit_timeGoing) EditText etTimeGoing;
    @Bind(R.id.edit_timeReturn) EditText etTimeReturn;
    @Bind(R.id.edit_price) EditText etPrice;
    @Bind(R.id.edit_passengers) EditText etPassengers;
    @Bind(R.id.edit_avseats) EditText etAvSeats;
    @Bind(R.id.btn_edit) Button btnEdit;


    public static final int MAP_ACTIVITY_EDIT = 0;
    public static final String MAPLOC = "MAPLOC";

    private Ride mRide;

    public double latGoing;
    public double latReturning;
    public double lngGoing;
    public double lngReturning;
    public String timeG;
    public String timeR;
    public int oldPassengers;
    int mapsGR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_edit);
        ButterKnife.bind(this);

        // Get post key from intent
        mRide = (Ride) getIntent().getSerializableExtra(EXTRA_RIDE);
        if (mRide == null) {
            throw new IllegalArgumentException("Must pass EXTRA_RIDE");
        }

        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRide(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mRide != null) {
            etTimeGoing.setText(mRide.getTimeGoing());
            etTimeReturn.setText(mRide.getTimeReturn());
            etEditPlaceFrom.setText(mRide.getPlaceGoing());
            etEditPlaceTo.setText(mRide.getPlaceReturn());
            etPrice.setText(String.valueOf(mRide.getPrice()));
            oldPassengers = mRide.getPassengers();
            etPassengers.setText(String.valueOf(mRide.getPassengers()));
            etAvSeats.setText(String.valueOf(mRide.getAvSeats()));
            latGoing = mRide.getLatGoing();
            latReturning = mRide.getLatReturn();
            lngGoing = mRide.getLngGoing();
            lngReturning = mRide.getLngReturn();
        }
    }

    public void editRide(View view){
        final String placeG = etEditPlaceFrom.getText().toString();
        final String placeR = etEditPlaceTo.getText().toString();
        final String timeG = etTimeGoing.getText().toString();
        final String timeR = etTimeReturn.getText().toString();

        String sPrice = etPrice.getText().toString();
        final int price;
        if(sPrice.length()>0)
            price = Integer.parseInt(sPrice);
        else
            price = 0;

        String sPassengers = etPassengers.getText().toString();
        final int passengers;
        if(sPassengers.length()>0)
            passengers = Integer.parseInt(sPassengers);
        else
            passengers = 0;

        final int avSeats = Integer.parseInt(etAvSeats.getText().toString());
        if (validate(placeG, placeR, sPrice, sPassengers, avSeats)) {

            btnEdit.setEnabled(false);
            Toast.makeText(this, "Put", Toast.LENGTH_LONG).show();

            int newAvSeats = avSeats - (oldPassengers -  passengers);
            String putKey = writeEditRide(placeG, placeR, price, passengers, newAvSeats);
            Ride r = new Ride(mRide.getID(), mRide.getAuthorID(), mRide.getAuthor(), timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, price, passengers, newAvSeats);
            (new SQLConnect()).updateRide(r);

            if (putKey.equals("Update")) {
                Intent intent = new Intent(RideEditActivity.this, RideDetailActivity.class);
                intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, mRide.getID());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Error. Try again later", Toast.LENGTH_LONG).show();
                btnEdit.setEnabled(true);
            }
        }
    }

    private String writeEditRide(String placeG, String placeR, int price,int passengers, int newAvSeats) {
        String result = "";
        try {
            Ride ride = new Ride("", "", timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, price, passengers, newAvSeats);
            RidePutTask rpt = new RidePutTask(this);
            rpt.setRidePost(ride);
            result = rpt.execute(Constants.BASE_URL + "ride/" + mRide.getID()).get();
            Log.e(TAG, "RESULT PUT RIDE: " + result);
            return result;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void startMapsEdit(View v){
        mapsGR = v.getId();
        Intent intent = new Intent(RideEditActivity.this, MapsActivity.class);
        startActivityForResult(intent, MAP_ACTIVITY_EDIT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_ACTIVITY_EDIT){ // If it was an ADD_ITEM, then add the new item and update the list
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                MapLocation ml = (MapLocation) MBuddle.getSerializable(MAPLOC);
                if (ml != null) {
                    if(mapsGR == R.id.edit_placeGoing) {
                        etEditPlaceFrom.setText("");
                        lngGoing = ml.getLongitude();
                        latGoing = ml.getLatitude();
                    }
                    if(mapsGR == R.id.edit_placeReturn) {
                        etEditPlaceTo.setText(ml.getAddress());
                        lngReturning = ml.getLongitude();
                        latReturning = ml.getLatitude();
                    }
                }
            }
        }
    }
    public void showTime(final View view){
        Calendar mcurrentTime = Calendar.getInstance();
        mcurrentTime.getTime();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(RideEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                int result = view.getId();
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, selectedHour);
                c.set(Calendar.MINUTE, selectedMinute);
                SimpleDateFormat formatTime = new SimpleDateFormat("kk:mm");
                if(result == R.id.edit_timeGoing){
                    timeG = formatTime.format(c.getTime());
                    etTimeGoing.setText(timeG);
                }
                else if(result == R.id.edit_timeReturn){
                    timeR = formatTime.format(c.getTime());
                    etTimeReturn.setText(timeR);
                }
            }

        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private boolean validate(String placeG, String placeR, String price, String passengers, int avSeats) {
        boolean valid = true;

        if (placeG.isEmpty()) {
            etEditPlaceFrom.setError("Not null");
            valid = false;
        } else {
            etEditPlaceFrom.setError(null);
        }

        if (placeR.isEmpty()) {
            etEditPlaceTo.setError("Not null");
            valid = false;
        } else {
            etEditPlaceTo.setError(null);
        }

        if (timeG != null  && timeG.isEmpty()) {
            etTimeGoing.setError("Not null");
            valid = false;
        } else {
            etTimeGoing.setError(null);
        }

        if (timeR != null && timeR.isEmpty()) {
            etTimeReturn.setError("Not null");
            valid = false;
        } else {
            etTimeReturn.setError(null);
        }

        if (price.isEmpty()) {
            etPrice.setError("Not 0");
            valid = false;
        } else {
            etPrice.setError(null);
        }

        if (passengers.isEmpty()) {
            etPassengers.setError("Not 0");
            valid = false;
        } else {
            etPassengers.setError(null);
            int checkPass = avSeats+Integer.parseInt(passengers);
            if(checkPass<oldPassengers){
                Toast.makeText(this, "Minimun passengers: " + (oldPassengers-avSeats), Toast.LENGTH_LONG).show();
            }
        }


        return valid;
    }
}
