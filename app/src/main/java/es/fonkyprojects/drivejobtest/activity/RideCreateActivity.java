package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.model.UserDays;
import es.fonkyprojects.drivejob.model.local.MapLocation;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePostTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.utils.MapsActivity;

public class RideCreateActivity extends Activity implements AdapterView.OnItemSelectedListener{

    //private static final String TAG = "CreateRideActivity";

    @BindView(R.id.input_placeGoing) EditText etPlaceFrom;
    @BindView(R.id.input_placeReturn) EditText etPlaceTo;
    @BindView(R.id.input_timeGoing)  EditText etTimeGoing;
    @BindView(R.id.input_timeReturn) EditText etTimeReturn;
    @BindView(R.id.input_days) EditText etDays;
    @BindView(R.id.input_price) EditText etPrice;
    @BindView(R.id.input_passengers) EditText etPassengers;
    @BindView(R.id.spinner_car) Spinner spinCar;
    @BindView(R.id.btn_create) Button btnCreate;


    //Form
    private String userID;
    private String placeFrom;
    private String placeTo;
    private String timeGoing;
    private String timeReturn;
    private double latGoing;
    private double latReturning;
    private double lngGoing;
    private double lngReturning;
    private int price;
    private int passengers;
    private String carID;
    private int engineId;

    //Google Maps
    private int mapsGR;
    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";


    //Days of week
    @BindArray(R.array.daysofweek) String[] listDays;
    @BindArray(R.array.shortdaysofweek) String[] shortListDays;
    private boolean[] checkedDays;
    private ArrayList<Integer> mUserDays;

    //Cars
    private List<Car> inpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_create);
        ButterKnife.bind(this);

        checkedDays = new boolean[listDays.length];
        mUserDays = new ArrayList<>();

        userID = FirebaseUser.getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();

       List<CharSequence> listCar = new ArrayList<>();
        try {
            GetTask ugt = new GetTask(this);
            String result = ugt.execute(Constants.BASE_URL + "car/?authorID=" + userID).get();
            Type type = new TypeToken<List<Car>>(){}.getType();
            inpList = new Gson().fromJson(result, type);
            for(int i=0; i<inpList.size(); i++){
                Car c = inpList.get(i);
                listCar.add(c.toString());
            }

            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCar);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinCar.setAdapter(adapter);
            spinCar.setSelection(0);
            spinCar.setOnItemSelectedListener(this);

            carID = inpList.get(0).getId();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void createRide(View view){

        placeFrom = etPlaceFrom.getText().toString();
        placeTo = etPlaceTo.getText().toString();
        String sPrice = etPrice.getText().toString();
        String sPassengers = etPassengers.getText().toString();
        String validateDays = etDays.getText().toString();

        if (validate(placeFrom, placeTo, validateDays, sPrice, sPassengers)) {

            btnCreate.setEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

            List<Boolean> days = new ArrayList<>();
            List<Integer> avSeatsDay = new ArrayList<>();
            for (boolean checkedDay : checkedDays) {
                if (checkedDay) {
                    days.add(true);
                    avSeatsDay.add(passengers);
                } else {
                    days.add(false);
                    avSeatsDay.add(0);
                }
            }

            List<UserDays> request = new ArrayList<>();
            List<UserDays> join = new ArrayList<>();

            String username = getUsername(userID);
            Ride ride = new Ride(userID, username, timeGoing, timeReturn, placeFrom, placeTo, latGoing, latReturning, lngGoing,
                    lngReturning, days, avSeatsDay, price, passengers, carID, request, join);
            String postKey = writeNewRide(ride);
            ride.setID(postKey);
            //TODO
            //(new SQLConnect()).insertRide(ride, engineId);

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

    private String writeNewRide(Ride ride) {
        String result = "";
        try {
            RidePostTask rpt = new RidePostTask(this);
            rpt.setRidePost(ride);
            result = rpt.execute(Constants.BASE_URL + "ride").get();
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
                Bundle mBundle = data.getExtras();
                MapLocation ml = (MapLocation) mBundle.getSerializable(MAPLOC);
                if (ml != null) {
                    if(mapsGR == R.id.input_placeGoing) {
                        etPlaceFrom.setText(ml.getAddress());
                        placeFrom = ml.getAddress();
                        lngGoing = ml.getLongitude();
                        latGoing = ml.getLatitude();
                    }
                    if(mapsGR == R.id.input_placeReturn) {
                        etPlaceTo.setText(ml.getAddress());
                        placeTo = ml.getAddress();
                        lngReturning = ml.getLongitude();
                        latReturning = ml.getLatitude();
                    }
                }
            }
        }
    }

    private String getUsername(String userId) {
        String result;
        try {
            GetTask ugt = new GetTask(this);
            result = ugt.execute(Constants.BASE_URL + "user/?userId=" + userId).get();

            Type type = new TypeToken<List<User>>(){}.getType();
            List<User> inpList = new Gson().fromJson(result, type);
            User u = inpList.get(0);
            return u.getUsername() + " " + u.getSurname();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void startMaps(View v){
        mapsGR = v.getId();
        Intent intent = new Intent(this, MapsActivity.class);
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
                    timeGoing = formatTime.format(c.getTime());
                    etTimeGoing.setText(timeGoing);
                }
                else if(result == R.id.input_timeReturn){
                    timeReturn = formatTime.format(c.getTime());
                    etTimeReturn.setText(timeReturn);
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
                    if (i!= mUserDays.size()-1) {
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

    private boolean validate(String sPlaceFrom, String sPlaceTo, String sCheckDays, String sPrice, String sPassengers) {
        boolean valid = true;

        if (sPlaceFrom.isEmpty()) {
            etPlaceFrom.setError(getText(R.string.notNull));
            valid = false;
        } else {  etPlaceFrom.setError(null); }
        if (sPlaceTo.isEmpty()) {
            etPlaceTo.setError(getText(R.string.notNull));
            valid = false;
        } else { etPlaceTo.setError(null);  }
        if (timeGoing == null || timeGoing.isEmpty()) {
            etTimeGoing.setError(getText(R.string.notNull));
            valid = false;
        } else { etTimeGoing.setError(null); }
        if (timeGoing == null || timeReturn.isEmpty()) {
            etTimeReturn.setError(getText(R.string.notNull));
            valid = false;
        } else { etTimeReturn.setError(null); }
        if (sCheckDays.isEmpty()) {
            etDays.setError(getText(R.string.notNull));
            valid = false;
        } else { etDays.setError(null); }
        if (sPrice.isEmpty() || Integer.parseInt(sPrice)==0) {
            etPrice.setError(getText(R.string.notZero));
            valid = false;
        } else {
            etPrice.setError(null);
            price = Integer.parseInt(sPrice);
        }
        if (sPassengers.isEmpty() || Integer.parseInt(sPassengers)==0) {
            etPassengers.setError(getText(R.string.notZero));
            valid = false;
        } else {
            etPassengers.setError(null);
            passengers = Integer.parseInt(sPassengers);
        }
        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        carID = inpList.get(position).getId();
        engineId = inpList.get(position).getEngineID();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
