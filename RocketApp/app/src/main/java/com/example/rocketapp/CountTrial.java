package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class CountTrial extends Trial {
    public static final String TYPE = "Count";

    private int numberCounted;

    public CountTrial(String description) {
        super(description);
        numberCounted = 0;
    }

    public CountTrial(String description, int number){
        super(description);
        numberCounted = number;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public void addCount(){
        numberCounted = numberCounted + 1;
    }

    public int getCount(){
        return numberCounted;
    }
}