package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class IntCountTrial extends Trial {

    private int posCount;

    public IntCountTrial() {
        posCount = 0;
    }

    public String getType() {
        return "IntCount";
    }

    public void addPCount(){
        posCount = posCount + 1;
    }

    @Exclude
    public int getPCount(){
        return posCount;
    }
}
