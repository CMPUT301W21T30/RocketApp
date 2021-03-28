package com.example.rocketapp.model.trials;

import com.example.rocketapp.controller.FirestoreNestableDocument;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

/**
 * Abstract class Trial
 * Classes derived from this are - "BinomialTrial", "CountTrial", "IntCountTrial" and "MeasurementTrial"
 * Posts the trial information to relevant experiments inside Firestore database
 */
public abstract class Trial extends FirestoreNestableDocument {
    private Boolean isIgnored = false;
    private Geolocation location;

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public Trial() { }

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
     * getter for type of experiment
     * @return type of experiment - String
     */
    public abstract String getType();

    /**
     * @return string representation of trial value
     */
    @Exclude
    public abstract String getValueString();

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
}
