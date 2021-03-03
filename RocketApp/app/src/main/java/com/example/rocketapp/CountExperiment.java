package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import com.google.firebase.firestore.Exclude;

public class CountExperiment extends Experiment {
    public static String TYPE = "Count";


    public CountExperiment() {
        //TODO
    }

    public CountExperiment(String name, String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(name, description, region, minTrials, geoLocationEnabled);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Exclude
    public float getMedian(){
        ArrayList<CountTrial> trials = getTrials();
        int length = trials.size();
        float median;
        if(length%2==0) {
            median = (trials.get((length / 2) + 1).getCount() + trials.get((length / 2) / 2).getCount())/2;
        }
        else {
            median = (trials.get((length / 2)+1).getCount());
        }
        return median;
    }

    @Exclude
    @Override
    public float getMean() {
        ArrayList<CountTrial> trials = getTrials();
        int sum = 0;
        for(int i = 0; i<trials.size(); i++){
            sum = sum + trials.get(i).getCount();
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
    public ArrayList<CountTrial> getTrials(){
        return (ArrayList<CountTrial>) trialsArrayList;
    }

}