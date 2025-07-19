package com.onurtas.fleettracker.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Driver {

    public int driverId;
    public String driverName;
    public String plate;
    public long date;
    public int score;
    public String place;
    public int speedRate;
    public int mobileUsage;
    public int brakeRate;
    public int revRate;
    public int totalMobileUsage;
    public int totalHardBrake;
    public String accelerationRate;
    public int totalMileage;
    public int totalTrips;
    public long lastUpdated;

    public Driver() {
    }

    public Date getLastUpdatedDateObject() { // Renamed for clarity
        if (this.lastUpdated > 0) {
            return new Date(this.lastUpdated * 1000L);
        }
        return null;
    }

    // get 'lastUpdated' as a formatted string
    public String getLastUpdatedFormatted() {
        Date dateObj = getLastUpdatedDateObject();
        if (dateObj != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return sdf.format(dateObj);
        }
        return ""; // Or "N/A"? or null?
    }

}