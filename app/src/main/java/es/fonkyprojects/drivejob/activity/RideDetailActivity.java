package es.fonkyprojects.drivejob.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.SQLQuery.SQLConnect;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.model.Messaging;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.model.RideUser;
import es.fonkyprojects.drivejob.model.RideUserRequest;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.model.UserDays;
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

    private List<UserDays> listUsersRequest = new ArrayList<>();
    private List<UserDays> listUsersJoin = new ArrayList<>();
    private String[] avSeatsDay;

    public RecyclerView requestRecyclerView;
    public RecyclerView.Adapter requestAdapter;
    public RecyclerView.LayoutManager requestLayoutManager;

    public RecyclerView joinRecyclerView;
    public RecyclerView.Adapter joinAdapter;
    public RecyclerView.LayoutManager joinLayoutManager;

    //private ImageView authorImage;
    private TextView authorView;
    private TextView timeGoingView;
    private TextView placeGoingView;
    private TextView timeReturnView;
    private TextView placeReturnView;
    private TextView daysView;
    private TextView priceView;
    private TextView avSeatsDayView;
    private TextView carView;
    private Button btnJoin;

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
        //authorImage = (ImageView)
        findViewById(R.id.ride_author_photo);
        authorView = (TextView) findViewById(R.id.ride_author);
        timeGoingView = (TextView) findViewById(R.id.ride_timeGoing);
        placeGoingView = (TextView) findViewById(R.id.ride_placeGoing);
        timeReturnView = (TextView) findViewById(R.id.ride_timeReturn);
        daysView = (TextView) findViewById(R.id.ride_days);
        placeReturnView = (TextView) findViewById(R.id.ride_placeReturn);
        priceView = (TextView) findViewById(R.id.ride_price);
        avSeatsDayView = (TextView) findViewById(R.id.ride_avSeatsDay);
        carView = (TextView) findViewById(R.id.ride_car);
        btnJoin = (Button) findViewById(R.id.btn_join);

        requestRecyclerView = (RecyclerView) findViewById(R.id.userrequest_list);
        joinRecyclerView = (RecyclerView) findViewById(R.id.userjoin_list);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mRideKey != null) {
            //GET Ride from key
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
                timeReturnView.setText(getString(R.string.returningDetail, ride.getTimeReturn()));
                placeGoingView.setText(getString(R.string.fromDetail, ride.getPlaceGoing()));
                placeReturnView.setText(getString(R.string.toDetail, ride.getPlaceReturn()));
                priceView.setText(getString(R.string.priceDetail, ride.getPrice()));
                String sAvSeatsDay = ride.getAvSeatsDay().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "");
                avSeatsDay = sAvSeatsDay.split(",");
                avSeatsDayView.setText(getString(R.string.avSeatsDayDetail, sAvSeatsDay));

                //Get car
                Car c = getCar(ride.getCarID());
                carView.setText(getString(R.string.carDetail, c.toString()));

                //Get days
                String[] items = ride.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                String[] shortListDays = getResources().getStringArray(R.array.shortdaysofweek);
                String days = "";
                for (int i = 0; i < items.length; i++) {
                    if (items[i].equals("true")) {
                        days = days + shortListDays[i] + ",";
                    }
                }
                days = days.substring(0, days.length()-1);
                daysView.setText(getString(R.string.daysDetail, days));
            }

            boolean request = false;
            boolean join = false;

            //Get UserRequest
            try {
                result = new GetTask(this).execute(Constants.BASE_URL + "rideuserrequest/?rideId=" + mRideKey).get();
                Type type = new TypeToken<List<RideUserRequest>>() {
                }.getType();
                List<RideUserRequest> listRideUserRequests = new Gson().fromJson(result, type);
                for (int i = 0; i < listRideUserRequests.size(); i++) { //Get Users
                    User u = getUser(listRideUserRequests.get(i).getUserId());
                    UserDays ud = new UserDays(u.getUserId(), u.getUsername() + " " + u.getSurname(), listRideUserRequests.get(i).getDays());
                    if (listRideUserRequests.get(i).getUserId().equals(FirebaseUser.getUid())) {
                        request = true;
                    }
                    listUsersRequest.add(ud);
                }

                requestAdapter = new UserRequestViewAdapter(listUsersRequest, new UserRequestViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(UserDays item) {
                        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
                        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, item.getUserId());
                        startActivity(intent);
                        finish();
                    }
                }, new UserRequestViewAdapter.OnAcceptClickListener() {
                    @Override
                    public void onAcceptClick(UserDays item) {
                        acceptJoin(item);
                        listUsersRequest.remove(item);
                        listUsersJoin.add(item);
                        requestAdapter.notifyDataSetChanged();
                        joinAdapter.notifyDataSetChanged();
                    }
                }, new UserRequestViewAdapter.OnRefuseClickListener() {
                    @Override
                    public void onRefuseClick(UserDays item) {
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
                    UserDays ud = new UserDays(u.getUserId(), u.getUsername() + " " + u.getSurname(), listRideUser.get(i).getDays());
                    if (listRideUser.get(i).getUserId().equals(FirebaseUser.getUid())) {
                        join = true;
                    }
                    listUsersJoin.add(ud);
                }


                joinAdapter = new UserJoinViewAdapter(listUsersJoin, new UserJoinViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(UserDays item) {
                        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
                        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, item.getUserId());
                        startActivity(intent);
                        finish();
                    }
                }, new UserJoinViewAdapter.OnRefuseClickListener() {
                    @Override
                    public void onRefuseClick(UserDays item) {
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

            //Chek if author/reques/join
            boolean avSeatsFree = false;
            for(int i=0; i<avSeatsDay.length; i++){
                int space = Integer.parseInt(avSeatsDay[i]);
                if(i>0)
                    avSeatsFree = true;
            }
            if (FirebaseUser.getUid().equals(authorID) || !avSeatsFree || request || join)
                btnJoin.setVisibility(View.GONE);
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

    public void goProfile(View view) {
        Intent intent = new Intent(RideDetailActivity.this, MyProfileActivity.class);
        intent.putExtra(MyProfileActivity.EXTRA_USER_ID, authorID);
        startActivity(intent);
    }


    public void selectDaysJoin(View view){
        ArrayList<String> temp = new ArrayList<>();
        String[] listDays = getResources().getStringArray(R.array.daysofweek); //días de la semana completo
        String[] myListDays; //listado con los dias de la Ride
        final boolean[] myCheckedDays; //días seleccionados
        final ArrayList<Integer> myMUserDays = new ArrayList<>(); //Selected days
        final ArrayList<Integer> mUserDays = new ArrayList<>(); //List of general position

        //Add day of week to temp/mListDays
        //Add pos to mUserDays
        String[] items = ride.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        for (int i=0; i<items.length; i++){
            if(items[i].equals("true") && Integer.parseInt(avSeatsDay[i])>0){
                temp.add(listDays[i]);
                mUserDays.add(i);
            }
        }
        myListDays = new String[temp.size()];
        myListDays = temp.toArray(myListDays);
        myCheckedDays = new boolean[myListDays.length];

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RideDetailActivity.this);
        mBuilder.setTitle(R.string.days);
        mBuilder.setMultiChoiceItems(myListDays, myCheckedDays, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    myMUserDays.add(mUserDays.get(position));
                } else {
                    myMUserDays.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                Collections.sort(myMUserDays);
                for (int i = 0; i < myMUserDays.size(); i++) {
                    item = item + myMUserDays.get(i);
                    if (i!= myMUserDays.size()-1) {
                        item = item + ",";
                    }
                }
                if(item.isEmpty())
                    dialogInterface.dismiss();
                else
                    requestJoin(item);
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void requestJoin(String selectedDays) {
        btnJoin.setVisibility(View.GONE);
        //Create user request
        String uid = FirebaseUser.getUid();
        RideUserRequest ruq = new RideUserRequest(mRideKey, uid, selectedDays);
        try {
            RideUserRequestPostTask rupt = new RideUserRequestPostTask(getApplicationContext());
            rupt.setRideUserRequestPost(ruq);
            String result = rupt.execute(Constants.BASE_URL + "rideuserrequest").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //Update seats -1
        updateSeats(selectedDays, -1);

        //Send message
        User u = getUser(uid);
        sendMessage(u.getUsername(), ride.getAuthorID(), mRideKey, 0);

        //Add to list
        UserDays ud = new UserDays(u.getUserId(), u.getUsername() + " " + u.getSurname(), selectedDays);
        listUsersRequest.add(ud);
        requestAdapter.notifyDataSetChanged();
    }

    private void acceptJoin(UserDays userdays) {
        String uid = userdays.getUserId();
        RideUser ru = new RideUser(mRideKey, uid, userdays.getDays());
        try {
            RideUserPostTask rupt = new RideUserPostTask(getApplicationContext());
            rupt.setRideUserPost(ru);
            String result = rupt.execute(Constants.BASE_URL + "rideuser").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        deleteRequest(uid);
    }

    private void refuseJoin(UserDays userdays) {
        deleteRequest(userdays.getUserId());
        updateSeats(userdays.getDays(), 1);
    }

    private void kickJoin(UserDays userdays) {
        String uid = userdays.getUserId();
        try {
            DeleteTask dt = new DeleteTask(getApplicationContext());
            String result = dt.execute(Constants.BASE_URL + "rideuser/?rideId=" + mRideKey + "&userId=" + uid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //Update seats +1
        updateSeats(userdays.getDays(), 1);
    }

    private String updateSeats(String userdays, int value) {
        //GET int days from String
        String[] sDays = userdays.split(",");
        for(int i = 0; i<sDays.length; i++ ){
            int day = Integer.parseInt(sDays[i]);
            avSeatsDay[day] = String.valueOf(Integer.parseInt(avSeatsDay[day]) + value);
        }
        ride.setAvSeatsDay(Arrays.toString(avSeatsDay));

        //REST Update avSeatsDay
        String result = "";
        try {
            RidePutTask rpt = new RidePutTask(getApplicationContext());
            rpt.setRidePut(ride);
            result = rpt.execute(Constants.BASE_URL + "ride/" + ride.getID()).get();
            if (result.equals("Update"))
                (new SQLConnect()).updateAvSeatsDay(ride.getAvSeatsDay(), mRideKey);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //SQL Update avSeatsDay
        (new SQLConnect()).updateAvSeatsDay(ride.getAvSeatsDay(), mRideKey);

        String sAvSeatsDay = ride.getAvSeatsDay().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "");
        avSeatsDayView.setText(getString(R.string.avSeatsDayDetail, sAvSeatsDay));
        return result;
    }

    private void deleteRequest(String uid) {
        try {
            DeleteTask dt = new DeleteTask(getApplicationContext());
            String result = dt.execute(Constants.BASE_URL + "rideuserrequest/?rideId=" + mRideKey + "&userId=" + uid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String username, String authorID, String rideKey, int code) {
        MessagingPostTask mpt = new MessagingPostTask(this);
        Messaging m = new Messaging(username, authorID, rideKey, code);
        mpt.setMessaging(m);

        try {
            String result = mpt.execute("https://fcm.googleapis.com/fcm/send").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
}
