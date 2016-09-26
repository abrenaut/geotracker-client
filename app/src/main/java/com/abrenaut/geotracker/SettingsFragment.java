package com.abrenaut.geotracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.webkit.URLUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by arthurbrenaut on 07/09/2016.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_ADDRESS = "address";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_STATUS = "status";

    private static final int PERMISSIONS_REQUEST_LOCATION = 2;
    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Set default values for preferences
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        findPreference(KEY_ADDRESS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Check the server address validity
                return URLUtil.isValidUrl((String) newValue);
            }
        });
        findPreference(KEY_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // Interval needs to be a positive integer
                return newValue != null && ((String) newValue).matches("\\d+");
            }
        });

        // If tracking is enabled, start it
        if (sharedPreferences.getBoolean(SettingsFragment.KEY_STATUS, false)) {
            startTrackingService();
        }
    }

    public void setPreferencesEnabled(boolean enabled) {
        findPreference(KEY_ADDRESS).setEnabled(enabled);
        findPreference(KEY_INTERVAL).setEnabled(enabled);
    }

    private void startTrackingService() {
        boolean permission = checkPermissions();

        if (permission) {
            setPreferencesEnabled(false);
            getActivity().startService(new Intent(getActivity(), TrackingService.class));
        }
    }

    private boolean checkPermissions() {
        boolean permission = true;

        // Beginning in Android 6.0 (API level 23), users grant permissions to apps while the app is running, not when they install the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Set<String> missingPermissions = new HashSet<>();

            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (!missingPermissions.isEmpty()) {
                requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]), PERMISSIONS_REQUEST_LOCATION);
                permission = false;
            }
        }

        return permission;
    }

    private void stopTrackingService() {
        getActivity().stopService(new Intent(getActivity(), TrackingService.class));
        setPreferencesEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            boolean permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    (permissions.length < 2 || grantResults[1] == PackageManager.PERMISSION_GRANTED);

            // If user granted permissions to access location, start the tracking service
            if (permissionGranted) {
                startTrackingService();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsFragment.KEY_STATUS)) {
            // Toggle tracking service when user enables / disables the tracking
            if (sharedPreferences.getBoolean(SettingsFragment.KEY_STATUS, false)) {
                startTrackingService();
            } else {
                stopTrackingService();
            }
        }
    }

    // For proper lifecycle management in the activity, it is recommended to register and unregister your SharedPreferences.OnSharedPreferenceChangeListener
    // during the onResume() and onPause() callbacks, respectively
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}