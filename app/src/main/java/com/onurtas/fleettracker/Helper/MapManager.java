package com.onurtas.fleettracker.Helper;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.onurtas.fleettracker.Model.Idle;
import com.onurtas.fleettracker.Model.Trip;
import com.onurtas.fleettracker.Model.VehicleMarker;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private List<Marker> currentMarkers = new ArrayList<>();

    private GoogleMap googleMap;
    private MapsCommon mapsCommon;

    public MapManager(GoogleMap googleMap, MapsCommon mapsCommon) {
        this.googleMap = googleMap;
        this.mapsCommon = mapsCommon;
    }

    public void updateMarkers(List<VehicleMarker> vehicleMarkers) {
        if (googleMap == null) return;

        clearMarkers();

        for (VehicleMarker vm : vehicleMarkers) {
            LatLng pos = vm.position;
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(vm.plate)
                    .snippet(vm.info)
                    .icon(BitmapDescriptorFactory.defaultMarker(vm.isActive
                            ? BitmapDescriptorFactory.HUE_GREEN
                            : BitmapDescriptorFactory.HUE_RED)));
            currentMarkers.add(m);
        }

        if (!currentMarkers.isEmpty()) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarkers.get(0).getPosition(), 9f));
        }
    }

    public void clearMarkers() {
        for (Marker m : currentMarkers) m.remove();
        currentMarkers.clear();
    }

    /**
     * Updates the map to display trip details including route, start/end markers,
     * and idle locations.
     *
     * @param trip            The Trip object containing details to display.
     * @param activityContext Context to run UI thread operations (e.g., requireActivity())
     */
    public void displayTripDetails(@NonNull Trip trip, @NonNull android.app.Activity activityContext) {
        if (googleMap == null) return;

        googleMap.clear();

        LatLng origin = new LatLng(trip.start_lat, trip.start_lon);
        LatLng dest = new LatLng(trip.end_lat, trip.end_lon);


        googleMap.addMarker(new MarkerOptions()
                .position(origin)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        googleMap.addMarker(new MarkerOptions()
                .position(dest)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        for (Idle idle : trip.idles) {
            LatLng idlePos = new LatLng(idle.latitute, idle.longitude);
            googleMap.addMarker(new MarkerOptions()
                            .position(idlePos)
                            .title(idle.idle_name + " (" + idle.duration + " min)")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                    .showInfoWindow();
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin);
        builder.include(dest);
        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100); // 100 = padding
        googleMap.animateCamera(cameraUpdate);

        // mapsCommon.getDirectionsFromRoutesApi and decodePolylineFromJson
        // should ideally be asynchronous or handled in a way that doesn't block the main thread
        new Thread(() -> {
            try {
                String json = mapsCommon.getDirectionsFromRoutesApi(origin, dest);
                List<LatLng> path = mapsCommon.decodePolylineFromJson(json);
                if (path != null && !path.isEmpty()) {
                    PolylineOptions options = new PolylineOptions()
                            .addAll(path)
                            .width(10)
                            .color(Color.BLUE)
                            .geodesic(true);

                    // To update UI from a background thread, you need to post to the main thread
                    activityContext.runOnUiThread(() -> googleMap.addPolyline(options));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}