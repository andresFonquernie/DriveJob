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
    @Bind(R.id.edit_avSeatsDay) EditText etAvSeatsDay;
    Spinner spinCar;
    @Bind(R.id.btn_edit) Button btnEdit;

    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";

    private Ride mRide;

    //Form
    private double latGoing;
    private double latReturn;
    private double lngGoing;
    private double lngReturn;
    private String timeG;
    private String timeR;
    private int price;
    private int passengers;
    private String carID;
    private int engineId;

    //Google Maps
    private int mapsGR;

    //Days of week
    String[] listDays;
    String[] shortListDays;
    boolean[] checkedDays;
    ArrayList<Integer> mUserDays = new ArrayList<>();

    private List<Car> inpList;

    //Compare old vs new
    private int oldPassengers;
    private int[] oldAvSeatsDay;
    private String[] oldDays = new String[7];
    private int[] avSeatsDay = new int[7];


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

            //Convert from array boolean to string of days
            String[] items = mRide.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            oldDays = mRide.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
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

            //Get avSeatsDay
            String[] stringAvSeatsDay = mRide.getAvSeatsDay().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            oldAvSeatsDay = new int[stringAvSeatsDay.length];
            for (int i = 0; i<stringAvSeatsDay.length; i++) {
                oldAvSeatsDay[i] = Integer.parseInt(stringAvSeatsDay[i]);
            }
            etAvSeatsDay.setText(mRide.getAvSeatsDay().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", ""));

            //Get allCars
            String userId = mRide.getAuthorID();
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

            //Initializr variables, if no changes
            latGoing = mRide.getLatGoing();
            latReturn = mRide.getLatReturn();
            lngGoing = mRide.getLngGoing();
            lngReturn = mRide.getLngReturn();
            timeG = mRide.getTimeGoing();
            timeR = mRide.getTimeReturn();
        }
    }

    public void editRide(View view) {
        String placeFrom = etEditPlaceFrom.getText().toString();
        String placeTo = etEditPlaceTo.getText().toString();
        String sPrice = etPrice.getText().toString();
        String sPassengers = etPassengers.getText().toString();
        String days = Arrays.toString(checkedDays);
        String validateDays = etDays.getText().toString();

        if (validate(placeFrom, placeTo, validateDays, sPrice, sPassengers)) {

            btnEdit.setEnabled(false);
            Toast.makeText(this, "Put", Toast.LENGTH_LONG).show();

            Ride r = new Ride(timeG, timeR, placeFrom, placeTo, latGoing, latReturn, lngGoing, lngReturn, days, price, passengers,
                    Arrays.toString(avSeatsDay), carID);
            String putKey = writeEditRide(r);
            r.setID(mRide.getID());
            (new SQLConnect()).updateRide(r, engineId);

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
                        lngReturn = ml.getLongitude();
                        latReturn = ml.getLatitude();
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

    private boolean validate(String sPlaceFrom, String sPlaceTo, String sCheckDays, String sPrice, String sPassengers) {
        boolean valid = true;
        if (sPlaceFrom.isEmpty()) {
            etEditPlaceFrom.setError(getText(R.string.notNull));
            valid = false;
        } else {  etEditPlaceFrom.setError(null); }
        if (sPlaceTo.isEmpty()) {
            etEditPlaceTo.setError(getText(R.string.notNull));
            valid = false;
        } else { etEditPlaceTo.setError(null);  }
        if (timeG == null || timeG.isEmpty()) {
            etTimeGoing.setError(getText(R.string.notNull));
            valid = false;
        } else { etTimeGoing.setError(null); }
        if (timeG == null || timeR.isEmpty()) {
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
        //Check if avSeatsDay > 0 for selectDays
        for(int i = 0; i<avSeatsDay.length; i++){
            if(oldDays[i].equals("true")){
                int intNewAvSeatsDay = oldAvSeatsDay[i] + passengers - oldPassengers;
                if(intNewAvSeatsDay<0){
                    valid=false;
                    etPassengers.setError(getText(R.string.notAvSeatsSpace));
                } else if(!checkedDays[i]){
                    if (intNewAvSeatsDay<passengers) {
                        valid = false;
                        etPassengers.setError(getText(R.string.notAvSeatsSpace));
                    } else {
                        avSeatsDay[i] = 0;
                    }
                }
                else {
                    avSeatsDay[i] = intNewAvSeatsDay;
                }
            } else if(checkedDays[i]){
                avSeatsDay[i] = passengers;
            }
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
