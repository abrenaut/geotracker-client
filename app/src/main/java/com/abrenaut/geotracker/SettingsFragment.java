package com.abrenaut.geotracker;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Patterns;

/**
 * Created by arthurbrenaut on 07/09/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_ADDRESS = "address";
    public static final String KEY_SECURE = "secure";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_STATUS = "status";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        findPreference(KEY_ADDRESS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Check the server address validity
                return newValue != null && !((String) newValue).isEmpty();
            }
        });
        findPreference(KEY_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Interval needs to be a positive integer
                return newValue != null && ((String) newValue).matches("\\d+");
            }
        });
    }

    public void setPreferencesEnabled(boolean enabled) {
        findPreference(KEY_ADDRESS).setEnabled(enabled);
        findPreference(KEY_SECURE).setEnabled(enabled);
        findPreference(KEY_INTERVAL).setEnabled(enabled);
    }

}