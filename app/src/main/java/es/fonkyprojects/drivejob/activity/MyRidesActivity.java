package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @BindView(R.id.myRidesCreated) TextView txtCreate;
    @BindView(R.id.myRidesRequest) TextView txtRequest;
    @BindView(R.id.myRidesJoin) TextView txtJoin;

    @BindView(R.id.myridescreated_list) RecyclerView recyclerViewCreated;
    public RecyclerView.Adapter adapterCreated;
    public RecyclerView.LayoutManager layoutManagerCreated;

    @BindView(R.id.myridesrequest_list) RecyclerView recyclerViewRequest;
    public RecyclerView.Adapter adapterRequest;
    public RecyclerView.LayoutManager layoutManagerRequest;

    @BindView(R.id.myridesjoin_list) RecyclerView recyclerViewJoin;
    public RecyclerView.Adapter adapterJoin;
    public RecyclerView.LayoutManager layoutManagerJoin;

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
            String result = new GetTask(getActivity()).execute(Constants.BASE_URL + "ride/?authorID=" + userId).get();
            Type type = new TypeToken<List<Ride>>(){}.getType();
            List<Ride> listRides = new Gson().fromJson(result, type);

            if(listRides.size()>0) {
                //Add to ListView
                adapterCreated = new RideViewAdapter(listRides, new RideViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Ride item) {
                        Intent intent = new Intent(getActivity(), RideDetailActivity.class);
                        intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, item.getID());
                        startActivity(intent);
                    }
                });
                layoutManagerCreated = new LinearLayoutManager(getActivity());
                recyclerViewCreated.setLayoutManager(layoutManagerCreated);
                recyclerViewCreated.setAdapter(adapterCreated);
            } else {
                txtCreate.setVisibility(View.GONE);
                recyclerViewCreated.setVisibility(View.GONE);
            }

            //RidesRequest
            result = new GetTask(getActivity()).execute(Constants.BASE_URL + "ride/?requestUser[]=" + userId).get();
            Type typeRequest = new TypeToken<List<Ride>>(){}.getType();
            List<Ride> listRidesRequest = new Gson().fromJson(result, typeRequest);
            if(listRidesRequest.size()>0) {
                //Add to ListView
                adapterRequest = new RideViewAdapter(listRidesRequest, new RideViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Ride item) {
                        Intent intent = new Intent(getActivity(), RideDetailActivity.class);
                        intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, item.getID());
                        startActivity(intent);
                    }
                });
                layoutManagerRequest = new LinearLayoutManager(getActivity());
                recyclerViewRequest.setLayoutManager(layoutManagerRequest);
                recyclerViewRequest.setAdapter(adapterRequest);
            } else {
                txtRequest.setVisibility(View.GONE);
                recyclerViewRequest.setVisibility(View.GONE);
            }


            result = new GetTask(getActivity()).execute(Constants.BASE_URL + "ride/?joinUser[]=" + userId).get();
            Type typeJoin = new TypeToken<List<Ride>>(){}.getType();
            List<Ride> listRidesJoin = new Gson().fromJson(result, typeJoin);
            if(listRidesJoin.size()>0) {
                //Add to ListView
                adapterJoin = new RideViewAdapter(listRidesJoin, new RideViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Ride item) {
                        Intent intent = new Intent(getActivity(), RideDetailActivity.class);
                        intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, item.getID());
                        startActivity(intent);
                    }
                });
                layoutManagerJoin = new LinearLayoutManager(getActivity());
                recyclerViewJoin.setLayoutManager(layoutManagerJoin);
                recyclerViewJoin.setAdapter(adapterJoin);
            } else {
                txtJoin.setVisibility(View.GONE);
                recyclerViewJoin.setVisibility(View.GONE);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Ride getRide(String rideId){
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
