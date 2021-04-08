package com.example.rocketapp.model.trials;

import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'Binomial' type.
 * The trials are either True(Success) or False(Fail)
 */
public class BinomialTrial extends Trial {
    public static final String TYPE = "Binomial";

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public BinomialTrial() {}

    /**
     * @param success True or False depending on the trial Passing or Failing. The interpretation of a Pass/Fail should be described in the experiment description.
     */
    public BinomialTrial(boolean success) {
        value = success ? 1.0f : 0.0f;
    }

    /**
     * @return the type of trial, in this case it would be "Binomial".   - String
     */
    @Override
    public String getType() {
        return TYPE;
    }


    /**
     * @return string representation of trial value
     */
    @Exclude
    @Override
    public String getValueString() {
        return value == 1 ? "true" : "false";
    }

}