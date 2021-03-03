package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class BinomialTrial extends Trial {
    public static final String TYPE = "Binomial";

    private int successNum;
    private int failureNum;

    public BinomialTrial() {
        successNum = 0;
        failureNum = 0;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public void addSuccess(){
        successNum = successNum + 1;
    }

    public void addFailure(){
        failureNum = failureNum + 1;
    }

    @Exclude
    public int getSuccess(){
        return successNum;
    }

    @Exclude
    public int getFailure(){
        return failureNum;
    }
}