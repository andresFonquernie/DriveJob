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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.MapLocation;
import es.fonkyprojects.drivejob.model.RideSearch;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.utils.MapsActivity;

public class SearchRideActivity extends Activity {

    private static final String TAG = "NewRideActivity";

    @Bind(R.id.input_timeGoing)  EditText etTimeGoing;
    @Bind(R.id.input_timeReturn) EditText etTimeReturn;
    @Bind(R.id.input_placeGoing) EditText etPlaceFrom;
    @Bind(R.id.input_placeReturn) EditText etPlaceTo;
    @Bind(R.id.btn_search) Button btnSearch;

    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";

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
        setContentView(R.layout.activity_search_ride);
        ButterKnife.bind(this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRide(view);
            }
        });
    }

    public void searchRide(final View view){
        if(validate()) {
            Log.e(TAG, timeG + " " + timeR + " " + latGoing + " " + latReturning + " " + lngGoing + " " + lngReturning);
            RideSearch rs = new RideSearch(FirebaseUser.getUid(), timeG, timeR, latGoing, latReturning, lngGoing, lngReturning);
            Intent intent = new Intent(this, SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.EXTRA_RIDE_SEARCH, rs);
            startActivity(intent);
        }
    }

    public void startMaps(View v){
        mapsGR = v.getId();
        Intent intent = new Intent(SearchRideActivity.this, MapsActivity.class);
        startActivityForResult(intent, MAP_ACTIVITY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_ACTIVITY){ // If it was an ADD_ITEM, then add the new item and update the list
            if(resultCode == Activity.RESULT_OK){
                Bundle MBuddle = data.getExtras();
                MapLocation ml = (MapLocation) MBuddle .getSerializable(MAPLOC);
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
        mTimePicker = new TimePickerDialog(SearchRideActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    private boolean validate() {
        boolean valid = true;

        if (latGoing == 0) {
            etPlaceFrom.setError("Not null");
            valid = false;
        } else {
            etPlaceFrom.setError(null);
        }
        if (latReturning == 0) {
            etPlaceTo.setError("Not null");
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
        return valid;
    }
}
