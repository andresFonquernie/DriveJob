package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.local.MapLocation;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.utils.MapsActivity;

public class SearchRideActivity extends Fragment {

    private static final String TAG = "SearchRideActivity";

    @BindView(R.id.input_timeGoing)  EditText etTimeGoing;
    @BindView(R.id.input_timeReturn) EditText etTimeReturn;
    @BindView(R.id.input_placeGoing) EditText etPlaceFrom;
    @BindView(R.id.input_placeReturn) EditText etPlaceTo;
    @BindView(R.id.input_days) EditText etDays;
    @BindView(R.id.btn_search) Button btnSearch;

    //Google Maps
    int mapsGR;
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

    //Days of week
    @BindArray(R.array.daysofweek) String[] listDays;
    @BindArray(R.array.shortdaysofweek)String[] shortListDays;
    boolean[] checkedDays;
    ArrayList<Integer> mUserDays = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_search_ride, container, false);
        ButterKnife.bind(this, view);

        addOnclick();
        mUserDays = new ArrayList<>();
        checkedDays = new boolean[listDays.length];
        return view;
    }

    public void addOnclick(){
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRide();
            }
        });
        etPlaceFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMaps(v);
            }
        });
        etPlaceTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMaps(v);
            }
        });
        etTimeGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime(v);
            }
        });
        etTimeReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime(v);
            }
        });
        etDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDays(v);
            }
        });
    }

    public void searchRide(){
        List<Boolean> days = new ArrayList<>();
        for (boolean checkedDay : checkedDays) {
            days.add(checkedDay);
        }


        if(validate()) {
            Ride rs = new Ride(FirebaseUser.getUid(), timeG, timeR, latGoing, latReturning, lngGoing, lngReturning, days);
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.EXTRA_RIDE_SEARCH, rs);
            startActivity(intent);
        }
    }

    public void startMaps(View v){
        mapsGR = v.getId();
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivityForResult(intent, MAP_ACTIVITY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        final TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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
        mTimePicker.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                mTimePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                mTimePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });
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
