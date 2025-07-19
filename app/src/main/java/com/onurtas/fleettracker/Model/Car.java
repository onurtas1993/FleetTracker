package com.onurtas.fleettracker.Model;

public class Car {

    public String car_id;
    public String picture;
    public String brand;
    public String model;
    public int year;
    public String color;
    public String transmission;
    public String plate;
    public Status status;

    public Car() {
    }

    public Car(Car car) {
        this.car_id = car.car_id;
        this.picture = car.picture;
        this.brand = car.brand;
        this.model = car.model;
        this.year = car.year;
        this.color = car.color;
        this.transmission = car.transmission;
        this.plate = car.plate;
        this.status = car.status;
    }

    public static class Status {
        public final int totalMiles;
        public final int totalTrips;
        public final int mpg;
        public final String status;
        public final int batteryVoltage;
        public final int exhaustEmission;
        public final int engineCoolantTemp;
        public final int transportation;
        public final String socketStatus;

        public Status(int totalMiles, int totalTrips, int mpg, String status,
                      int batteryVoltage, int exhaustEmission, int engineCoolantTemp,
                      int transportation, String socketStatus) {
            this.totalMiles = totalMiles;
            this.totalTrips = totalTrips;
            this.mpg = mpg;
            this.status = status;
            this.batteryVoltage = batteryVoltage;
            this.exhaustEmission = exhaustEmission;
            this.engineCoolantTemp = engineCoolantTemp;
            this.transportation = transportation;
            this.socketStatus = socketStatus;
        }
    }
}
