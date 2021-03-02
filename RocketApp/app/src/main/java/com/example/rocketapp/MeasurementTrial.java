package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class MeasurementTrial extends Trial {

    private float measurement;

    public MeasurementTrial() {
        measurement = 0;
    }
    public MeasurementTrial(float measure){
        measurement = measure;
    }

    public void addMeasurement(float measure) {
        measurement = measure;
    }
    public String getType() {
        return "Measurement";
    }


    @Exclude
    public float getMeasurement(){
        return measurement;
    }
}
