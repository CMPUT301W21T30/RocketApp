package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class CountsTrial extends Trial {

    private int numberCounted;

    public CountTrial() {
        numberCounted = 0;
    }

    public void addCount(){
        numberCounted = numberCounted + 1;
    }

}
