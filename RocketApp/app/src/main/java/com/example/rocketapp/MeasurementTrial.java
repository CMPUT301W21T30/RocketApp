package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class MeasurementTrial extends Trial {
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