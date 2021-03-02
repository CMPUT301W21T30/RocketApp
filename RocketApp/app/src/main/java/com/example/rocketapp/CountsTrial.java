package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class CountTrial extends Trial {

    private int numberCounted;

    public CountTrial() {
        numberCounted = 0;
    }

    public void addCount(){
        numberCounted = numberCounted + 1;
    }

    public int getCount(){
        return numberCounted;
    }
}
