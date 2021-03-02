package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import com.google.firebase.firestore.Exclude;

public class IntCountExperiment extends Experiment {

    private ArrayList<IntCountTrial> trials = new ArrayList<>();

    public IntCountExperiment() {
        //TODO
    }

    @Override
    public String getType() {
        return "IntCount";
    }

    @Exclude
    public float getMedian(){
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

    @Override
    public float getMean() {
        int sum = 0;
        for(int i = 0; i<trials.size(); i++){
            sum = sum + trials.get(i).getPCount();
        }
        return (float) ((sum/trials.size())*1.0);
    }

    @Override
    public float getStdDev() {
        //TODO
        return 0;
    }

    @Override
    public float getQuartiles() {
        //TODO
        return 0;
    }

}
