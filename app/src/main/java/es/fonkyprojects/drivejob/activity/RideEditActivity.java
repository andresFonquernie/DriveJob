package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.model.MapLocation;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePutTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.utils.MapsActivity;

public class RideEditActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "RideEditActivity";
    public static final String EXTRA_RIDE = "ride";

    @Bind(R.id.edit_placeGoing)  EditText etEditPlaceFrom;
    @Bind(R.id.edit_placeReturn) EditText etEditPlaceTo;
    @Bind(R.id.edit_timeGoing) EditText etTimeGoing;
    @Bind(R.id.edit_timeReturn) EditText etTimeReturn;
    @Bind(R.id.input_days) EditText etDays;
    @Bind(R.id.edit_price) EditText etPrice;
    @Bind(R.id.edit_passengers) EditText etPassengers;
    @Bind(R.id.edit_avseats) EditText etAvSeats;
    Spinner spinCar;
    @Bind(R.id.btn_edit) Button btnEdit;

    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";

    private Ride mRide;

    //Form
    private double latGoing;
    private double latReturning;
    private double lngGoing;
    private double lngReturning;
    private String timeG;
    private String timeR;
    private int oldPassengers;
    private String days;
    private String carID;

    //Google Maps
    private int mapsGR;

    //Days of week
    String[] listDays;
    String[] shortListDays;
    boolean[] checkedDays;
    ArrayList<Integer> mUserDays = new ArrayList<>();

    private List<Car> inpList;


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

        //Days
        listDays = getResources().getStringArray(R.array.daysofweek);
        shortListDays = getResources().getStringArray(R.array.shortdaysofweek);
        checkedDays = new boolean[listDays.length];
    }

    @Override
    public void onStart() {
        super.onStart();

        List<CharSequence> listCar = new ArrayList<>();
        //Check if Ride exists
        if (mRide != null) {
            etTimeGoing.setText(mRide.getTimeGoing());
            etTimeReturn.setText(mRide.getTimeReturn());
            etEditPlaceFrom.setText(mRide.getPlaceGoing());
            etEditPlaceTo.setText(mRide.getPlaceReturn());
            etPrice.setText(String.valueOf(mRide.getPrice()));
            oldPassengers = mRide.getPassengers();
            etPassengers.setText(String.valueOf(mRide.getPassengers()));
            etAvSeats.setText(String.valueOf(mRide.getAvSeats()));

            timeG = mRide.getTimeGoing();
            timeR = mRide.getTimeReturn();
            latGoing = mRide.getLatGoing();
            latReturning = mRide.getLatReturn();
            lngGoing = mRide.getLngGoing();
            lngReturning = mRide.getLngReturn();

            String userId = FirebaseUser.getUid();
            int pos = 0;
            try {
                GetTask ugt = new GetTask(this);
                String result = ugt.execute(Constants.BASE_URL + "car/?authorID=" + userId).get();
                Type type = new TypeToken<List<Car>>() {}.getType();
                inpList = new Gson().fromJson(result, type);
                for (int i = 0; i < inpList.size(); i++) {
                    Car c = inpList.get(i);
                    listCar.add(c.toString());
                    if(mRide.getCarID().equals(c.getId()))
                        pos=i;
                }

                spinCar = (Spinner) findViewById(R.id.spinner_car);
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCar);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinCar.setAdapter(adapter);
                spinCar.setSelection(pos);
                spinCar.setOnItemSelectedListener(this);
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            //Convert from array boolean to string of days
            String[] items = mRide.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            String day = "";
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals("true")) {
                    checkedDays[i] = true;
                    mUserDays.add(i);
                    day = day + shortListDays[i] + ",";
                }
            }
            day = day.substring(0, day.length() - 1);
            etDays.setText(day);
        }
    }

    public void editRide(View view) {
        Log.e(TAG, "GOING " + lngGoing + " " + latGoing);
        final String placeG = etEditPlaceFrom.getText().toString();
        final String placeR = etEditPlaceTo.getText().toString();
        final int avSeats = Integer.parseInt(etAvSeats.getText().toString());
        String days = Arrays.toString(checkedDays);
        String checkDays = etDays.getText().toString();

        //Check if price
        String sPrice = etPrice.getText().toString();
        final int price;
        if (sPrice.length() > 0)
            price = Integer.parseInt(sPrice);
        else
            price = 0;

        //Check if passengers
        String sPassengers = etPassengers.getText().toString();
        final int passengers;
        if (sPassengers.length() > 0)
            passengers = Integer.parseInt(sPassengers);
        else
            passengers = 0;

        if (validate(placeG, placeR, checkDays, sPrice, sPassengers, avSeats)) {

            btnEdit.setEnabled(false);
            Toast.makeText(this, "Put", Toast.LENGTH_LONG).show();

            int newAvSeats = avSeats - (oldPassengers - passengers);
            Ride r = new Ride(mRide.getID(), mRide.getAuthorID(), mRide.getAuthor(), timeG, timeR, placeG, placeR, latGoing, latReturning,
                    lngGoing, lngReturning, days, price, passengers, newAvSeats, carID);
            Log.e(TAG, "GOING " + lngGoing + " " + latGoing);
            String putKey = writeEditRide(r);
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

    private String writeEditRide(Ride ride) {
        String result = "";
        try {
            RidePutTask rpt = new RidePutTask(this);
            rpt.setRidePut(ride);
            result = rpt.execute(Constants.BASE_URL + "ride/" + mRide.getID()).get();
            return result;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void startMapsEdit(View v) {
        mapsGR = v.getId();
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, MAP_ACTIVITY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_ACTIVITY){ // If it was an ADD_ITEM, then add the new item and update the list
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                MapLocation ml = (MapLocation) MBuddle.getSerializable(MAPLOC);
                if (ml != null) {
                    if(mapsGR == R.id.edit_placeGoing) {
                        etEditPlaceFrom.setText(ml.getAddress());
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

    public void showTime(final View view) {
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
                if (result == R.id.edit_timeGoing) {
                    timeG = formatTime.format(c.getTime());
                    etTimeGoing.setText(timeG);
                } else if (result == R.id.edit_timeReturn) {
                    timeR = formatTime.format(c.getTime());
                    etTimeReturn.setText(timeR);
                }
            }

        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
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

    private boolean validate(String placeG, String placeR, String checkDays, String price, String passengers, int avSeats) {
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

        if (timeG != null && timeG.isEmpty()) {
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

        if (checkDays.isEmpty()) {
            etDays.setError("Not null");
            valid = false;
        } else {
            etDays.setError(null);
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
            int checkPass = avSeats + Integer.parseInt(passengers);
            if (checkPass < oldPassengers) {
                Toast.makeText(this, "Minimun passengers: " + (oldPassengers - avSeats), Toast.LENGTH_LONG).show();
            }
        }
        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        carID = inpList.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
