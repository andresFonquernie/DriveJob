package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.model.Messaging;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.UserDays;
import es.fonkyprojects.drivejob.model.local.MapLocation;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.restMethods.Messaging.MessagingPostTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePutTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.MapsActivity;

public class RideEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "RideEditActivity";
    public static final String EXTRA_RIDE = "ride";
    private Ride mRide;

    //Initialize Views
    @BindView(R.id.edit_placeGoing)  EditText etEditPlaceFrom;
    @BindView(R.id.edit_placeReturn) EditText etEditPlaceTo;
    @BindView(R.id.edit_timeGoing) EditText etTimeGoing;
    @BindView(R.id.edit_timeReturn) EditText etTimeReturn;
    @BindView(R.id.input_days) EditText etDays;
    @BindView(R.id.edit_price) EditText etPrice;
    @BindView(R.id.edit_passengers) EditText etPassengers;
    @BindView(R.id.edit_avSeatsDay) EditText etAvSeatsDay;
    @BindView(R.id.spinner_car) Spinner spinCar;
    @BindView(R.id.btn_edit) Button btnEdit;
    @BindView(R.id.editToolbar) Toolbar toolbar;

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
    public static final int MAP_ACTIVITY = 3;
    public static final String MAPLOC = "MAPLOC";

    //Days of week
    @BindArray(R.array.daysofweek) String[] listDays;
    @BindArray(R.array.shortdaysofweek)String[] shortListDays;
    boolean[] checkedDays;
    ArrayList<Integer> mUserDays = new ArrayList<>();

    //Cars
    private List<Car> inpList;
    int selection;

    //Compare old vs new
    private int oldPassengers;
    private List<Integer> oldAvSeatsDay;
    private List<Boolean> oldDays;
    private List<Integer> avSeatsDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get post key from intent
        mRide = (Ride) getIntent().getSerializableExtra(EXTRA_RIDE);
        if (mRide == null) {
            throw new IllegalArgumentException("Must pass EXTRA_RIDE");
        }

        //Initialize Days
        checkedDays = new boolean[listDays.length];
        mUserDays = new ArrayList<>();

        oldDays = new ArrayList<>();
        oldAvSeatsDay = new ArrayList<>();
        avSeatsDay = new ArrayList<>(listDays.length);
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
            oldDays = mRide.getDays();
            String day = "";
            for (int i = 0; i < oldDays.size(); i++) {
                if (oldDays.get(i)) {
                    checkedDays[i] = true;
                    mUserDays.add(i);
                    day = day + shortListDays[i] + ",";
                }
            }
            day = day.substring(0, day.length() - 1);
            etDays.setText(day);

            //Get avSeats
            oldAvSeatsDay = mRide.getAvSeats();
            etAvSeatsDay.setText(oldAvSeatsDay.toString());

            //Get allCars from User
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

                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCar);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinCar.setAdapter(adapter);
                spinCar.setSelection(pos);
                spinCar.setOnItemSelectedListener(this);
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            //Initialize variables, if no changes
            latGoing = mRide.getLatGoing();
            latReturn = mRide.getLatReturn();
            lngGoing = mRide.getLngGoing();
            lngReturn = mRide.getLngReturn();
            timeG = mRide.getTimeGoing();
            timeR = mRide.getTimeReturn();
        }
    }

    public void editRide(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.updating_ride));
        progressDialog.show();

        String placeFrom = etEditPlaceFrom.getText().toString();
        String placeTo = etEditPlaceTo.getText().toString();
        String sPrice = etPrice.getText().toString();
        String sPassengers = etPassengers.getText().toString();
        String validateDays = etDays.getText().toString();

        if (validate(placeFrom, placeTo, validateDays, sPrice, sPassengers)) {

            btnEdit.setEnabled(false);
            Toast.makeText(this, "Put", Toast.LENGTH_LONG).show();
            List<Boolean> days = new ArrayList<>();
            for (boolean checkedDay : checkedDays) {
                if (checkedDay) {
                    days.add(true);
                } else {
                    days.add(false);
                }

            }

            Ride r = new Ride(timeG, timeR, placeFrom, placeTo, latGoing, latReturn, lngGoing, lngReturn, days, avSeatsDay,
                    price, passengers, carID);
            String putKey = writeEditRide(r);
            r.setID(mRide.getID());
            (new SQLConnect()).updateRide(r, engineId);

            if (putKey.equals("Update")) {
                sendMessage();
                progressDialog.dismiss();
                Intent intent = new Intent(RideEditActivity.this, RideDetailActivity.class);
                intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, mRide.getID());
                startActivity(intent);
                finish();
            } else {
                progressDialog.dismiss();
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
        if(mapsGR == etEditPlaceFrom.getId()) {
            if (!etEditPlaceFrom.getText().toString().equals("")) {
                intent.putExtra(MapsActivity.EXTRA_TEXT, etEditPlaceFrom.getText().toString());
                intent.putExtra(MapsActivity.EXTRA_LNG, lngGoing);
                intent.putExtra(MapsActivity.EXTRA_LAT, latGoing);
            }
        }
        else if(mapsGR == etEditPlaceTo.getId()) {
            if (!etEditPlaceTo.getText().toString().equals("")) {
                intent.putExtra(MapsActivity.EXTRA_TEXT, etEditPlaceTo.getText().toString());
                intent.putExtra(MapsActivity.EXTRA_LNG, lngReturn);
                intent.putExtra(MapsActivity.EXTRA_LAT, latReturn);
            }
        }
        startActivityForResult(intent, MAP_ACTIVITY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_ACTIVITY){ // If it was an ADD_ITEM, then add the new item and update the list
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                MapLocation ml = (MapLocation) MBuddle.getSerializable(MAPLOC);
                if (ml != null) {
                    if(mapsGR == etEditPlaceFrom.getId()) {
                        etEditPlaceFrom.setText(ml.getAddress());
                        lngGoing = ml.getLongitude();
                        latGoing = ml.getLatitude();
                    }
                    if(mapsGR == etEditPlaceTo.getId()) {
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
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        if(view.getId() == etTimeGoing.getId()){
            if(timeG!=null){
                String[] time = timeG.split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            }
        } else if(view.getId() == etTimeReturn.getId()){
            if(timeR!=null){
                String[] time = timeR.split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            }
        }

        final TimePickerDialog mTimePicker;
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
        mTimePicker.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                mTimePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                mTimePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });
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

    private void sendMessage() {
        List<UserDays> ud = new ArrayList<>(mRide.getRequest());
        ud.addAll(mRide.getJoin());
        for(int i=0; i<ud.size(); i++){
            MessagingPostTask mpt = new MessagingPostTask(this);
            Messaging m;
            m = new Messaging(mRide.getAuthor(), ud.get(i).getUserId(), mRide.getID(), 50);
            mpt.setMessaging(m);
            try {
                String result = mpt.execute(Constants.MESSAGING_URL).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
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
        for(int i = 0; i<checkedDays.length; i++){
            if(oldDays.get(i)){ //TRUE, antes teníamos el día seleccionado
                int intNewAvSeatsDay = oldAvSeatsDay.get(i) + passengers - oldPassengers;
                if(intNewAvSeatsDay<0){ //TRUE, hay un pasajero sin asiento
                    valid=false;
                    etPassengers.setError(getText(R.string.notAvSeatsSpace));
                } else //Todos los pasajeros tienen asiento
                    if(!checkedDays[i]){ //TRUE, hemos eliminado el día
                        if (intNewAvSeatsDay<passengers) { //TRUE, había pasajeros en el  día que hemos eliminado
                                    valid = false;
                            etPassengers.setError(getText(R.string.notAvSeatsSpace));
                        } else { // No había pasajeros en el  día que hemos eliminado
                            avSeatsDay.add(0);
                        }
                    } else { //El día está seleccionado y añadimos los pasajeros resultantes
                        avSeatsDay.add(intNewAvSeatsDay);
                    }
            } else if(checkedDays[i]){ //El día es nuevo
                avSeatsDay.add(passengers);
            }
            else { //Ni estaba antes ni lo hemos añadido
                avSeatsDay.add(0);
            }
        }
        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(selection < 0) {
            carID = inpList.get(position).getId();
            engineId = inpList.get(position).getEngineID();
        }
        else{
            spinCar.setSelection(selection);
            carID = inpList.get(position).getId();
            engineId = inpList.get(position).getEngineID();
            selection = -1;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_edit_ok:
                editRide(this.findViewById(android.R.id.content));
                break;
            case R.id.mnu_edit_cancel:
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
