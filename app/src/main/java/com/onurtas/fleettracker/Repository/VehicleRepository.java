package com.onurtas.fleettracker.Repository; // Package name from your file

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.onurtas.fleettracker.Helper.ApiConstants;
import com.onurtas.fleettracker.Helper.NetworkBridge;
import com.onurtas.fleettracker.Model.Car;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VehicleRepository { // Class name from your file

    private static final String TAG = "VehicleRepository";
    private final RequestQueue requestQueue;

    public VehicleRepository() {
        requestQueue = NetworkBridge.getInstance().getRequestQueue();
    }

    public void fetchCars(final OnDataFetchedCallback callback) {
        String requestURL = ApiConstants.CARS_URL;

        JsonArrayRequest getRequest = new JsonArrayRequest(
                Request.Method.GET, requestURL, null,
                response -> {
                    try {
                        List<Car> parsedCars = new ArrayList<>();
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject rawCar = response.getJSONObject(i);
                                Car car = new Car();
                                car.car_id = rawCar.optString("car_id");
                                car.picture = rawCar.optString("picture");
                                car.brand = rawCar.optString("brand");
                                car.model = rawCar.optString("model");
                                car.year = rawCar.optInt("year");
                                car.color = rawCar.optString("color");
                                car.transmission = rawCar.optString("transmission");
                                car.plate = rawCar.optString("plate");
                                parsedCars.add(car);
                            }
                        }
                        if (callback != null) {
                            callback.onSuccess(parsedCars);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: ", e);
                        if (callback != null) {
                            callback.onError("Error parsing data.");
                        }
                    }
                },
                error -> {
                    Log.e(TAG, "Volley network error: ", error);
                    if (callback != null) {
                        callback.onError("Network request failed. Please try again.");
                    }
                }
        );
        requestQueue.add(getRequest);
    }

    public interface OnDataFetchedCallback {
        void onSuccess(List<Car> fetchedCars);

        void onError(String message);
    }
}