package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'Binomial' type.
 * The trials are either True(Success) or False(Fail)
 */
public class BinomialTrial extends Trial {
    public static final String TYPE = "Binomial";
    private boolean value;
    private double latitude;
    private double longitude;

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public BinomialTrial() {}

    /**
     * @param success True or False depending on the trial Passing or Failing. The interpretation of a Pass/Fail should be described in the experiment description.
     */
    public BinomialTrial(boolean success) {
        value = success;
    }

    /**
     * @return the type of trial, in this case it would be "Binomial".   - String
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * @return the outcome of trial, True if pass, False if fail.    - Boolean
     */
    public boolean isValue() {
        return value;
    }

    /**
     * @param value Setter for the value of this trial.
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    /**
     * @return string representation of trial value
     */
    @Exclude
    @Override
    public String getValueString() {
        return value ? "True" : "False";
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }
}