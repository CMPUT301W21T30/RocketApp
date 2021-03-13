package com.example.rocketapp;

public class IntCountTrial extends Trial {
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