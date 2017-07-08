package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.viewholder.RideViewAdapter;

public class MyRidesActivity extends Fragment {

    private static final String TAG = "MyRidestActivity";

    @BindView(R.id.myrides_list) RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_my_rides, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            //MyRides
            String userId = FirebaseUser.getUid();
            //String result = new GetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/ride/?authorID=" + userId).get();
            String result = new GetTask(getActivity()).execute("https://secret-meadow-74492.herokuapp.com/api/ride").get();
            Type type = new TypeToken<List<Ride>>(){}.getType();
            List<Ride> listRides = new Gson().fromJson(result, type);

            //RidesJoin
            //TODO
            /*result = new GetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/rideuser/?userId=" + userId).get();
            type = new TypeToken<List<RideUser>>(){}.getType();
            List<RideUser> inpList = new Gson().fromJson(result, type);
            if(inpList.size()>0) {
                RideUser rideUser = inpList.get(0);
                String[] ridesJoin = rideUser.getRideId().split(",");
                for (String aRidesJoin : ridesJoin) {
                    if (!aRidesJoin.equals("")) {
                        Ride r = getRide(aRidesJoin);
                        listRides.add(r);
                    }
                }
            }*/

            //Add to ListView
            adapter = new RideViewAdapter(listRides, new RideViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Ride item) {
                    Intent intent = new Intent(getActivity(), RideDetailActivity.class);
                    intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, item.getID());
                    startActivity(intent);
                }
            });
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Ride getRide(String rideId){
        Ride ride = new Ride();
        try {
            GetTask rgt = new GetTask(getActivity());
            String result = rgt.execute(Constants.BASE_URL + "ride/" + rideId).get();
            Type type = new TypeToken<Ride>(){}.getType();
            ride = new Gson().fromJson(result, type);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ride;
    }
}
