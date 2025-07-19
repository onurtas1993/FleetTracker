package com.onurtas.fleettracker.Model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VehicleMarker {
    public LatLng position;
    public String plate;
    public String info;
    public boolean isActive;

    public VehicleMarker(JSONObject obj) throws Exception {
        double lat = obj.getDouble("latitude");
        double lon = obj.getDouble("longitude");
        this.position = new LatLng(lat, lon);
        this.plate = obj.getString("vehicle_plate");
        this.isActive = obj.getInt("activeness") == 1;

        long start = obj.getLong("last_seen_epoch_date") * 1000;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String time = fmt.format(new Date(start));
        this.info = time;
    }
}