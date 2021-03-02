package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class CountsTrial extends Trial {

    private int numberCounted;

    public CountsTrial() {
        numberCounted = 0;
    }

    @Override
    public String getType() {
        return "Counts";
    }

    public void addCount(){
        numberCounted = numberCounted + 1;
    }

    @Exclude
    public int getCount(){
        return numberCounted;
    }
}
