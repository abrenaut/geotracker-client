/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
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

import android.location.Location;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Position {

    private String deviceId;

    private Date time;

    private double latitude;

    private double longitude;

    private double altitude;

    private double speed;

    private double course;

    public Position(String deviceId, Location location) {
        this.deviceId = deviceId;
        this.time = new Date(location.getTime());
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.speed = location.getSpeed() * 1.943844; // speed in knots
        this.course = location.getBearing();
    }

    public String toJSONString() throws JSONException {
        JSONObject params = new JSONObject();

        params.put("uid", this.deviceId);
        params.put("time", this.time);
        params.put("lat", this.latitude);
        params.put("lng", this.longitude);
        params.put("alt", this.altitude);
        params.put("spd", this.speed);
        params.put("crs", this.course);

        return params.toString();
    }

    @Override
    public String toString() {
        return " lat:" + this.latitude +
                ", lon:" + this.longitude;
    }
}
