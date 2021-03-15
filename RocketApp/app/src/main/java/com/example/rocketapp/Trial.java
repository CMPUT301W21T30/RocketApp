package com.example.rocketapp;

/**
 * Abstract class Trial
 * Classes derived from this are - "BinomialTrial", "CountTrial", "IntCountTrial" and "MeasurementTrial"
 * Posts the trial information to relevant experiments inside Firestore database
 */
public abstract class Trial extends DataManager.FirestoreNestableDocument implements DataManager.Type {

    public Trial() { }

    /**
     * getter for type of experiment
     * @return type of experiment - String
     */
    @Override
    public abstract String getType();
}
