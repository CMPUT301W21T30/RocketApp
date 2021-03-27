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

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public Trial() { }

    public Boolean getIgnored() {
        return isIgnored;
    }

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

    public abstract double getLatitude();

    public abstract double getLongitude();

    public abstract void setLatitude(double latitude);

    public abstract void setLongitude(double longitude);
}
