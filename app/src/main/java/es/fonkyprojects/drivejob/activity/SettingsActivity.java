package es.fonkyprojects.drivejob.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.preference.SeekBarPreference;

public class SettingsActivity extends PreferenceActivity {

    @BindView(R.id.my_toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setActionBar(toolbar);
        getActionBar().setTitle(R.string.settings);
        int titleId = getResources().getIdentifier(getString(R.string.settings), "id", getPackageName());
        TextView abTitle = (TextView) findViewById(titleId);
        abTitle.setTextColor(getResources().getColor(R.color.white));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SeekBarPreference distanceSeek;
        private SeekBarPreference timeSeek;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            int radius;
            addPreferencesFromResource(R.xml.preferences);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            distanceSeek = (SeekBarPreference) this.findPreference("DISTANCE");
            radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("DISTANCE", 500);
            distanceSeek.setSummary(this.getString(R.string.goDis).replace("$1", ""+radius));

            timeSeek = (SeekBarPreference) this.findPreference("TIME");
            radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("TIME", 30);
            timeSeek.setSummary(this.getString(R.string.goDis).replace("$1", ""+radius));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("DISTANCE")) {
                int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("DISTANCE", 500);
                distanceSeek.setSummary(this.getString(R.string.goDis).replace("$1", "" + radius));
            }
            else if(key.equals("TIME")){
                int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("TIME", 30);
                timeSeek.setSummary(this.getString(R.string.goDis).replace("$1", "" + radius));
            }
        }
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