package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'IntCount' type.
 * The trials are either True(Success) or False(Fail)
 */
public class IntCountTrial extends Trial implements Comparable< IntCountTrial >{
    public static final String TYPE = "IntCount";

    private int posCount;

    /**
     * Constructor for IntCountTrial initialized with 0 if no value is passed
     */
    public IntCountTrial() {
        posCount = 0;
    }

    /**
     * Constructor for IntCountTrial where a value for count is passed.
     * @param value
     *          value is set as the value of this trial.
     */
    public IntCountTrial(int value) {
        posCount = value;
    }

    /**
     * @param trial
     *          trial parameter is a different object of CountTrial class which gets compared to this trial based on their value
     * @return the difference in (this object's trial value - passed object's trial value)
     */
    @Override
    public int compareTo(IntCountTrial trial) {
        int compareCount = ((IntCountTrial)trial).getPCount();
        return this.getPCount() - compareCount;
    }

    /**
     * @return the type of trial, objects of this class will return "IntCount"
     */
    public String getType() {
        return TYPE;
    }

    /**
     * Increment a value already present in a trial by 1
     */
    public void addPCount(){
        posCount = posCount + 1;
    }

    /**
     * @return the value of this trial
     */
    public int getPCount(){
        return posCount;
    }
}