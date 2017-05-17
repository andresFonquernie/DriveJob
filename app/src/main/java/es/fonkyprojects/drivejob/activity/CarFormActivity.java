package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.restMethods.Car.CarPostTask;
import es.fonkyprojects.drivejob.restMethods.Car.CarPutTask;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class CarFormActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "FormCarActivity";
    public static final String EXTRA_CAR = "car";

    private boolean create = true;
    private int engineId = 0;

    @Bind(R.id.input_brand) EditText etBrand;
    @Bind(R.id.input_model) EditText etModel;
    Spinner etEngine;
    @Bind(R.id.btn_addCar) Button btnCar;

    //Form
    private String mCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_form);
        ButterKnife.bind(this);

        etEngine = (Spinner) findViewById(R.id.input_engine);
        ArrayAdapter<CharSequence> listEngine = ArrayAdapter.createFromResource(this, R.array.engineType, android.R.layout.simple_spinner_item);
        listEngine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etEngine.setAdapter(listEngine);
        etEngine.setSelection(engineId);
        etEngine.setOnItemSelectedListener(this);

        mCar = getIntent().getStringExtra(EXTRA_CAR);
        if (mCar != null) {
            create = false;
            btnCar.setText(getString(R.string.edit_car));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Check if Ride exists
        if (mCar != null) {
            try {
                String result = new GetTask(this).execute(Constants.BASE_URL + "car/" + mCar).get();
                Car car = new Gson().fromJson(result, Car.class);
                etBrand.setText(car.getBrand());
                etModel.setText(car.getModel());
                etEngine.setSelection(car.getEngineID());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    public void getData(View view){
        String brand = etBrand.getText().toString();
        String model = etModel.getText().toString();
        if(!validate(brand, model)){
            formError();
            return;
        }

        btnCar.setEnabled(false);

        //Progress Dialog
        final ProgressDialog progressDialog = new ProgressDialog(CarFormActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating car...");
        progressDialog.show();

        String result;
        if(create)
            result = writeNewCar(brand, model);
        else
            result = editCar(brand, model);

        if(result.equals("Update") || !result.equals("Error")){
            progressDialog.dismiss();
            btnCar.setEnabled(true);
            Intent intent = new Intent(getApplicationContext(),MyCarActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    private void formError(){
        Toast.makeText(getBaseContext(), "Error in form", Toast.LENGTH_LONG).show();
        btnCar.setEnabled(true);
    }

    private String writeNewCar(String brand, String model){
        String result = "";
        try {
            CarPostTask cpt = new CarPostTask(this);
            cpt.setCarPost(new Car(FirebaseUser.getUid(), brand, model, engineId));
            result = cpt.execute(Constants.BASE_URL + "car").get();
            Car c = new Gson().fromJson(result, Car.class);
            Log.e(TAG, "RESULT: " + result);
            Log.e(TAG, c.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String editCar(String brand, String model){
        String result = "";
        try {
            CarPutTask cpt = new CarPutTask(this);
            cpt.setCarPut(new Car(FirebaseUser.getUid(), brand, model, engineId));
            result = cpt.execute(Constants.BASE_URL + "car/" + mCar).get();
            Log.e(TAG, "RESULT PUT CAR: " + result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean validate(String brand, String model) {
        boolean valid = true;

        if (brand.isEmpty()) {
            etBrand.setError(getText(R.string.notNull));
            valid = false;
        } else {
            etBrand.setError(null);
        }

        if (model.isEmpty()) {
            etModel.setError("Not null");
            valid = false;
        } else {
            etModel.setError(null);
        }
        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        engineId = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
