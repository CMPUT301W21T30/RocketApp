package com.example.rocketapp;

import android.util.Log;

import static android.content.ContentValues.TAG;

class ExperimentInfo {
    private DataManager.ID ownerId;
    private String name;
    private String description;
    private String region;
    private int minTrials;
    private boolean geoLocationEnabled;

    public ExperimentInfo() { }

    public ExperimentInfo(String name, String description, String region, int minTrials, boolean geoLocationEnabled) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.minTrials = minTrials;
        this.geoLocationEnabled = geoLocationEnabled;
    }

    public DataManager.ID getOwner() { return ownerId; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRegion() {
        return region;
    }

    public int getMinTrials() {
        return minTrials;
    }

    public boolean isGeoLocationEnabled() {
        return geoLocationEnabled;
    }

    public void setOwner(DataManager.ID id) {
        ownerId = id;
    }

    public void setName(User owner, String name) {
        if (hasPermission(owner)) this.name = name;
    }

    public void setDescription(User owner, String description) {
        if (hasPermission(owner)) this.description = description;
    }

    public void setRegion(User owner, String region) {
        if (hasPermission(owner)) this.region = region;
    }

    public void setMinTrials(User owner, int minTrials) {
        if (hasPermission(owner)) this.minTrials = minTrials;
    }

    public void setGeoLocationEnabled(User owner, boolean geoLocationEnabled) {
        if (hasPermission(owner)) this.geoLocationEnabled = geoLocationEnabled;
    }

    boolean hasPermission(User owner) {
        boolean permission = this.ownerId.equals(owner.getId());
        if (permission)
            return true;
        else
            Log.d(TAG, "Error: Tried to set property without permission");
        return false;
    }

    @Override
    public String toString() {
        return "ExperimentInfo{" +
                "owner=" + ownerId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", region='" + region + '\'' +
                ", minTrials=" + minTrials +
                ", geoLocationEnabled=" + geoLocationEnabled +
                '}';
    }


}