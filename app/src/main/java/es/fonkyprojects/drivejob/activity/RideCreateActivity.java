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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.MapLocation;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.RideUser.RideUserPostTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePostTask;
import es.fonkyprojects.drivejob.restMethods.Users.UserGetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class RideCreateActivity extends Activity{

    private static final String TAG = "NewRideActivity";

    @Bind(R.id.input_placeGoing) EditText etPlaceFrom;
    @Bind(R.id.input_placeReturn) EditText etPlaceTo;
    @Bind(R.id.input_timeGoing)  EditText etTimeGoing;
    @Bind(R.id.input_timeReturn) EditText etTimeReturn;
    @Bind(R.id.input_price) EditText etPrice;
    @Bind(R.id.input_passengers) EditText etPassengers;
    @Bind(R.id.btn_create) Button btnCreate;

    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";

    public double latGoing;
    public double latReturning;
    public double lngGoing;
    public double lngReturning;
    public String timeG;
    public String timeR;
    public String userID;
    public String username;

    int mapsGR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);
        ButterKnife.bind(this);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRide(view);
            }
        });
    }

    public void createRide(View view){

        final String placeG = etPlaceFrom.getText().toString();
        final String placeR = etPlaceTo.getText().toString();

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

        if (validate(placeG, placeR, sPrice, sPassengers)) {

            btnCreate.setEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

            String postKey = writeNewRide(placeG, placeR, price, passengers);
            Ride r = new Ride(postKey,userID, username, timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, price, passengers, passengers);
            (new SQLConnect()).insertRide(r);
            writeNewRideUser(postKey);
            Log.e(TAG, "POSTKEY: " + postKey);

            if (!postKey.equals("Error")) {
                Intent intent = new Intent(RideCreateActivity.this, RideDetailActivity.class);
                intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, postKey);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Error. Try again later", Toast.LENGTH_LONG).show();
                btnCreate.setEnabled(true);
            }
        }
    }

     private String writeNewRide(String placeG, String placeR, int price,int passengers) {
        String result = "";
        try {
            userID = FirebaseUser.getUid();
            username = getUsername(userID);

            Ride ride = new Ride(userID, username, timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, price, passengers, passengers);
            RidePostTask rpt = new RidePostTask(this);
            rpt.setRidePost(ride);
            result = rpt.execute(Constants.BASE_URL + "ride").get();

            Log.e(TAG, "RESULT WRITE RIDE: " + result);
            Ride r = new Gson().fromJson(result, Ride.class);
            result = r.getID();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String writeNewRideUser(String rideKey) {
        try {
            RideUser rideUser = new RideUser(rideKey, "");
            RideUserPostTask rupt = new RideUserPostTask(this);
            rupt.setRideUserPost(rideUser);
            String result = rupt.execute(Constants.BASE_URL + "rideuser").get();

            Log.e(TAG, "RESULT WRITE RIDE: " + result);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getUsername(String userId) throws ExecutionException, InterruptedException {
        String result;
        UserGetTask ugt = new UserGetTask(this);
        result = ugt.execute(Constants.BASE_URL + "user/?userId=" + userId).get();
        Log.e(TAG, "RESULT GET USER: " + result);
        Type type = new TypeToken<List<User>>(){}.getType();
        List<User> inpList = new Gson().fromJson(result, type);
        User u = inpList.get(0);
        Log.i(TAG, u.getUsername());

        return u.getUsername() + " " + u.getSurname();
    }


    public void startMaps(View v){
        mapsGR = v.getId();
        Intent intent = new Intent(RideCreateActivity.this, MapsActivity.class);
        startActivityForResult(intent, MAP_ACTIVITY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_ACTIVITY){ // If it was an ADD_ITEM, then add the new item and update the list
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                MapLocation ml = (MapLocation) MBuddle.getSerializable(MAPLOC);
                if (ml != null) {
                    if(mapsGR == R.id.input_placeGoing) {
                        etPlaceFrom.setText(ml.getAddress());
                        lngGoing = ml.getLongitude();
                        latGoing = ml.getLatitude();
                    }
                    if(mapsGR == R.id.input_placeReturn) {
                        etPlaceTo.setText(ml.getAddress());
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
        mTimePicker = new TimePickerDialog(RideCreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                int result = view.getId();
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, selectedHour);
                c.set(Calendar.MINUTE, selectedMinute);
                SimpleDateFormat formatTime = new SimpleDateFormat("kk:mm");
                if(result == R.id.input_timeGoing){
                    timeG = formatTime.format(c.getTime());
                    etTimeGoing.setText(timeG);
                }
                else if(result == R.id.input_timeReturn){
                    timeR = formatTime.format(c.getTime());
                    etTimeReturn.setText(timeR);
                }
            }

        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private boolean validate(String placeG, String placeR, String price, String passengers) {
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
        }
        return valid;
    }
}
