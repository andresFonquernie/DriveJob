package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.UserRide;
import es.fonkyprojects.drivejob.restMethods.Rides.RideGetTask;
import es.fonkyprojects.drivejob.restMethods.UserRide.UserRideGetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.viewholder.RideViewAdapter;

public class MyRidesActivity extends AppCompatActivity {

    private static final String TAG = "MyRidestActivity";

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;

    private List<Ride> listRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        ButterKnife.bind(this);
        recyclerView = (RecyclerView) findViewById(R.id.myrides_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {

            //RidesCreate
            String userId = FirebaseUser.getUid();
            String result = new RideGetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/ride/?authorID=" + userId).get();
            Log.e(TAG, result);
            Type type = new TypeToken<List<Ride>>(){}.getType();
            listRides = new Gson().fromJson(result, type);

            //RidesJoin
            result = new UserRideGetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/userride/?userId=" + userId).get();
            type = new TypeToken<List<UserRide>>(){}.getType();
            List<UserRide> inpList = new Gson().fromJson(result, type);
            if(inpList.size()>0) {
                UserRide userRide = inpList.get(0);
                String[] ridesJoin = userRide.getRideId().split(",");
                for (int i = 0; i < ridesJoin.length; i++) {
                    if(!ridesJoin[i].equals("")) {
                        Ride r = getRide(ridesJoin[i]);
                        listRides.add(r);
                    }
                }
            }

            adapter = new RideViewAdapter(listRides, new RideViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Ride item) {
                    Intent intent = new Intent(MyRidesActivity.this, RideDetailActivity.class);
                    intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, item.getID());
                    startActivity(intent);
                    finish();
                }
            });
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Ride getRide(String rideId){
        Ride ride = new Ride();
        try {
            RideGetTask rgt = new RideGetTask(this);
            String result = rgt.execute(Constants.BASE_URL + "ride/" + rideId).get();
            Type type = new TypeToken<Ride>(){}.getType();
            ride = new Gson().fromJson(result, type);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ride;
    }
}
