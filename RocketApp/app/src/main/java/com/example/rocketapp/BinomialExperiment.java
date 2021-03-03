package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import com.google.firebase.firestore.Exclude;

public class BinomialExperiment extends Experiment {
    public static String TYPE = "Binomial";

    private ArrayList<BinomialTrial> trials = new ArrayList<>();

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
    public float getMedian(){
        int length = trials.size();
        float median;
        if(length%2==0) {
            median = (trials.get((length / 2) + 1).getSuccessRate() + trials.get((length / 2) / 2).getSuccessRate())/2;
        }
        else {
            median = (trials.get((length / 2)+1).getSuccessRate());
        }
        return median;
    }

    @Exclude
    @Override
    public float getMean() {
        float sum = 0;
        for(int i = 0; i<trials.size(); i++){
            sum = sum + trials.get(i).getSuccessRate();
        }
        return (sum/trials.size());
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
        return trials;
    }

}