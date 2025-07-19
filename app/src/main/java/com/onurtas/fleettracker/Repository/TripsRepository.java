package com.onurtas.fleettracker.Repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.onurtas.fleettracker.Helper.ApiConstants;
import com.onurtas.fleettracker.Helper.GeoUtils;
import com.onurtas.fleettracker.Helper.NetworkBridge;
import com.onurtas.fleettracker.Model.Trip;
import com.onurtas.fleettracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TripsRepository {

    private static final String TAG = "TripsRepository";
    private final GeoUtils geoUtils;
    private final String mapsApiKey;
    private final RequestQueue requestQueue;

    public TripsRepository(Context context, GeoUtils geoUtils) {
        this.geoUtils = geoUtils;
        this.mapsApiKey = context.getString(R.string.google_maps_key);
        this.requestQueue = NetworkBridge.getInstance().getRequestQueue();
    }

    public void fetchDirections(String origin, String dest, final DirectionsCallback callback) {
        String directionsUrl = buildDirectionsUrl(origin, dest);

        JsonObjectRequest directionsRequest = new JsonObjectRequest(Request.Method.GET, directionsUrl, null,
                response -> callback.onDirectionsLoaded(response.toString()),
                error -> {
                    Log.e(TAG, "fetchDirections error: ", error);
                    callback.onError(error);
                }
        );
        requestQueue.add(directionsRequest);
    }

    private String buildDirectionsUrl(String origin, String dest) {
        return "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + origin +
                "&destination=" + dest +
                "&key=" + mapsApiKey;
    }

    public void fetchTrips(final TripsCallback callback) {
        String requestURL = ApiConstants.TRIPS_URL;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,
                response -> parseTrips(response, callback),
                error -> {
                    Log.e(TAG, "fetchTrips error: ", error);
                    callback.onError(error);
                }
        );
        requestQueue.add(getRequest);
    }

    private void parseTrips(JSONArray rawTrips, final TripsCallback callback) {
        final ArrayList<Trip> trips = new ArrayList<>();
        if (rawTrips == null || rawTrips.length() == 0) {
            callback.onTripsLoaded(trips);
            return;
        }

        try {
            for (int i = 0; i < rawTrips.length(); i++) {
                JSONObject rawTrip = rawTrips.getJSONObject(i);
                Trip trip = new Trip();

                JSONArray startGeo = rawTrip.getJSONArray("start_geolocation");
                trip.start_lat = startGeo.getDouble(0);
                trip.start_lon = startGeo.getDouble(1);

                JSONArray endGeo = rawTrip.optJSONArray("end_geolocation");
                if (endGeo != null && endGeo.length() >= 2) {
                    trip.end_lat = endGeo.getDouble(0);
                    trip.end_lon = endGeo.getDouble(1);
                } else {
                    trip.end_lat = 0.0;
                    trip.end_lon = 0.0;
                }

                trip.distance = rawTrip.getInt("distance");
                trip.driver_name = rawTrip.getString("driver");
                trip.plate = rawTrip.getString("plate");
                trip.start_epoch_date = rawTrip.getLong("start_epoch_date");
                trip.end_epoch_date = rawTrip.getLong("end_epoch_date");
                trip.elapsed_time_in_minutes = rawTrip.getInt("elapsed_time_in_minutes");
                trip.fuel_consumption = rawTrip.getInt("fuel_consumption");

                trips.add(trip);
            }
            resolveFriendlyNamesForTripList(trips, callback);

        } catch (JSONException e) {
            Log.e(TAG, "parseTrips error: ", e);
            callback.onError(e);
        }
    }

    public void fetchTripDetails(final TripDetailsCallback callback) {
        String requestURL = ApiConstants.TRIP_DETAILS_URL;

        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestURL,
                null,
                response -> parseTripDetails(response, callback),
                error -> {
                    Log.e(TAG, "fetchTripDetails error: ", error);
                    callback.onError(error);
                }
        );
        requestQueue.add(getRequest);
    }

    private void parseTripDetails(JSONObject rawTrip, final TripDetailsCallback callback) {
        final Trip trip = new Trip();
        try {
            JSONArray startGeo = rawTrip.getJSONArray("start_geolocation");
            trip.start_lat = startGeo.getDouble(0);
            trip.start_lon = startGeo.getDouble(1);

            JSONArray endGeo = rawTrip.optJSONArray("end_geolocation");
            if (endGeo != null && endGeo.length() >= 2) {
                trip.end_lat = endGeo.getDouble(0);
                trip.end_lon = endGeo.getDouble(1);
            } else {
                trip.end_lat = 0.0;
                trip.end_lon = 0.0;
            }

            trip.start_epoch_date = rawTrip.getLong("start_epoch_date");
            trip.end_epoch_date = rawTrip.getLong("end_epoch_date");
            trip.driver_name = rawTrip.getString("driver_name");
            trip.plate = rawTrip.getString("plate");
            trip.distance = rawTrip.getInt("distance");
            trip.fuel_consumption = rawTrip.getInt("fuel_consumption");
            trip.elapsed_time_in_minutes = rawTrip.getInt("elapsed_time_in_minutes");
            trip.overall_driving_score = rawTrip.getInt("overall_driving_score");
            trip.safety_score = rawTrip.getInt("safety_score");
            trip.economy_score = rawTrip.getInt("economy_score");

            resolveSingleTripFriendlyNames(trip,
                    () -> callback.onTripLoaded(trip)
            );

        } catch (JSONException e) {
            Log.e(TAG, "parseTripDetails error during initial parsing: ", e);
            callback.onError(e);
        }
    }

    private void resolveFriendlyNamesForTripList(final ArrayList<Trip> trips, final TripsCallback callback) {
        if (trips.isEmpty()) {
            callback.onTripsLoaded(trips);
            return;
        }

        final AtomicInteger resolvedCount = new AtomicInteger(0);
        final int totalTrips = trips.size();

        for (final Trip trip : trips) {
            resolveSingleTripFriendlyNames(trip,
                    () -> { // onAllNamesResolved for this trip
                        if (resolvedCount.incrementAndGet() == totalTrips) {
                            callback.onTripsLoaded(trips);
                        }
                    }
            );
        }
    }

    private void resolveSingleTripFriendlyNames(final Trip trip, final Runnable onAllNamesResolved) {
        final AtomicInteger completionCounter = new AtomicInteger(0);
        final int TOTAL_GEOCODING_TASKS = 2;

        final Runnable checkCompletion = () -> {
            if (completionCounter.incrementAndGet() == TOTAL_GEOCODING_TASKS) {
                onAllNamesResolved.run();
            }
        };

        geoUtils.getFriendlyNameAsync(mapsApiKey, trip.start_lat, trip.start_lon,
                new GeoUtils.UnifiedGeoCallback() {
                    @Override
                    public void onNameReady(String name) {
                        trip.friendly_start_street_name = (name != null && !name.isEmpty()) ? name : "Unknown Location";
                        checkCompletion.run();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.w(TAG, "Failed to get start friendly name for trip (plate: " + trip.plate + "). Lat: " + trip.start_lat + ", Lon: " + trip.start_lon, e);
                        trip.friendly_start_street_name = "Unknown Location";
                        // We still call checkCompletion to ensure the flow continues for this trip
                        checkCompletion.run();
                    }
                });

        // Fetch end address friendly name
        if (trip.end_lat != 0.0 || trip.end_lon != 0.0) {
            geoUtils.getFriendlyNameAsync(mapsApiKey, trip.end_lat, trip.end_lon,
                    new GeoUtils.UnifiedGeoCallback() {
                        @Override
                        public void onNameReady(String name) {
                            trip.friendly_end_street_name = (name != null && !name.isEmpty()) ? name : "Unknown Location";
                            checkCompletion.run();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.w(TAG, "Failed to get end friendly name for trip (plate: " + trip.plate + "). Lat: " + trip.end_lat + ", Lon: " + trip.end_lon, e);
                            trip.friendly_end_street_name = "Unknown Location";
                            // We still call checkCompletion
                            checkCompletion.run();
                        }
                    });
        } else {
            // If end location is not set, consider it "resolved" for the counter
            trip.friendly_end_street_name = "Not Specified";
            checkCompletion.run();
        }
    }


    // Callbacks
    public interface DirectionsCallback {
        void onDirectionsLoaded(String directionsJson);

        void onError(Exception error);
    }


    public interface TripsCallback {
        void onTripsLoaded(ArrayList<Trip> trips);

        void onError(Exception error);
    }

    public interface TripDetailsCallback {
        void onTripLoaded(Trip trip);

        void onError(Exception error);
    }
}