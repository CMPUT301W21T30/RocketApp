package com.example.rocketapp;

public class ExperimentInfo {
//    private DataManager.ID ownerId;
    private String description;
    private String region;
    private int minTrials;
    private boolean geoLocationEnabled;

    public ExperimentInfo() { }

    public ExperimentInfo(String description, String region, int minTrials, boolean geoLocationEnabled) {
        this.description = description;
        this.region = region;
        this.minTrials = minTrials;
        this.geoLocationEnabled = geoLocationEnabled;
    }

//    public DataManager.FirestoreObject.ID getOwner() { return ownerId; }

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

//    public void setOwner(DataManager.ID id) {
//        ownerId = id;
//    }

    public void setDescription(User owner, String description) {
//        if (hasPermission(owner))
            this.description = description;
    }

    public void setRegion(User owner, String region) {
//        if (hasPermission(owner))
            this.region = region;
    }

    public void setMinTrials(User owner, int minTrials) {
//        if (hasPermission(owner))
            this.minTrials = minTrials;
    }

    public void setGeoLocationEnabled(User owner, boolean geoLocationEnabled) {
//        if (hasPermission(owner))
            this.geoLocationEnabled = geoLocationEnabled;
    }

//    public boolean hasPermission(User owner) {
//        boolean permission = this.get.equals(owner.getId());
//        if (permission)
//            return true;
//        else
//            Log.d(TAG, "Error: Tried to set property without permission");
//        return false;
//    }

    public boolean containsString(String string) {
        return this.region.contains(string) ||
                this.description.contains(string);
    }

    @Override
    public String toString() {
        return "ExperimentInfo{" +
//                "owner=" + ownerId +
                ", description='" + description + '\'' +
                ", region='" + region + '\'' +
                ", minTrials=" + minTrials +
                ", geoLocationEnabled=" + geoLocationEnabled +
                '}';
    }


}