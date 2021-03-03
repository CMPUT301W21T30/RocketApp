package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class CountTrial extends Trial {
    public static final String TYPE = "Count";

    private int numberCounted;

    public CountTrial() {
        numberCounted = 0;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public void addCount(){
        numberCounted = numberCounted + 1;
    }

    @Exclude
    public int getCount(){
        return numberCounted;
    }
}