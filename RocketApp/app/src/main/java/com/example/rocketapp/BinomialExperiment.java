package com.example.rocketapp;
import java.util.ArrayList;

import com.google.firebase.firestore.Exclude;

public class BinomialExperiment extends Experiment {
    public static String TYPE = "Binomial";

    public BinomialExperiment() {
        //TODO
    }

    public BinomialExperiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(description, region, minTrials, geoLocationEnabled);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Exclude
    @Override
    public float getMedian() {
        //TODO
        return 0;
    }

    @Exclude
    @Override
    public float getMean() {
        ArrayList<BinomialTrial> trials = getTrials();
        int length = trials.size();
        int success = 0;
        int failure = 0;
        for(int i=0; i<length; i++){
            if(trials.get(i).isValue()){
                success = success+1;
            }
            else{
                failure = failure+1;
            }
        }
        return (float) ((success/failure)*1.0);
    }

    @Exclude
    @Override
    public float getStdDev() {
        //TODO
        return 0;
    }

    @Exclude
    @Override
    public float getQuartiles() {
        //TODO
        return 0;
    }

    @Exclude
    @Override
    public ArrayList<BinomialTrial> getTrials(){
        return (ArrayList<BinomialTrial>) trialsArrayList;
    }

}