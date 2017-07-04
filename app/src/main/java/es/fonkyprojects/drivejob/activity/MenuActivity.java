package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends Fragment{

    @BindView(R.id.btn_create) Button btnCreate;
    @BindView(R.id.btn_search) Button btnSearch;
    @BindView(R.id.btn_myrides) Button btnMyRides;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        return inflater.inflate(R.layout.activity_menu, container, false);
    }

    public void searchRideMenu(View view){
        startActivity(new Intent(getActivity(), SearchRideActivity.class));
    }

    public void myRidesMenu(View view){
        startActivity(new Intent(getActivity(), MyRidesActivity.class));
    }

    public void myCarMenu(View view){
        startActivity(new Intent(getActivity(), CarListActivity.class));
    }
}
