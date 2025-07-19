package com.onurtas.fleettracker.Model;

import java.util.List;

public class DashboardResult {
    private final DashboardStats stats;
    private final List<VehicleMarker> markers;

    public DashboardResult(DashboardStats stats, List<VehicleMarker> markers) {
        this.stats = stats;
        this.markers = markers;
    }

    public DashboardStats getStats() {
        return stats;
    }

    public List<VehicleMarker> getMarkers() {
        return markers;
    }
}
