package com.onurtas.fleettracker.Helper;

import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class GeoUtils {

    private static final String TAG = "GeoUtils";
    private final ExecutorService executor;
    private final Handler mainHandler;

    public GeoUtils(ExecutorService executor, Handler mainHandler) {
        this.executor = executor;
        this.mainHandler = mainHandler;
    }

    public void getFriendlyNameAsync(String apiKey, double lat, double lon, UnifiedGeoCallback callback) {
        executor.execute(() -> {
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon +
                    "&sensor=true&key=" + apiKey;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
                StringBuilder contentBuilder = new StringBuilder();
                String str;
                while ((str = in.readLine()) != null) {
                    contentBuilder.append(str);
                }

                JSONObject json = new JSONObject(contentBuilder.toString());
                JSONArray results = json.optJSONArray("results"); // Use optJSONArray for safer parsing

                String friendlyName = null;

                if (results != null && results.length() > 0) {
                    JSONObject firstResult = results.optJSONObject(0);
                    if (firstResult != null) {
                        JSONArray components = firstResult.optJSONArray("address_components");
                        if (components != null && components.length() > 0) {
                            String streetName = findComponentName(components, "route");
                            if (streetName == null) {
                                streetName = findComponentName(components, "street_address");
                            }
                            if (streetName == null) { // Fallback to a broader component if specific ones aren't found
                                streetName = findComponentName(components, "locality");
                            }
                            if (streetName == null && components.length() > 1) { // Original logic as a further fallback
                                JSONObject component = components.optJSONObject(1);
                                if (component != null) {
                                    streetName = component.optString("short_name");
                                }
                            }
                            if (streetName == null) { // Ultimate fallback to the very first component
                                JSONObject component = components.optJSONObject(0);
                                if (component != null) {
                                    streetName = component.optString("short_name");
                                }
                            }
                            friendlyName = streetName;
                        }
                    }
                }

                if (friendlyName != null && !friendlyName.isEmpty()) {
                    final String finalFriendlyName = friendlyName;
                    mainHandler.post(() -> callback.onNameReady(finalFriendlyName));
                } else {
                    // If no suitable name found, treat as an error or a specific "not found" case
                    Log.w(TAG, "No friendly name found for: " + lat + "," + lon);
                    mainHandler.post(() -> callback.onNameReady("Unknown Location"));
                }

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching or parsing geo data for: " + lat + "," + lon, e);
                mainHandler.post(() -> callback.onError(e));
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error in getFriendlyNameAsync for: " + lat + "," + lon, e);
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    /**
     * Helper to find a specific component type's short_name from address_components.
     *
     * @param components JSONArray of address components.
     * @param type       The type of component to look for (e.g., "route", "locality").
     * @return The short_name of the found component, or null if not found.
     */
    private String findComponentName(JSONArray components, String type) {
        if (components == null) return null;
        for (int i = 0; i < components.length(); i++) {
            JSONObject component = components.optJSONObject(i);
            if (component != null) {
                JSONArray types = component.optJSONArray("types");
                if (types != null) {
                    for (int j = 0; j < types.length(); j++) {
                        if (type.equals(types.optString(j))) {
                            return component.optString("short_name");
                        }
                    }
                }
            }
        }
        return null; // Type not found
    }

    // Callback interface for asynchronous name resolution
    public interface UnifiedGeoCallback {
        void onNameReady(String name);

        void onError(Exception e);
    }
}