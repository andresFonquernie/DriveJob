package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.model.Messaging;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.model.RideUserRequest;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.DeleteTask;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.restMethods.Messaging.MessagingPostTask;
import es.fonkyprojects.drivejob.restMethods.RideUser.RideUserPostTask;
import es.fonkyprojects.drivejob.restMethods.RideUserRequest.RideUserRequestPostTask;
import es.fonkyprojects.drivejob.restMethods.Rides.RidePutTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;
import es.fonkyprojects.drivejob.viewholder.UserJoinViewAdapter;
import es.fonkyprojects.drivejob.viewholder.UserRequestViewAdapter;

public class RideDetailActivity extends AppCompatActivity {

    private static final String TAG = "RideDetailActivity";

    public static final String EXTRA_RIDE_KEY = "ride_key";

    private String mRideKey;
    private String authorID;
    private Ride ride;

    private List<User> listUsersRequest = new ArrayList<>();
    private List<User> listUsersJoin = new ArrayList<>();

    public RecyclerView requestRecyclerView;
    public RecyclerView.Adapter requestAdapter;
    public RecyclerView.LayoutManager requestLayoutManager;

    public RecyclerView joinRecyclerView;
    public RecyclerView.Adapter joinAdapter;
    public RecyclerView.LayoutManager joinLayoutManager;

    private ImageView authorImage;
    private TextView authorView;
    private TextView timeGoingView;
    private TextView placeGoingView;
    private TextView timeReturnView;
    private TextView placeReturnView;
    private TextView daysView;
    private TextView priceView;
    private TextView avSeats;
    private TextView tCar;
    private Button joinButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

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
        tCar = (TextView) findViewById(R.id.ride_car);

        requestRecyclerView = (RecyclerView) findViewById(R.id.userrequest_list);
        joinRecyclerView = (RecyclerView) findViewById(R.id.userjoin_list);

        joinButton = (Button) findViewById(R.id.btn_join);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestJoin();
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
                result = new GetTask(this).execute(Constants.BASE_URL + "ride/" + mRideKey).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (result != null && !result.equals("Error")) {
                ride = new Gson().fromJson(result, Ride.class);
                authorID = ride.getAuthorID();
                authorView.setText(ride.getAuthor());
                timeGoingView.setText(getString(R.string.goingDetail, ride.getTimeGoing()));
                timeReturnView.setText(getString(R.string.returningDetail,  ride.getTimeReturn()));
                placeGoingView.setText(getString(R.string.fromDetail, ride.getPlaceGoing()));
                placeReturnView.setText(getString(R.string.toDetail, ride.getPlaceReturn()));
                priceView.setText(getString(R.string.priceDetail, ride.getPrice()));
                avSeats.setText(getString(R.string.avseatsDetail, ride.getAvSeats()));

                Car c = getCar(ride.getCarID());
                tCar.setText(getString(R.string.carDetail, c.toString()));

                String[] items = ride.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                String[] shortListDays = getResources().getStringArray(R.array.shortdaysofweek);
                String days = "";
                for (int i = 0; i < items.length; i++) {
                    if (items[i].equals("true")) {
                        days = days + shortListDays[i] + ",";
                    }
                }
                daysView.setText(getString(R.string.days) + ": " + days.substring(0, days.length() - 1));
            }

            boolean request = false;
            boolean join = false;

