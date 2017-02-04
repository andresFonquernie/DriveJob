package es.fonkyprojects.drivejob.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import es.fonkyprojects.drivejob.preference.SeekBarPreference;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
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
            radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("DISTANCE", 50);
            distanceSeek.setSummary(this.getString(R.string.goDis).replace("$1", ""+radius));

            timeSeek = (SeekBarPreference) this.findPreference("TIME");
            radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("TIME", 50);
            timeSeek.setSummary(this.getString(R.string.goDis).replace("$1", ""+radius));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("DISTANCE")) {
                int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("DISTANCE", 500);
                distanceSeek.setSummary(this.getString(R.string.goDis).replace("$1", "" + radius));
            }
            else if(key.equals("TIME")){
                int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("TIME", 15);
                timeSeek.setSummary(this.getString(R.string.goDis).replace("$1", "" + radius));
            }
        }
    }
}