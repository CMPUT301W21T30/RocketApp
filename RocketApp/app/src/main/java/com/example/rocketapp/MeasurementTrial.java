package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class MeasurementTrial extends Trial {

    private float measurement;

    public MeasurementTrial() {
        measurement = 0;
    }

    public void addMeasurement(float measure){
        measurement = measure;
    }

    public float getMeasurement(){
        return measurement;
    }
}
