package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class CountsTrial extends Trial {

    private int numberCounted;

    public CountsTrial() {
        numberCounted = 0;
    }

    public void addCount(){
        numberCounted = numberCounted + 1;
    }

    public int getCount(){
        return numberCounted;
    }
}
