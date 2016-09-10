/*
 * Copyright 2012 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abrenaut.geotracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 2;

    private SharedPreferences sharedPreferences;
    private SettingsFragment settingsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Display the settings fragment as the main content.
        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

        // Set default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // If tracking is enabled, start it
        if (sharedPreferences.getBoolean(SettingsFragment.KEY_STATUS, false)) {
            startTrackingService();
        }
    }

    private void startTrackingService() {
        boolean permission = checkPermissions();

        if (permission) {
            settingsFragment.setPreferencesEnabled(false);
            startService(new Intent(this, TrackingService.class));
        }
    }

    private boolean checkPermissions() {
        boolean permission = true;

        // Beginning in Android 6.0 (API level 23), users grant permissions to apps while the app is running, not when they install the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Set<String> missingPermissions = new HashSet<>();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        stopService(new Intent(this, TrackingService.class));
        settingsFragment.setPreferencesEnabled(true);
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
    protected void onResume() {
        super.onResume();
        settingsFragment.getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settingsFragment.getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.status) {
            startActivity(new Intent(this, StatusActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
