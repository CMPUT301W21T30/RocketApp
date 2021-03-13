package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class IntCountTrial extends Trial implements Comparable< IntCountTrial >{
    public static final String TYPE = "IntCount";

    private int posCount;

    public IntCountTrial(String description) {
        super(description);
        posCount = 0;
    }

    public IntCountTrial(String description, int value) {
        super(description);
        posCount = value;
    }

    @Override
    public int compareTo(IntCountTrial trial) {
        int compareCount = ((IntCountTrial)trial).getPCount();
        return this.getPCount() - compareCount;
    }

    public String getType() {
        return TYPE;
    }

    public void addPCount(){
        posCount = posCount + 1;
    }

    public int getPCount(){
        return posCount;
    }
}