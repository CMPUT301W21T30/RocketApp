package com.example.rocketapp.model.trials;

import com.example.rocketapp.controller.FirestoreNestableDocument;
import com.google.firebase.firestore.Exclude;
import static java.lang.Math.ceil;

/**
 * Abstract class Trial
 * Classes derived from this are - "BinomialTrial", "CountTrial", "IntCountTrial" and "MeasurementTrial"
 * Posts the trial information to relevant experiments inside Firestore database
 */
public abstract class Trial extends FirestoreNestableDocument implements Comparable<Trial> {
    private Boolean isIgnored = false;
    private Geolocation location;
    protected Float value;

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public Trial() { }

    /**
     * getter for type of experiment
     * @return type of experiment - String
     */
    public abstract String getType();

    /**
     * @return gets the float representation of the value for this trial
     */
    public Float getValue() {
        return value;
    }

    /**
     * @return string representation of trial value
     */
    @Exclude
    public String getValueString(){
        return String.valueOf(value);
    }

    /**
     * @return whether this trial is being ignored in statistics calculations
     */
    public Boolean getIgnored() {
        return isIgnored;
    }

    /**
     * @param ignore whether this trial should be ignored for statistics calculations
     */
    public void setIgnored(Boolean ignore) {
        isIgnored = ignore;
    }

    /**
     * @return the location of this trial
     */
    public Geolocation getLocation() {
        return location;
    }

    /**
     * Location sets the location of this trial
     */
    public void setLocation(Geolocation location) {
        this.location = location;
    }

    /**
     * @param trial
     *          trial parameter is a different object of CountTrial class which gets compared to this trial based on their value
     * @return the difference in (this object's trial value - passed object's trial value)
     */
    @Override
    public int compareTo(Trial trial) {
        return (int) ceil(value - trial.getValue());
    }
}
