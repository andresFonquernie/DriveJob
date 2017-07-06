package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.viewholder.RideViewAdapter;

public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultActivity";

    @BindView(R.id.ride_list) RecyclerView recyclerView;
    @BindView(R.id.detailToolbar) Toolbar toolbar;

    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;

    public static final String EXTRA_RIDE_SEARCH = "ride_search";

    private String authorId;
    private String timeGoing;
    private String timeReturn;
    private double latGoing;
    private double latReturning;
    private double lngGoign;
    private double lngReturning;
    private List<Boolean> days;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        getValues();
    }

    @Override
    protected void onStart() {
        super.onStart();

        int maxDistance = sharedPref.getInt("DISTANCE",0);
        int maxTime = sharedPref.getInt("TIME",0);

        List<Ride> listRides = (new SQLConnect()).searchRide(authorId, latGoing, latReturning, lngGoign, lngReturning,
                timeGoing, timeReturn, days, maxDistance, maxTime);
        adapter = new RideViewAdapter(listRides, new RideViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ride item) {
                Intent intent = new Intent(SearchResultActivity.this, RideDetailActivity.class);
                intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, item.getID());
                startActivity(intent);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void getValues(){
        Ride rs = (Ride) getIntent().getSerializableExtra(EXTRA_RIDE_SEARCH);
        authorId = rs.getAuthorID();
        timeGoing = rs.getTimeGoing();
        timeReturn = rs.getTimeReturn();
        latGoing = rs.getLatGoing();
        latReturning = rs.getLatReturn();
        lngGoign = rs.getLngGoing();
        lngReturning = rs.getLngReturn();
        days = rs.getDays();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_blank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
