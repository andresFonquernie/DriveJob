package es.fonkyprojects.drivejob.preference;

import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import es.fonkyprojects.drivejob.activity.R;

/**
 * Created by andre on 26/01/2017.
 */

public class myPreference extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    PendingIntent pendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("distance")){
            EditTextPreference pref = (EditTextPreference) findPreference("distance");
            int i = Integer.parseInt(pref.getText());
            if(i<1500)
                pref.setSummary(pref.getText());
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(getContext(), "Max distance is 1500", Toast.LENGTH_LONG).show();
            }
        }
        if(key.equals("time")){
            EditTextPreference pref = (EditTextPreference) findPreference("time");
            pref.setSummary(pref.getText());
        }
    }
}
