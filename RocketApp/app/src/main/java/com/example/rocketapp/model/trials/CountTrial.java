package com.example.rocketapp.model.trials;
import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'Count' type.
 * The trials are either True(Success) or False(Fail)
 */
public class CountTrial extends Trial {
    public static final String TYPE = "Count";

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public CountTrial() {}

    /**
     * Constructor for CountTrial where a value for count is passed.
     * @param count
     *          the count for this trial.
     */
    public CountTrial(int count){
        this.value = (float) count;
    }

    /**
     * @return the type of trial, objects of this class will return "Count"
     */
    @Override
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