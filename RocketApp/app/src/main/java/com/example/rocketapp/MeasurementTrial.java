package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class MeasurementTrial extends Trial {

    private float measurement;

    public MeasurementTrial() {
        measurement = 0
    }

    public void addMeasurement(){
        measurement = measurement + 1;
    }


}
