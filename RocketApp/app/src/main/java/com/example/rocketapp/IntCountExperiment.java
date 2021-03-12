package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import com.google.firebase.firestore.Exclude;

public class IntCountExperiment extends Experiment {
    public static String TYPE = "IntCount";

    public IntCountExperiment() {
        //TODO
    }

    public IntCountExperiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(description, region, minTrials, geoLocationEnabled);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Exclude
    public float getMedian(){
        ArrayList<IntCountTrial> trials = getTrials();
        int length = trials.size();
        float median;
        if(length%2==0) {
            median = (trials.get((length / 2) + 1).getPCount() + trials.get((length / 2) / 2).getPCount())/2;
        }
        else {
            median = (trials.get((length / 2)+1).getPCount());
        }
        return median;
    }

    @Exclude
    @Override
    public float getMean() {
        ArrayList<IntCountTrial> trials = getTrials();
        int sum = 0;
        for(int i = 0; i<trials.size(); i++){
            sum = sum + trials.get(i).getPCount();
        }
        return (float) ((sum/trials.size())*1.0);
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
    public ArrayList<IntCountTrial> getTrials(){
        return (ArrayList<IntCountTrial>) trialsArrayList;
    }

}