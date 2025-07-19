package com.onurtas.fleettracker.Model;

import org.json.JSONObject;

public class DashboardStats {
    public int activeVehicles;
    public int parkedVehicles;
    public int crashes;
    public int breaches;
    public int underMaintenance;
    public int fuelConsumptions;

    public DashboardStats(JSONObject obj) throws Exception {
        this.activeVehicles = obj.getInt("active_vehicles");
        this.parkedVehicles = obj.getInt("parked_vehicles");
        this.crashes = obj.getInt("crashes");
        this.breaches = obj.getInt("breaches");
        this.underMaintenance = obj.getInt("under_maintenance");
        this.fuelConsumptions = obj.getInt("fuel_consumptions");
    }
}