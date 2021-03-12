package com.example.rocketapp;

import static java.lang.Math.ceil;

public class MeasurementTrial extends Trial implements Comparable<MeasurementTrial>{
    public static final String TYPE = "Measurement";

    private float measurement;

    public MeasurementTrial(String description) {
        super(description);
        measurement = 0;
    }
    public MeasurementTrial(String description, float value){
        super(description);
        measurement = value;
    }

    @Override
    public int compareTo(MeasurementTrial trial) {
        float compareCount = ((MeasurementTrial)trial).getMeasurement();
        return (int) ceil((this.getMeasurement() - compareCount));
    }

    public MeasurementTrial(float measure){
        measurement = measure;
    }

    public void addMeasurement(float measure) {
        measurement = measure;
    }

    public String getType() {
        return TYPE;
    }

    public float getMeasurement(){
        return measurement;
    }
}