package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import com.google.firebase.firestore.Exclude;

public class BinomialExperiment extends Experiment {
    public static String TYPE = "Binomial";


    public BinomialExperiment() {
        //TODO
    }

    public BinomialExperiment(String name, String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(name, description, region, minTrials, geoLocationEnabled);
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
        return (float) ((success/(failure+success))*1.0);
    }

    @Exclude
    @Override
    public float getStdDev() {
        float mean = getMean();
        return (getTrials().size() * (1 - mean) * mean);
    }

    @Exclude
    @Override
    public float getTopQuartile() {
        if (getMean() < 0.25) {
            return 0;
        } else if (getMean() == 0.25) {
            return (float) 0.5;
        } else return 1;
    }

    @Exclude
    @Override
    public float getBottomQuartile() {
        if (getMean() < 0.75) {
            return 0;
        } else if (getMean() == 0.75) {
            return (float) 0.5;
        } else return 1;
    }

    @Exclude
    @Override
    public ArrayList<BinomialTrial> getTrials(){
        return (ArrayList<BinomialTrial>) trialsArrayList;
    }

}