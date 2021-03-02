package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class IntCountTrial extends Trial {

    private int posCount;

    public IntCountTrial() {
        posCount = 0;
    }

    public void addPCount(){
        posCount = posCount + 1;
    }

    public int getPCount(){
        return posCount;
    }
}
