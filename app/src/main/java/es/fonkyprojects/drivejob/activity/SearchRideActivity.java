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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

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
    @Bind(R.id.input_days) EditText etDays;
    @Bind(R.id.btn_search) Button btnSearch;

    public static final int MAP_ACTIVITY = 0;
    public static final String MAPLOC = "MAPLOC";

    //Form
    public double latGoing;
    public double latReturning;
    public double lngGoing;
    public double lngReturning;
    public String timeG;
    public String timeR;
    public String days;

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
        setContentView(R.layout.activity_search_ride);
        ButterKnife.bind(this);

        listDays = getResources().getStringArray(R.array.daysofweek);
        shortListDays = getResources().getStringArray(R.array.shortdaysofweek);
        checkedDays = new boolean[listDays.length];

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRide(view);
            }
        });
    }

    public void searchRide(final View view){
        days = Arrays.toString(checkedDays);

        if(validate()) {
            Log.e(TAG, timeG + " " + timeR + " " + latGoing + " " + latReturning + " " + lngGoing + " " + lngReturning + " " + days);
            RideSearch rs = new RideSearch(FirebaseUser.getUid(), timeG, timeR, latGoing, latReturning, lngGoing, lngReturning, days);
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

    //Get Hour
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

    //Select Days of Week
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
        if (days != null && days.isEmpty()) {
            etDays.setError("Not null");
            valid = false;
        } else {
            etDays.setError(null);
        }
        return valid;
    }
}
