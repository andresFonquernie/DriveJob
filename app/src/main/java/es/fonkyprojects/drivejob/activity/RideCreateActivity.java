package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.MapLocation;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePostTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.utils.MapsActivity;

public class RideCreateActivity extends Activity{

    private static final String TAG = "NewRideActivity";

    @Bind(R.id.input_placeGoing) EditText etPlaceFrom;
    @Bind(R.id.input_placeReturn) EditText etPlaceTo;
    @Bind(R.id.input_timeGoing)  EditText etTimeGoing;
    @Bind(R.id.input_timeReturn) EditText etTimeReturn;
    @Bind(R.id.input_days) EditText etDays;
    @Bind(R.id.input_price) EditText etPrice;
    @Bind(R.id.input_passengers) EditText etPassengers;
    @Bind(R.id.btn_create) Button btnCreate;

    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";

    //Form
    public String userID;
    public String username;
    public String timeG;
    public String timeR;
    public double latGoing;
    public double latReturning;
    public double lngGoing;
    public double lngReturning;

    //Google Maps
    int mapsGR;

    //Days of week
    String[] listDays;
    String[] shortListDays;
    boolean[] checkedDays;
    ArrayList<Integer> mUserDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_create);
        ButterKnife.bind(this);

        listDays = getResources().getStringArray(R.array.daysofweek);
        shortListDays = getResources().getStringArray(R.array.shortdaysofweek);
        checkedDays = new boolean[listDays.length];

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
        String days = Arrays.toString(checkedDays);
        String checkDays = etDays.getText().toString();

        //Check if price
        String sPrice = etPrice.getText().toString();
        final int price;
        if(sPrice.length()>0)
            price = Integer.parseInt(sPrice);
        else
            price = 0;

        //Check if passengers
        String sPassengers = etPassengers.getText().toString();
                final int passengers;
        if(sPassengers.length()>0)
            passengers = Integer.parseInt(sPassengers);
        else
            passengers = 0;

        if (validate(placeG, placeR, checkDays, sPrice, sPassengers)) {

            btnCreate.setEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

            String postKey = writeNewRide(placeG, placeR, days, price, passengers);
            Ride r = new Ride(postKey,userID, username, timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, days, price, passengers, passengers);
            String s = (new SQLConnect()).insertRide(r);
            Log.e(TAG, s);
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

     private String writeNewRide(String placeG, String placeR, String days, int price,int passengers) {
        String result = "";
        try {
            userID = FirebaseUser.getUid();
            username = getUsername(userID);

            Ride ride = new Ride(userID, username, timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, days, price, passengers, passengers);
            RidePostTask rpt = new RidePostTask(this);
            rpt.setRidePost(ride);
            result = rpt.execute(Constants.BASE_URL + "ride").get();

            Log.e(TAG, "RESULT WRITE RIDE: " + result);
            Ride r = new Gson().fromJson(result, Ride.class);
            result = r.getID();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
         return result;
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

    private String getUsername(String userId) throws ExecutionException, InterruptedException {
        String result;
        GetTask ugt = new GetTask(this);
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
        mTimePicker.setTitle(R.string.selectTime);
        mTimePicker.show();
    }

    public void selectDays(View v) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
        mBuilder.setTitle(R.string.days);
        mBuilder.setMultiChoiceItems(listDays, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    mUserDays.add(position);
                } else {
                    mUserDays.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                Collections.sort(mUserDays);
                for (int i = 0; i < mUserDays.size(); i++) {
                    item = item + shortListDays[mUserDays.get(i)];
                    if (i != mUserDays.size() - 1) {
                        item = item + ", ";
                    }
                }
                etDays.setText(item);
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedDays.length; i++) {
                    checkedDays[i] = false;
                    mUserDays.clear();
                    etDays.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private boolean validate(String placeG, String placeR, String checkDays, String price, String passengers) {
        boolean valid = true;

        if (placeG == null || placeG.isEmpty()) {
            etPlaceFrom.setError("Not null");
            valid = false;
        } else {
            etPlaceFrom.setError(null);
        }

        if (placeR == null || placeR.isEmpty()) {
            etPlaceTo.setError("Not null");
            valid = false;
        } else {
            etPlaceTo.setError(null);
        }

        if (timeG == null || timeG.isEmpty()) {
            etTimeGoing.setError("Not null");
            valid = false;
        } else {
            etTimeGoing.setError(null);
        }

        if (timeR == null || timeR.isEmpty()) {
            etTimeReturn.setError("Not null");
            valid = false;
        } else {
            etTimeReturn.setError(null);
        }

        if (checkDays == null || checkDays.isEmpty()) {
            etDays.setError("Not null");
            valid = false;
        } else {
            etDays.setError(null);
        }

        if (price == null || price.isEmpty()) {
            etPrice.setError("Not 0");
            valid = false;
        } else {
            etPrice.setError(null);
        }

        if (passengers == null || passengers.isEmpty()) {
            etPassengers.setError("Not 0");
            valid = false;
        } else {
            etPassengers.setError(null);
        }
        return valid;
    }
}
