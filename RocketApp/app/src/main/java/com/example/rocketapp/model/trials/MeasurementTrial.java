package com.example.rocketapp.model.trials;


/**
 * Class for Trials of 'Measurement' type.
 * The trials are either True(Success) or False(Fail)
 */
public class MeasurementTrial extends Trial {
    public static final String TYPE = "Measurement";

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public MeasurementTrial() { }

    /**
     * Constructor for MeasurementTrial where a value for count is passed.
     * @param value
     *          value is set as the value of this trial.
     */
    public MeasurementTrial(float value){
        this.value = value;
    }

    /**
     * @return the type of trial, objects of this class will return "Measurement"
     */
    @Override
    public String getType() {
        return TYPE;
    }

}