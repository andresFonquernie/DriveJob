package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.restMethods.DeleteTask;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.viewholder.CarViewAdapter;

public class MyCarActivity extends AppCompatActivity {

    private static final String TAG = "MyCarActivity";

    @Bind(R.id.btnAddCar) Button btnAddCar;

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        ButterKnife.bind(this);

        recyclerView = (RecyclerView) findViewById(R.id.mycar_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        super.onStart();

        //MyRides
        try {
            String userId = FirebaseUser.getUid();
            String result = new GetTask(this).execute(Constants.BASE_URL + "car/?authorID=" + userId).get();
            Log.e(TAG, result);
            Type type = new TypeToken<List<Car>>() {}.getType();
            final List<Car> listCars = new Gson().fromJson(result, type);

            //Add to ListView
            adapter = new CarViewAdapter(listCars, new CarViewAdapter.OnEditClickListener() {
                @Override
                public void OnEditClick(Car item) {
                    Intent intent = new Intent(MyCarActivity.this, CarFormActivity.class);
                    intent.putExtra(CarFormActivity.EXTRA_CAR, item.getId());
                    startActivity(intent);
                }
            }, new CarViewAdapter.OnDeleteClickListener() {
                @Override
                public void OnDeleteClick(Car item) {
                    deleteCar(item);
                    listCars.remove(item);
                    adapter.notifyDataSetChanged();
                }
            });

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void deleteCar(Car car){
        String result = "";
        try {
            DeleteTask dt = new DeleteTask(getApplicationContext());
            Log.e(TAG, Constants.BASE_URL + "car/?_id=" + car.getId());
            result = dt.execute(Constants.BASE_URL + "car/?_id=" + car.getId()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void addCar(View view) {
        startActivity(new Intent(this, CarFormActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.mnu_mycar, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_add_car:
                Intent carFormInt = new Intent(this, CarFormActivity.class);
                startActivity(carFormInt);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
