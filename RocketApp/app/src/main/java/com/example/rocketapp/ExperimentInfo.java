package com.example.rocketapp;

/**
 * Information for experiment
 */
public class ExperimentInfo {
//    private DataManager.ID ownerId;
    private String description;         //Details regarding what the experiment is and how to perform
    private String region;              //Region where the experiment is performed.
    private int minTrials;              //The minimum number of trials required to derive a conclusion.
    private boolean geoLocationEnabled;     // True if the trial requires user to submit their geoLocation, False otherwise.

    public ExperimentInfo() { }

    public ExperimentInfo(String description, String region, int minTrials, boolean geoLocationEnabled) {
        this.description = description;
        this.region = region;
        this.minTrials = minTrials;
        this.geoLocationEnabled = geoLocationEnabled;
    }

//    public DataManager.FirestoreObject.ID getOwner() { return ownerId; }

    /**
     * getter for experiment description
     * @return description of experiment        - String
     */
    public String getDescription() {
        return description;
    }

    /**
     * getter for region of experiment
     * @return region           - String
     */
    public String getRegion() {
        return region;
    }

    /**
     * getter for minimum number of trials required
     * @return minimum number of trials        - Int
     */
    public int getMinTrials() {
        return minTrials;
    }

    /**
     * getter for whether geoLocation needs to be enabled or not
     * @return boolean
     */
    public boolean isGeoLocationEnabled() {
        return geoLocationEnabled;
    }

//    public void setOwner(DataManager.ID id) {
//        ownerId = id;
//    }

    /**
     * setter for description of experiment
     * @param owner
     *          object of User class, the creator of this experiment
     * @param description
     *          String description
     */
    public void setDescription(User owner, String description) {
//        if (hasPermission(owner))
            this.description = description;
    }

    /**
     * setter for region of experiment
     * @param owner
     *          object of User class, the creator of this experiment
     * @param region
     *          String region
     */
    public void setRegion(User owner, String region) {
//        if (hasPermission(owner))
            this.region = region;
    }

    /**
     * setter for minimum trials required for experiment to end
     * @param owner
     *          object of User class, the creator of this experiment
     * @param minTrials
     *          integer minTrials
     */
    public void setMinTrials(User owner, int minTrials) {
//        if (hasPermission(owner))
            this.minTrials = minTrials;
    }

    /**
     * setter for geo location enability of experiment
     * @param owner
     *          object of User class, the creator of this experiment
     * @param geoLocationEnabled
     *          set True if geoLocation is to be enabled, false otherwise
     */
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

    /**
     * Used for searching purposes
     * @param string
     *         search string passed
     * @return
     *          if search query is present in either description or region, return True
     */
    public boolean containsString(String string) {
        return this.region.contains(string) ||
                this.description.contains(string);
    }

    /**
     * Display function for Experiment
     * @return String displaying experiment info
     */
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