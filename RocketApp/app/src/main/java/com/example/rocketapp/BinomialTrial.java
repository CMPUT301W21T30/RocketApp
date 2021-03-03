package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class BinomialTrial extends Trial {
    public static final String TYPE = "Binomial";

    private int successNum;
    private int failureNum;
    private float successRate;

    public BinomialTrial(String description) {
        super(description);
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

    @Exclude
    public float getSuccessRate(){
        if(successNum+failureNum==0){
            successRate = 0;
        }
        else{
            successRate = (float)successNum/failureNum;
        }
        return successRate;
    }
}