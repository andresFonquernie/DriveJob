package es.fonkyprojects.drivejob.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.restMethods.Car.CarPostTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class CarFormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "FormCarActivity";

    private int engineId = 0;

    @BindView(R.id.input_brand) EditText etBrand;
    @BindView(R.id.input_model) EditText etModel;
    @BindView(R.id.btn_addCar) Button btnCar;
    @BindView(R.id.input_engine) Spinner etEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_form);
        ButterKnife.bind(this);

        //Spinner
        ArrayAdapter<CharSequence> listEngine = ArrayAdapter.createFromResource(this, R.array.engineType, android.R.layout.simple_spinner_item);
        listEngine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etEngine.setAdapter(listEngine);
        etEngine.setSelection(engineId);
        etEngine.setOnItemSelectedListener(this);
    }

    public void writeCar(View view){
        String brand = etBrand.getText().toString();
        String model = etModel.getText().toString();
        if(!validate(brand, model)){
            formError();
            return;
        }

        btnCar.setEnabled(false);

        //Progress Dialog
        final ProgressDialog progressDialog = new ProgressDialog(CarFormActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating car...");
        progressDialog.show();

        String result = postCar(brand, model);

        if(!result.equals("Error")){
            progressDialog.dismiss();
            btnCar.setEnabled(true);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
        }
    }

    private void formError(){
        Toast.makeText(getBaseContext(), "Error in form", Toast.LENGTH_LONG).show();
        btnCar.setEnabled(true);
    }

    private String postCar(String brand, String model){
        String result = "";
        try {
            CarPostTask cpt = new CarPostTask(this);
            cpt.setCarPost(new Car(FirebaseUser.getUid(), brand, model, engineId));
            result = cpt.execute(Constants.BASE_URL + "car").get();
            Car c = new Gson().fromJson(result, Car.class);
        } catch (InterruptedException | ExecutionException e) {
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
