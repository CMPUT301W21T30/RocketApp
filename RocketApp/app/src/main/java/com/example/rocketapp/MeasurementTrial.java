package com.example.rocketapp;
import static java.lang.Math.ceil;

/**
 * Class for Trials of 'Measurement' type.
 * The trials are either True(Success) or False(Fail)
 */
public class MeasurementTrial extends Trial implements Comparable<MeasurementTrial>{
    public static final String TYPE = "Measurement";

    private float measurement;

    /**
     * Constructor for MeasurementTrial initialized with 0 if no value is passed
     */
    public MeasurementTrial() {
        measurement = 0;
    }

    /**
     * Constructor for MeasurementTrial where a value for count is passed.
     * @param value
     *          value is set as the value of this trial.
     */
    public MeasurementTrial(float value){
        measurement = value;
    }

    /**
     * @param trial
     *          trial parameter is a different object of CountTrial class which gets compared to this trial based on their value
     * @return the difference in (this object's trial value - passed object's trial value) rounded up to nearest integer
     */
    @Override
    public int compareTo(MeasurementTrial trial) {
        float compareCount = ((MeasurementTrial)trial).getMeasurement();
        return (int) ceil((this.getMeasurement() - compareCount));
    }

    /**
     * @return the type of trial, objects of this class will return "Measurement"
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * @return the value of this trial
     */
    public float getMeasurement(){
        return measurement;
    }
}