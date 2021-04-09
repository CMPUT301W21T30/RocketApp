package com.example.rocketapp.model.trials;

import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'IntCount' type.
 * The trials are either True(Success) or False(Fail)
 */
public class IntCountTrial extends Trial  {
    public static final String TYPE = "IntCount";

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
        this.value = (float) value;
    }

    /**
     * @return the type of trial, objects of this class will return "IntCount"
     */
    public String getType() {
        return TYPE;
    }


    /**
     * @return string containing integer value for this trial
     */
    @Exclude
    @Override
    public String getValueString() {
        return String.valueOf(value.intValue());
    }

}