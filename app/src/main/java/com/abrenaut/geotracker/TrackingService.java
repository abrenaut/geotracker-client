/*
 * Copyright 2012 - 2015 Anton Tananaev (anton.tananaev@gmail.com)
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TrackingService extends Service implements LocationListener {

    private String url;

    private String deviceId;

    private long period;

    private long lastUpdateTime;

    protected LocationManager locationManager;

    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    public void onCreate() {
        StatusActivity.addMessage("Tracking on");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String address = preferences.getString(SettingsFragment.KEY_ADDRESS, null);
        int port = Integer.parseInt(preferences.getString(SettingsFragment.KEY_PORT, null));
        boolean secure = preferences.getBoolean(SettingsFragment.KEY_SECURE, false);

        // Build the API URL
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(secure ? "https" : "http")
                .encodedAuthority(address + ':' + port)
                .appendPath("api")
                .appendPath("1.0")
                .appendPath("positions");
        url = builder.build().toString();

        // We use the Android ID to identify uniquely the device
        deviceId = Settings.Secure.ANDROID_ID;
        period = Integer.parseInt(preferences.getString(SettingsFragment.KEY_INTERVAL, null)) * 1000;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, period, 0, this);
    }

    @Override
    public void onDestroy() {
        StatusActivity.addMessage("Tracking off");
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && location.getTime() - lastUpdateTime > period) {
            StatusActivity.addMessage("New location");
            lastUpdateTime = location.getTime();
            try {
                send(new Position(deviceId, location));
            } catch (Exception e) {
                StatusActivity.addMessage("Could not send new location " + e.getMessage());
            }
        }
    }

    private void send(final Position position) throws JSONException, UnsupportedEncodingException {
        StatusActivity.addMessage(position.toString());
        client.post(this, url, new StringEntity(position.toJSONString()), "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                StatusActivity.addMessage("Send failure: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                StatusActivity.addMessage("Send success: " + responseString);
            }
        });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
