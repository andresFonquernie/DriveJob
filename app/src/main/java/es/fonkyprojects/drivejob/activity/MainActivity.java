package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import es.fonkyprojects.drivejob.fragment.OnFragmentInteractionListener;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        suscribeTopic();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new MenuActivity());
        tx.commit();
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            boolean found = false;
            int i = 0;
            while(i<navigationView.getMenu().size() && !found){
                if(navigationView.getMenu().getItem(i).isChecked())
                    found = true;
                i++;
                }
            if(found)
                goHome(this.findViewById(android.R.id.content));
            else
                super.onBackPressed();

        }
    }

    public void goHome(View view){
        Fragment fragment = new MenuActivity();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().getItem(0).setChecked(false);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        drawer.closeDrawers();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        changeFragment(-1, item);
        return true;
    }

    @Override
    public void fragmentButton(int id) {
        changeFragment(id, null);
    }

    private void changeFragment(int id, MenuItem item){
        boolean fragmentTransaction = false;
        Fragment fragment = null;
        if(id >= 0){
            item = navigationView.getMenu().getItem(id);
            id = item.getItemId();
        } else if(item != null) {
            id = item.getItemId();
        }

        if (id == R.id.create) {
            fragment = new RideCreateActivity();
            fragmentTransaction = true;
        } else if (id == R.id.search) {
            fragment = new SearchRideActivity();
            fragmentTransaction = true;
        } else if (id == R.id.my_car) {
            fragment = new CarListActivity();
            fragmentTransaction = true;
        } else if (id == R.id.profile) {
            Intent i = new Intent(this, MyProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.my_routes) {
            fragment = new MyRidesActivity();
            fragmentTransaction = true;
        } else if (id == R.id.settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.logout){
            unsuscribeTopic();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        if(fragmentTransaction) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }
        drawer.closeDrawers();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    //subscribe topics
    public void suscribeTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseUser.getUid());
    }

    //unsubscribe topics
    public void unsuscribeTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseUser.getUid());
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
