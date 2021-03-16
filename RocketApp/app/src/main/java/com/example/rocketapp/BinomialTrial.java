package com.example.rocketapp;

/**
 * Class for Trials of 'Binomial' type.
 * The trials are either True(Success) or False(Fail)
 */
public class BinomialTrial extends Trial {
    public static final String TYPE = "Binomial";

    private boolean value;

    /**
     *
     * If an object of this class is created without passing any value, it is assumed to be False.
     *
     */
    public BinomialTrial() {
        value = false;
    }

    /**
     *
     * @param success
     *          True or False depending on the trial Passing or Failing. The interpretation of a Pass/Fail should be described in the experiment description.
     */
    public BinomialTrial(boolean success) {
        setValue(success);
    }

    /**
     *
     * @return the type of trial, in this case it would be "Binomial".   - String
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     *
     * @return the outcome of trial, True if pass, False if fail.    - Boolean
     */
    public boolean isValue() {
        return value;
    }

    /**
     *
     * @param value
     *          Setter for the value of this trial.
     */
    public void setValue(boolean value) {
        this.value = value;
    }
}