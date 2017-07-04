package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.restMethods.DeleteTask;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.viewholder.CarViewAdapter;

public class CarListActivity extends Fragment {

    private static final String TAG = "CarListActivity";

    @BindView(R.id.btnAddCar) Button btnAddCar;

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_my_car, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.mycar_list);
        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //MyRides
        try {
            String userId = FirebaseUser.getUid();
            String result = new GetTask(getActivity()).execute(Constants.BASE_URL + "car/?authorID=" + userId).get();
            Type type = new TypeToken<List<Car>>() {}.getType();
            final List<Car> listCars = new Gson().fromJson(result, type);

            //Add to ListView
            adapter = new CarViewAdapter(listCars, new CarViewAdapter.OnDeleteClickListener() {
                @Override
                public void OnDeleteClick(Car item) {
                    deleteCar(item);
                    listCars.remove(item);
                    adapter.notifyDataSetChanged();
                }
            });

            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void deleteCar(Car car){
        try {
            String result = new GetTask(getActivity()).execute(Constants.BASE_URL + "ride/?carID=" + car.getId()).get();
            Type type = new TypeToken<List<Ride>>() {}.getType();
            List<Ride> listCars = new Gson().fromJson(result, type);
            if(listCars.size()>0){
                Toast.makeText(getActivity(), "Can't delete. There are rides using it", Toast.LENGTH_LONG).show();
            } else {
                DeleteTask dt = new DeleteTask(getActivity());
                result = dt.execute(Constants.BASE_URL + "car/" + car.getId()).get();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void addCar() {
        startActivity(new Intent(getActivity(), CarFormActivity.class));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mnu_mycar, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_add_car:
                Intent carFormInt = new Intent(getActivity(), CarFormActivity.class);
                startActivity(carFormInt);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
