package com.onurtas.fleettracker.Repository;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.onurtas.fleettracker.Helper.ApiConstants;
import com.onurtas.fleettracker.Model.DashboardResult;
import com.onurtas.fleettracker.Model.DashboardStats;
import com.onurtas.fleettracker.Model.VehicleMarker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardRepository {

    private final RequestQueue requestQueue;

    public DashboardRepository(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void fetchDashboardData(DashboardDataCallback callback) {
        String requestURL = ApiConstants.DASHBOARD_URL;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, requestURL, null,
                rawDashboard -> {
                    try {
                        if (rawDashboard.length() == 0) {
                            callback.onError("No data received.");
                            return;
                        }

                        DashboardStats stats = new DashboardStats(rawDashboard);
                        JSONArray vehicleLocations = rawDashboard.getJSONArray("vehicleLocations");

                        List<VehicleMarker> markers = new ArrayList<>();

                        for (int i = 0; i < vehicleLocations.length(); i++) {
                            JSONObject rawVehicleMarkerJson = vehicleLocations.getJSONObject(i);
                            VehicleMarker marker = new VehicleMarker(rawVehicleMarkerJson);
                            markers.add(marker);
                        }

                        DashboardResult result = new DashboardResult(stats, markers);
                        callback.onSuccess(result);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Error parsing JSON structure: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("Error processing dashboard data: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onError("Ops.. Something went wrong with the network request.");
                });

        requestQueue.add(getRequest);
    }

    public interface DashboardDataCallback {
        void onSuccess(DashboardResult data);

        void onError(String errorMessage);
    }
}
