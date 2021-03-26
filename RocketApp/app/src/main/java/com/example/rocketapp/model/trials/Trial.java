package com.example.rocketapp.model.trials;

import com.example.rocketapp.controller.DataManager;
import com.google.firebase.firestore.Exclude;

/**
 * Abstract class Trial
 * Classes derived from this are - "BinomialTrial", "CountTrial", "IntCountTrial" and "MeasurementTrial"
 * Posts the trial information to relevant experiments inside Firestore database
 */
public abstract class Trial extends DataManager.FirestoreNestableDocument implements DataManager.Type {

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public Trial() { }

    /**
     * getter for type of experiment
     * @return type of experiment - String
     */
    @Override
    public abstract String getType();

    /**
     * @return string representation of trial value
     */
    @Exclude
    public abstract String getValueString();
}
