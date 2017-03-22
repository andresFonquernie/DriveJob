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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.model.UserRide;

import es.fonkyprojects.drivejob.restMethods.RideUser.RideUserDeleteTask;
import es.fonkyprojects.drivejob.restMethods.RideUser.RideUserGetTask;
import es.fonkyprojects.drivejob.restMethods.RideUser.RideUserPutTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RideDeleteTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RideGetTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePutTask;
import es.fonkyprojects.drivejob.restMethods.UserRide.UserRideGetTask;
import es.fonkyprojects.drivejob.restMethods.UserRide.UserRidePutTask;
import es.fonkyprojects.drivejob.restMethods.Users.UserGetTask;

import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.viewholder.UserViewAdapter;

public class RideDetailActivity extends AppCompatActivity{

    private static final String TAG = "RideDetailActivity";

    public static final String EXTRA_RIDE = "ride";
    public static final String EXTRA_RIDE_KEY = "ride_key";

    private String mRideKey;
    private String authorID;
    private Ride ride;
    private RideUser rideUser;

    public String[] ridersJoin;

    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;

    private List<User> listUsers = new ArrayList<>();

    private ImageView authorImage;
    private TextView authorView;
    private TextView timeGoingView;
    private TextView placeGoingView;
    private TextView timeReturnView;
    private TextView placeReturnView;
    private TextView daysView;
    private TextView priceView;
    private TextView avSeats;
    private Button joinButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        // Get post key from intent
        mRideKey = getIntent().getStringExtra(EXTRA_RIDE_KEY);
        if (mRideKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Views
        authorImage = (ImageView) findViewById(R.id.ride_author_photo);
        authorView = (TextView) findViewById(R.id.ride_author);
        timeGoingView = (TextView) findViewById(R.id.ride_timeGoing);
        placeGoingView = (TextView) findViewById(R.id.ride_placeGoing);
        timeReturnView = (TextView) findViewById(R.id.ride_timeReturn);
        daysView = (TextView) findViewById(R.id.ride_days);
        placeReturnView = (TextView) findViewById(R.id.ride_placeReturn);
        priceView = (TextView) findViewById(R.id.ride_prize);
        avSeats = (TextView) findViewById(R.id.ride_passengers);

        recyclerView = (RecyclerView) findViewById(R.id.user_list);

        joinButton = (Button) findViewById(R.id.btn_join);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRide(view);
            }
        });
        authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfile(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mRideKey != null) {
            //GET request
            String result = null;
            try {
                result = new RideGetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/ride/" + mRideKey).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if( result!=null && !result.equals("Error")){
                ride = new Gson().fromJson(result, Ride.class);
                timeGoingView.setText(getString(R.string.going) + ": " + ride.getTimeGoing());
                timeReturnView.setText(getString(R.string.returning) + ": " + ride.getTimeReturn());
                placeGoingView.setText(getString(R.string.from) + ": " + ride.getPlaceGoing());
                placeReturnView.setText(getString(R.string.to) + ": " + ride.getPlaceReturn());
                priceView.setText(getString(R.string.price) + ": " + String.valueOf(ride.getPrice()));
                avSeats.setText(getString(R.string.avseats) + ": " + String.valueOf(ride.getAvSeats()));
                authorID = ride.getAuthorID();
                authorView.setText(ride.getAuthor());

                String[] items = ride.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                String[] shortListDays = getResources().getStringArray(R.array.shortdaysofweek);
                String days = "";
                for (int i=0; i<items.length; i++){
                    if(items[i].equals("true")){
                        days = days + shortListDays[i] + ",";
                    }
                }
                daysView.setText(getString(R.string.days) + ": " + days.substring(0,days.length()-1));
            }

            if(FirebaseUser.getUid().equals(authorID)) {
                joinButton.setVisibility(View.INVISIBLE);
            }

            try {
                boolean joined = false;
                result = new RideUserGetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/rideuser/?rideId=" + mRideKey).get();
                Type type = new TypeToken<List<RideUser>>(){}.getType();
                List<RideUser> inpList = new Gson().fromJson(result, type);
                if(inpList.size()>0) {
                    rideUser = inpList.get(0);
                    ridersJoin = rideUser.getUserId().split(",");
                    if(!ridersJoin[0].equals("")) {
                        for (int i = 0; i < ridersJoin.length; i++) {
                            User u = getUser(ridersJoin[i]);
                            if (FirebaseUser.getUid().equals(u.getUserId()))
                                joined = true;
                            listUsers.add(u);
                        }
                    }

                    if (joined) {
                        joinButton.setVisibility(View.INVISIBLE);
                    }
                }


                adapter = new UserViewAdapter(listUsers, new UserViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(User item) {
                        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
                        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, item.getUserId());
                        startActivity(intent);
                        finish();
                    }
                });
                layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG + " IE", Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void joinRide(View view) {
        final String uid = FirebaseUser.getUid();
        String userJoin = rideUser.getUserId();
        if(userJoin == null || userJoin.length()==0){
            userJoin = uid;
        }
        else{
            userJoin = userJoin +"," + uid;
        }

        String result = "";
        (new SQLConnect()).updateAvSeats(ride.getAvSeats() - 1, mRideKey);
        try {
            String ru = new UserRideGetTask(this).execute("https://secret-meadow-74492.herokuapp.com/api/userride/?userId=" + uid).get();
            Type type = new TypeToken<List<UserRide>>(){}.getType();
            List<UserRide> inpList = new Gson().fromJson(ru, type);

            UserRide userRide = inpList.get(0);
            String rideJoin = userRide.getRideId();
            if(rideJoin == null || rideJoin.length()==0){
                rideJoin = mRideKey;
            }
            else{
                rideJoin = userJoin +"," + mRideKey;
            }
            userRide.setRideId(rideJoin);
            UserRidePutTask urpt = new UserRidePutTask(this);
            urpt.setUserRidePut(userRide);
            result = urpt.execute("https://secret-meadow-74492.herokuapp.com/api/userride/" + userRide.get_id()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if(result.equals("Update")) {
            try {

                rideUser.setUserId(userJoin);
                RideUserPutTask rupt = new RideUserPutTask(this);
                rupt.setRideUserPost(rideUser);
                result = rupt.execute("https://secret-meadow-74492.herokuapp.com/api/rideuser/" + rideUser.get_id()).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if(result.equals("Update")){
            try {
                ride.setAvSeats(ride.getAvSeats() - 1);
                RidePutTask rpt = new RidePutTask(this);
                rpt.setRidePost(ride);
                result = rpt.execute("https://secret-meadow-74492.herokuapp.com/api/ride/" + mRideKey).get();
                if(result.equals("Update"))
                    joinButton.setVisibility(View.INVISIBLE);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUser(String userId){
        User user = new User();
        try {
            UserGetTask ugt = new UserGetTask(this);
            String result = ugt.execute(Constants.BASE_URL + "user/?userId=" + userId).get();
            Type type = new TypeToken<List<User>>(){}.getType();
            List<User> inpList = new Gson().fromJson(result, type);
            user = inpList.get(0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void goProfile(View view){
        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, authorID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(FirebaseUser.getUid().equals(authorID)) {
            getMenuInflater().inflate(R.menu.mnu_myride, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If changes OK, return new value in the result Intent back to the calling activity
        Intent intent;
        switch (item.getItemId()) {
            case R.id.mnu_edit:
                intent = new Intent(RideDetailActivity.this, RideEditActivity.class);
                intent.putExtra(RideDetailActivity.EXTRA_RIDE, ride);
                startActivity(intent);
                break;
            case R.id.mnu_delete:
                RideDeleteTask rdt = new RideDeleteTask(this);
                try {
                    String result =  rdt.execute(Constants.BASE_URL + "ride/" + mRideKey).get();
                    (new SQLConnect()).deleteRide(mRideKey);
                    (new RideUserDeleteTask(this)).execute(Constants.BASE_URL + "rideuser/?rideId=" + mRideKey).get();
                    for(int i=0; i<listUsers.size(); i++){
                        String s = (new UserRideGetTask(this)).execute(Constants.BASE_URL + "userride/?userId=" + listUsers.get(i).getUserId()).get();
                        Log.e(TAG, "STRING: " + s);
                        Type type = new TypeToken<List<UserRide>>(){}.getType();
                        List<UserRide> l = new Gson().fromJson(s, type);
                        UserRide ur = l.get(0);
                        String[] rides = ur.getRideId().split(",");
                        Log.e(TAG + "rides", rides[0]);
                        String newRides = "";
                        for(int j=0; j<rides.length; j++){
                            if(!rides[j].equals(mRideKey)){
                                if(newRides == null || newRides.length()==0){
                                    newRides = rides[j];
                                }
                                else{
                                    newRides = newRides +"," + rides[j];
                                }
                            }
                        }
                        ur.setRideId(newRides);
                        UserRidePutTask urpt = new UserRidePutTask(this);
                        urpt.setUserRidePut(ur);
                        urpt.execute("https://secret-meadow-74492.herokuapp.com/api/userride/" + ur.get_id()).get();
                        Log.e(TAG + " Id", ur.getRideId());
                    }
                    if(result.equals("Ok")){
                        intent = new Intent(RideDetailActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(this, "Error deleting", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }

}
