package com.onurtas.fleettracker.Repository;

import android.app.Application;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.onurtas.fleettracker.Helper.ApiConstants;
import com.onurtas.fleettracker.Helper.NetworkBridge;
import com.onurtas.fleettracker.Model.Driver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    private static final String TAG = "DriverRepository";
    private final RequestQueue requestQueue;

    public DriverRepository(Application application) {
        requestQueue = NetworkBridge.getInstance().getRequestQueue();
    }


    public void fetchDrivers(final FetchDriversCallback callback) {
        String requestURL = ApiConstants.DRIVERS_URL;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null, response -> {
            Log.d(TAG, "Response fetchDriversFromServer: " + response.toString());
            List<Driver> drivers = new ArrayList<>();
            try {
                if (response.length() == 0) {
                    Log.i(TAG, "No drivers found");
                    callback.onDriversLoaded(drivers);
                    return;
                }

                for (int i = 0; i < response.length(); i++) {
                    JSONObject rawDriverJson = response.getJSONObject(i);
                    Driver driver = new Driver();
                    driver.driverId = rawDriverJson.optInt("driver_id");
                    driver.driverName = rawDriverJson.optString("name", rawDriverJson.optString("driver_name"));
                    driver.plate = rawDriverJson.optString("plate");
                    driver.date = rawDriverJson.optLong("date", 0L);
                    driver.score = rawDriverJson.optInt("score");
                    drivers.add(driver);
                }
                callback.onDriversLoaded(drivers);
            } catch (JSONException e) {
                Log.e(TAG, "JSON Parsing error in fetchDriversFromServer: " + e.getMessage());
                callback.onError(e);
            }
        }, error -> {
            Log.e(TAG, "Volley error in fetchDriversFromServer: " + error.toString());
            callback.onError(error);
        });
        requestQueue.add(getRequest);
    }

    public void fetchDriver(final FetchDriverCallback callback) {
        String requestURL = ApiConstants.DRIVER_DETAILS_URL;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, requestURL, null, response -> {
            Log.d(TAG, "Response fetchDriverScoreFromServer: " + response.toString());
            try {
                Driver driver = new Driver();
                driver.driverId = response.optInt("driver_id");
                driver.driverName = response.optString("driver_name");
                driver.plate = response.optString("plate");
                driver.score = response.optInt("score");
                driver.place = response.optString("place");
                driver.speedRate = response.optInt("speed_rate");
                driver.mobileUsage = response.optInt("mobile_usage");
                driver.brakeRate = response.optInt("brake_rate");
                driver.revRate = response.optInt("rev_rate");
                driver.totalMobileUsage = response.optInt("total_mobile_usage");
                driver.totalHardBrake = response.optInt("total_hard_brake");
                driver.accelerationRate = response.optString("acceleration_rate");
                driver.totalMileage = response.optInt("total_mileage");
                driver.totalTrips = response.optInt("total_trips");
                driver.date = response.optLong("date", 0L);
                driver.lastUpdated = response.optLong("last_updated", 0L);
                callback.onDriverLoaded(driver);
            } catch (Exception e) {
                Log.e(TAG, "JSON Parsing error in fetchDriverScoreFromServer: " + e.getMessage());
                callback.onError(e);
            }
        }, error -> {
            Log.e(TAG, "Volley error in fetchDriverScoreFromServer: " + error.toString());
            callback.onError(error);
        });
        requestQueue.add(getRequest);
    }

    public interface FetchDriverCallback {
        void onDriverLoaded(Driver driver);

        void onError(Exception error);
    }

    public interface FetchDriversCallback {
        void onDriversLoaded(List<Driver> drivers);

        void onError(Exception error);
    }

}