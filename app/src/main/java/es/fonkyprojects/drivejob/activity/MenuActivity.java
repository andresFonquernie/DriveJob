package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Car;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class MenuActivity extends AppCompatActivity{

    @Bind(R.id.btn_create) Button btnCreate;
    @Bind(R.id.btn_search) Button btnSearch;
    @Bind(R.id.btn_myrides) Button btnMyRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        suscribeTopic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_profile:
                Intent profileInt = new Intent(this, MyProfileActivity.class);
                startActivity(profileInt);
                break;
            case R.id.mnu_settings:
                Intent itemSettings = new Intent(this, SettingsActivity.class);
                startActivity(itemSettings);
                break;
            case R.id.mnu_logout:
                unsuscribeTopic();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void createRideMenu(View view){
        try {
            String userId = FirebaseUser.getUid();
            String result = new GetTask(this).execute(Constants.BASE_URL + "car/?authorID=" + userId).get();Type type = new TypeToken<List<Car>>() {}.getType();
            List<Car> listCars = new Gson().fromJson(result, type);
            if(listCars.size()>0){
                startActivity(new Intent(MenuActivity.this, RideCreateActivity.class));
            } else {
                Toast.makeText(this, R.string.noCarsWarning , Toast.LENGTH_LONG).show();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void searchRideMenu(View view){
        startActivity(new Intent(MenuActivity.this, SearchRideActivity.class));
    }

    public void myRidesMenu(View view){
        startActivity(new Intent(MenuActivity.this, MyRidesActivity.class));
    }

    public void myCarMenu(View view){
        startActivity(new Intent(MenuActivity.this, MyCarActivity.class));
    }

    //subscribe topics
    public void suscribeTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    //unsubscribe topics
    public void unsuscribeTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
    }

}
