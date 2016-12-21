package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.os.Bundle;

import es.fonkyprojects.drivejob.settings.mySettingsFragments;

public class MySettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new mySettingsFragments())
                .commit();
    }
}
