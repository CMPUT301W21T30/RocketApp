package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'IntCount' type.
 * The trials are either True(Success) or False(Fail)
 */
public class IntCountTrial extends Trial implements Comparable<IntCountTrial> {
    public static final String TYPE = "IntCount";
    private double latitude;
    private double longitude;

    private int count;

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public IntCountTrial() {}

    /**
     * Constructor for IntCountTrial where a value for count is passed.
     * @param value
     *          value is set as the value of this trial.
     */
    public IntCountTrial(int value) {
        setPCount(value);
    }

    /**
     * @param trial
     *          trial parameter is a different object of CountTrial class which gets compared to this trial based on their value
     * @return the difference in (this object's trial value - passed object's trial value)
     */
    @Override
    public int compareTo(IntCountTrial trial) {
        int compareCount = trial.getPCount();
        return this.getPCount() - compareCount;
    }

    /**
     * @return the type of trial, objects of this class will return "IntCount"
     */
    public String getType() {
        return TYPE;
    }

    /**
     * @return the value of this trial
     */
    public int getPCount(){
        return count;
    }

    /**
     * @return string representation of trial value
     */
    @Exclude
    @Override
    public String getValueString(){return Integer.toString(count); }

    /**
     * Setter for pos
     * @param value
     *          Value to initialize pos with
     */
    public void setPCount(int value){
        count = value;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}