package es.fonkyprojects.drivejob.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.fragment.OnFragmentInteractionListener;
import es.fonkyprojects.drivejob.model.Messaging;
import es.fonkyprojects.drivejob.notification.CreateNotification;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class MenuActivity extends Fragment implements View.OnClickListener{

    private static final String TAG = "MenuActivity";

    @BindView(R.id.btn_create) Button btnCreate;
    @BindView(R.id.btn_search) Button btnSearch;
    @BindView(R.id.btn_myrides) Button btnMyRides;
    @BindView(R.id.btn_car) Button btnCar;

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_menu, container, false);
        ButterKnife.bind(this, view);

        btnCreate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnMyRides.setOnClickListener(this);
        btnCar.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        String userId = FirebaseUser.getUid();
        String result = null;
        List<String> ids = new ArrayList<>();
        try {
            result = new GetTask(getActivity()).execute(Constants.BASE_URL +"message/?useridTo=" + userId).get();
            Type type = new TypeToken<List<Messaging>>(){}.getType();
            List<Messaging> messagingList = new Gson().fromJson(result, type);
            for(int i=0; i<messagingList.size(); i++){
                Messaging m = messagingList.get(i);
                CreateNotification cn = new CreateNotification();
                cn.select(FirebaseUser.getUid(), m.getUsernameFrom(), m.getKey(), m.getValue());
                ids.add(m.get_id());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void createMenu(){
        mListener.fragmentButton(0);
    }

    public void searchRideMenu(){
        mListener.fragmentButton(1);
    }

    public void myRidesMenu(){
        mListener.fragmentButton(4);
    }

    public void myCarMenu(){
        mListener.fragmentButton(2);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_create)
            createMenu();
        if(id == R.id.btn_search)
            searchRideMenu();
        if(id == R.id.btn_myrides)
            myRidesMenu();
        if(id == R.id.btn_car)
            myCarMenu();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }
}
