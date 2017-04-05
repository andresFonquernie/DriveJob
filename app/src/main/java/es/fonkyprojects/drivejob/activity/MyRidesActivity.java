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
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.viewholder.RideViewAdapter;

public class MyRidesActivity extends AppCompatActivity {

    private static final String TAG = "MyRidestActivity";

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;

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

            //MyRides
            String userId = FirebaseUser.getUid();
            String result = new GetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/ride/?authorID=" + userId).get();
            Log.e(TAG, result);
            Type type = new TypeToken<List<Ride>>(){}.getType();
            List<Ride> listRides = new Gson().fromJson(result, type);

            //RidesJoin
            result = new GetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/userride/?userId=" + userId).get();
            type = new TypeToken<List<RideUser>>(){}.getType();
            List<RideUser> inpList = new Gson().fromJson(result, type);
            if(inpList.size()>0) {
                RideUser rideUser = inpList.get(0);
                String[] ridesJoin = rideUser.getRideId().split(",");
                for (int i = 0; i < ridesJoin.length; i++) {
                    if(!ridesJoin[i].equals("")) {
                        Ride r = getRide(ridesJoin[i]);
                        listRides.add(r);
                    }
                }
            }

            //Add to ListView
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
            GetTask rgt = new GetTask(this);
            String result = rgt.execute(Constants.BASE_URL + "ride/" + rideId).get();
            Type type = new TypeToken<Ride>(){}.getType();
            ride = new Gson().fromJson(result, type);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ride;
    }
}
