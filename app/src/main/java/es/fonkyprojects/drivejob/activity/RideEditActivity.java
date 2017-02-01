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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.MapLocation;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePutTask;
import es.fonkyprojects.drivejob.utils.Constants;

public class RideEditActivity extends Activity {

    private static final String TAG = "RideEditActivity";
    public static final String EXTRA_RIDE = "ride";
    public static final String EXTRA_RIDE_KEY = "ride_key";

    @Bind(R.id.edit_input_placeGoing) EditText etEditPlaceFrom;
    @Bind(R.id.edit_input_placeReturn) EditText etEditPlaceTo;
    private TextView etTimeGoing;
    private TextView etTimeReturn;
    @Bind(R.id.edit_input_price) EditText etPrice;
    @Bind(R.id.edit_input_passengers) EditText etPassengers;
    @Bind(R.id.btn_edit) Button btnEdit;


    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";

    private Ride mRide;

    public double latGoing;
    public double latReturning;
    public double lngGoing;
    public double lngReturning;
    public String timeG;
    public String timeR;
    int mapsGR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_edit);
        ButterKnife.bind(this);

        // Get post key from intent
        mRide = (Ride) getIntent().getSerializableExtra(EXTRA_RIDE);
        if (mRide == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Views
        etTimeGoing = (EditText) findViewById(R.id.edit_input_timeGoing);
        etTimeReturn = (EditText) findViewById(R.id.edit_input_timeReturn);

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
            etPassengers.setText(String.valueOf(mRide.getPassengers()));
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
        final int price = Integer.parseInt(etPrice.getText().toString());
        final int passengers = Integer.parseInt(etPassengers.getText().toString());


        if (validate(timeG, placeG, timeR, placeR, price, passengers)) {

            btnEdit.setEnabled(false);
            Toast.makeText(this, "Put", Toast.LENGTH_LONG).show();

            String putKey = writeEditRide(placeG, placeR, price, passengers);
            Ride r = new Ride(putKey,mRide.getAuthorID(), mRide.getAuthor(), timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, price, passengers);
            //SQLConnect sc = new SQLConnect();
            //sc.editRide(r);
            //sc.closeConnect();
            Log.e(TAG, "PUTKEY RESULT: " + putKey);

            if (putKey.equals("Update")) {
                // Go to RideDetailActivity
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

    private String writeEditRide(String placeG, String placeR,
                                int price,int passengers) {
        String result = "";
        try {
            Ride ride = new Ride("", "", timeG, timeR, placeG, placeR, latGoing, latReturning, lngGoing, lngReturning, price, passengers);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                MapLocation ml = (MapLocation) MBuddle .getSerializable(MAPLOC);
                Log.e("ENTRAMOS EN RESULT EDIT", ml.getAddress());
                if (ml != null) {
                    Log.e(TAG + "PG", String.valueOf(R.id.edit_input_placeGoing));
                    Log.e(TAG + "PR", String.valueOf(R.id.edit_input_placeReturn));
                    if(mapsGR == R.id.edit_input_placeGoing) {
                        Log.e(TAG + " IN", "SE MODIFICA ");
                        etEditPlaceFrom.setText("");
                        lngGoing = ml.getLongitude();
                        latGoing = ml.getLatitude();
                    }
                    if(mapsGR == R.id.edit_input_placeReturn) {
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
                if(result == R.id.edit_input_timeGoing){
                    timeG = formatTime.format(c.getTime());
                    etTimeGoing.setText(timeG);
                }
                else if(result == R.id.edit_input_timeReturn){
                    timeR = formatTime.format(c.getTime());
                    etTimeReturn.setText(timeR);
                }
            }

        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void startMaps(View v){
        mapsGR = v.getId();
        Log.e(TAG + " MapsGR", String.valueOf(mapsGR));
        Intent intent = new Intent(RideEditActivity.this, MapsActivity.class);
        startActivityForResult(intent, MAP_ACTIVITY);
    }

    private boolean validate(String timeG, String placeG, String timeR, String placeR, int price, int passengers) {
        boolean valid = true;

        if (placeG.isEmpty()) {
            etEditPlaceFrom.setError("Not null");
            valid = false;
        } else {
            etEditPlaceTo.setError(null);
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
}