            //Get UserRequest
            try {
                result = new GetTask(this).execute(Constants.BASE_URL + "rideuserrequest/?rideId=" + mRideKey).get();
                Type type = new TypeToken<List<RideUserRequest>>() {}.getType();
                List<RideUserRequest> listRideUserRequests = new Gson().fromJson(result, type);
                for (int i = 0; i < listRideUserRequests.size(); i++) { //Get Users
                    User u = getUser(listRideUserRequests.get(i).getUserId());
                    if (listRideUserRequests.get(i).getUserId().equals(FirebaseUser.getUid())) {
                        request = true;
                    }
                    listUsersRequest.add(u);
                }

                requestAdapter = new UserRequestViewAdapter(listUsersRequest, new UserRequestViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(User item) {
                        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
                        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, item.getUserId());
                        startActivity(intent);
                        finish();
                    }
                }, new UserRequestViewAdapter.OnAcceptClickListener() {
                    @Override
                    public void onAcceptClick(User item) {
                        acceptJoin(item);
                        listUsersRequest.remove(item);
                        listUsersJoin.add(item);
                        requestAdapter.notifyDataSetChanged();
                        joinAdapter.notifyDataSetChanged();
                    }
                }, new UserRequestViewAdapter.OnRefuseClickListener() {
                    @Override
                    public void onRefuseClick(User item) {
                        refuseJoin(item);
                        listUsersRequest.remove(item);
                        requestAdapter.notifyDataSetChanged();
                    }
                });


                requestLayoutManager = new LinearLayoutManager(this);
                requestRecyclerView.setLayoutManager(requestLayoutManager);
                requestRecyclerView.setAdapter(requestAdapter);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            //Get Users join
            try {
                result = new GetTask(this).execute(Constants.BASE_URL + "rideuser/?rideId=" + mRideKey).get();
                Type type = new TypeToken<List<RideUser>>() {
                }.getType();
                List<RideUser> listRideUser = new Gson().fromJson(result, type);
                for (int i = 0; i < listRideUser.size(); i++) { //Get Users
                    User u = getUser(listRideUser.get(i).getUserId());
                    if (listRideUser.get(i).getUserId().equals(FirebaseUser.getUid())) {
                        join = true;
                    }
                    listUsersJoin.add(u);
                }


                joinAdapter = new UserJoinViewAdapter(listUsersJoin, new UserJoinViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(User item) {
                        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
                        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, item.getUserId());
                        startActivity(intent);
                        finish();
                    }
                }, new UserJoinViewAdapter.OnRefuseClickListener() {
                    @Override
                    public void onRefuseClick(User item) {
                        kickJoin(item);
                        listUsersJoin.remove(item);
                        joinAdapter.notifyDataSetChanged();
                    }
                });
                joinLayoutManager = new LinearLayoutManager(this);
                joinRecyclerView.setLayoutManager(joinLayoutManager);
                joinRecyclerView.setAdapter(joinAdapter);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (request || join || ride.getAvSeats() == 0 || FirebaseUser.getUid().equals(authorID))
                joinButton.setVisibility(View.GONE);
        }
    }

    private User getUser(String userId) {
        User user = new User();
        try {
            GetTask gt = new GetTask(this);
            String result = gt.execute(Constants.BASE_URL + "user/?userId=" + userId).get();
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> inpList = new Gson().fromJson(result, type);
            user = inpList.get(0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return user;
    }

    private Car getCar(String carId) {
        Car car = new Car();
        try {
            GetTask gt = new GetTask(this);
            String result = gt.execute(Constants.BASE_URL + "car/" + carId).get();
            car = new Gson().fromJson(result, Car.class);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return car;
    }


    private void requestJoin() {
        String uid = FirebaseUser.getUid();
        //Create user request
        RideUserRequest ruq = new RideUserRequest(mRideKey, uid);
        try {
            RideUserRequestPostTask rupt = new RideUserRequestPostTask(getApplicationContext());
            rupt.setRideUserRequestPost(ruq);
            String result = rupt.execute(Constants.BASE_URL + "rideuserrequest").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //Update seats -1
        ride.setAvSeats(ride.getAvSeats() - 1);
        updateSeats();

        User u = getUser(uid);

        MessagingPostTask mpt = new MessagingPostTask(this);
        Messaging m = new Messaging(u.getUsername(), ride.getAuthorID(), mRideKey, 0);
        mpt.setMessaging(m);

        try {
            String result = mpt.execute("https://fcm.googleapis.com/fcm/send").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        listUsersRequest.add(u);
        requestAdapter.notifyDataSetChanged();
    }

    private void acceptJoin(User u) {
        String uid = u.getUserId();
        RideUser ru = new RideUser(mRideKey, uid);
        try {
            RideUserPostTask rupt = new RideUserPostTask(getApplicationContext());
            rupt.setRideUserPost(ru);
            String result = rupt.execute(Constants.BASE_URL + "rideuser").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        deleteRequest(uid);
    }

    private void refuseJoin(User u) {
        deleteRequest(u.getUserId());
        ride.setAvSeats(ride.getAvSeats()+1);
        updateSeats();
        (new SQLConnect()).updateAvSeats(ride.getAvSeats(), mRideKey);
    }

    private void deleteRequest(String uid) {
        try {
            DeleteTask dt = new DeleteTask(getApplicationContext());
            String result = dt.execute(Constants.BASE_URL + "rideuserrequest/?rideId=" + mRideKey + "&userId=" + uid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void kickJoin(User user) {
        String uid = user.getUserId();
        try {
            DeleteTask dt = new DeleteTask(getApplicationContext());
            String result = dt.execute(Constants.BASE_URL + "rideuser/?rideId=" + mRideKey + "&userId=" + uid).get();

            RidePutTask rpt = new RidePutTask(getApplicationContext());
            ride.setAvSeats(ride.getAvSeats() + 1);
            rpt.setRidePut(ride);
            result = rpt.execute(Constants.BASE_URL + "ride/" + ride.getID()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        (new SQLConnect()).updateAvSeats(ride.getAvSeats(), mRideKey);
    }

    private String deleteRide() {
        String result = "";

        DeleteTask dt = new DeleteTask(this);
        try {
            result = dt.execute(Constants.BASE_URL + "ride/" + mRideKey).get();
            if (result.equals("Ok")) {
                (new SQLConnect()).deleteRide(mRideKey);
                result = (new DeleteTask(this)).execute(Constants.BASE_URL + "rideuser/?rideId=" + mRideKey).get();
                result = (new DeleteTask(this)).execute(Constants.BASE_URL + "rideuserrequest/?rideId=" + mRideKey).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String updateSeats() {
        String result = "";
        try {
            RidePutTask rpt = new RidePutTask(getApplicationContext());
            rpt.setRidePut(ride);
            result = rpt.execute(Constants.BASE_URL + "ride/" + ride.getID()).get();
            if (result.equals("Update"))
                (new SQLConnect()).updateAvSeats(ride.getAvSeats(), mRideKey);
            avSeats.setText(getString(R.string.avseatsDetail, ride.getAvSeats()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void goProfile(View view) {
        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, authorID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FirebaseUser.getUid().equals(authorID)) {
            getMenuInflater().inflate(R.menu.mnu_ride_detail, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.mnu_edit:
                intent = new Intent(RideDetailActivity.this, RideEditActivity.class);
                intent.putExtra(RideEditActivity.EXTRA_RIDE, ride);
                startActivity(intent);
                break;
            case R.id.mnu_delete:
                String result = deleteRide();
                if (result.equals("Ok")) {
                    intent = new Intent(RideDetailActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error deleting", Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

}
