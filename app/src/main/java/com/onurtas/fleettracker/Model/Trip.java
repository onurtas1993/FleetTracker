package com.onurtas.fleettracker.Model;

import java.util.ArrayList;

public class Trip {


    public Long start_epoch_date;
    public Long end_epoch_date;
    public Double start_lat;
    public Double start_lon;
    public String friendly_start_street_name;
    public String friendly_end_street_name;
    public String driver_name;
    public String plate;
    public Double end_lat;
    public Double end_lon;
    public int distance;
    public int fuel_consumption;
    public int elapsed_time_in_minutes;
    public int safety_score;
    public int economy_score;
    public int overall_driving_score;
    public ArrayList<Idle> idles = new ArrayList<>();

    public Trip() {
    }

}
