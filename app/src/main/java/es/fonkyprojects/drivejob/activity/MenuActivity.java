package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{


    @Bind(R.id.btn_create) Button btnCreate;
    @Bind(R.id.btn_search) Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);


        btnCreate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_profile:
                break;
            case R.id.mnu_settings:
                //Intent itemSettings = new Intent(this, MySettingsActivity.class);
                //startActivity(itemSettings);
                break;
            case R.id.mnu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void create(){
        startActivity(new Intent(MenuActivity.this, CreateRideActivity.class));
    }

    private void search(){
        startActivity(new Intent(MenuActivity.this, SearchRideActivity.class));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_create) {
            create();
        } else if (i == R.id.btn_search) {
            search();
        }

    }
}
