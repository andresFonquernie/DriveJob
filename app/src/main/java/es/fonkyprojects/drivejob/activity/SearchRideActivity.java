package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchRideActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "NewRideActivity";

    @Bind(R.id.input_timeGoing)  EditText etTimeGoing;
    @Bind(R.id.input_placeGoing) EditText etPlaceGoing;
    @Bind(R.id.input_timeReturn) EditText etTimeReturn;
    @Bind(R.id.input_placeReturn) EditText etPlaceReturn;
    @Bind(R.id.btn_search) Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride);
        ButterKnife.bind(this);

        btnSearch.setOnClickListener(this);
        etTimeGoing.setOnClickListener(this);
        etTimeReturn.setOnClickListener(this);
    }

    private void searchRide(){

        final String timeG = etTimeGoing.getText().toString();
        final String placeG = etPlaceGoing.getText().toString();
        final String timeR = etTimeReturn.getText().toString();
        final String placeR = etPlaceReturn.getText().toString();

        if(validate(timeG, placeG, timeR, placeR)) {

            btnSearch.setEnabled(false);
            Intent intent = new Intent(this, SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.EXTRA_TIME_GOING, timeG);
            intent.putExtra(SearchResultActivity.EXTRA_PLACE_GOING, placeG);
            intent.putExtra(SearchResultActivity.EXTRA_TIME_RETURN, timeR);
            intent.putExtra(SearchResultActivity.EXTRA_PLACE_RETURN, placeR);

            startActivity(intent);

        }
    }

    private void showTime(final String text){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SearchRideActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if(text.equals("going"))
                    etTimeGoing.setText( selectedHour + ":" + selectedMinute);
                else if(text.equals("return"))
                    etTimeReturn.setText( selectedHour + ":" + selectedMinute);
            }

        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private boolean validate(String timeG, String placeG, String timeR, String placeR) {
        boolean valid = true;

        if (timeG.isEmpty()) {
            etTimeGoing.setError("Not null");
            valid = false;
        } else {
            etTimeGoing.setError(null);
        }

        if (placeG.isEmpty()) {
            etPlaceGoing.setError("Not null");
            valid = false;
        } else {
            etPlaceGoing.setError(null);
        }

        if (timeR.isEmpty()) {
            etTimeReturn.setError("Not null");
            valid = false;
        } else {
            etTimeReturn.setError(null);
        }

        if (placeR.isEmpty()) {
            etPlaceReturn.setError("Not null");
            valid = false;
        } else {
            etPlaceReturn.setError(null);
        }
        return valid;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_search) {
            searchRide();
        } else if (i == R.id.input_timeGoing) {
            showTime("going");
        } else if (i == R.id.input_timeReturn) {
            showTime("return");
        }
    }
}
