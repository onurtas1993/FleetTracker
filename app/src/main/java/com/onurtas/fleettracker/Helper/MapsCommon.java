package com.onurtas.fleettracker.Helper;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsCommon {

    private String mapsKey;

    public MapsCommon(String mapKey) {
        this.mapsKey = mapKey;
    }

    public String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + mapsKey;


        return url;
    }

    public String getDirectionsFromRoutesApi(LatLng origin, LatLng dest) throws IOException, JSONException {
        String urlString = "https://routes.googleapis.com/directions/v2:computeRoutes?key=" + mapsKey;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        // Add the required field mask header:
        conn.setRequestProperty("X-Goog-FieldMask", "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline");
        conn.setDoOutput(true);

        // Build JSON request body
        JSONObject jsonBody = new JSONObject();

        JSONObject originObj = new JSONObject();
        JSONObject originLocation = new JSONObject();
        JSONObject originLatLng = new JSONObject();
        originLatLng.put("latitude", origin.latitude);
        originLatLng.put("longitude", origin.longitude);
        originLocation.put("latLng", originLatLng);
        originObj.put("location", originLocation);

        JSONObject destObj = new JSONObject();
        JSONObject destLocation = new JSONObject();
        JSONObject destLatLng = new JSONObject();
        destLatLng.put("latitude", dest.latitude);
        destLatLng.put("longitude", dest.longitude);
        destLocation.put("latLng", destLatLng);
        destObj.put("location", destLocation);

        jsonBody.put("origin", originObj);
        jsonBody.put("destination", destObj);
        jsonBody.put("travelMode", "DRIVE");

        // Write JSON to output stream
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();

        InputStream is = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }
        br.close();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode + ", response: " + response.toString());
        }

        return response.toString();
    }


    public List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            poly.add(new LatLng(lat / 1E5, lng / 1E5));
        }

        return poly;
    }

    public List<LatLng> decodePolylineFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray routes = jsonObject.getJSONArray("routes");

            if (routes.length() > 0) {
                JSONObject firstRoute = routes.getJSONObject(0);
                JSONObject polyline = firstRoute.getJSONObject("polyline");
                String encoded = polyline.getString("encodedPolyline");

                return decodePolyline(encoded);
            }

        } catch (JSONException e) {
            Log.e("MapsCommon", "Error parsing polyline JSON", e);
        }

        return Collections.emptyList();
    }

}
