package com.example.rocketapp.model.trials;

import android.location.Location;

/**
 * Represents a location no the globe using longitude and latitude
 */
public class Geolocation {
    private double longitude;
    private double latitude;

    /**
     * Default constructor for firestore serialization
     */
    public Geolocation() { }

    public Geolocation(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }

    public Geolocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
