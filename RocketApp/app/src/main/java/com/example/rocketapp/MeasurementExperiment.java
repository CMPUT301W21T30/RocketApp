package com.example.rocketapp;
import java.util.ArrayList;

import com.google.firebase.firestore.Exclude;

public class MeasurementExperiment extends Experiment {
    public static String TYPE = "Measurement";

    public MeasurementExperiment() {
        //TODO
    }

    public MeasurementExperiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(description, region, minTrials, geoLocationEnabled);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Exclude
    public float getMedian(){
        ArrayList<MeasurementTrial> trials = getTrials();
        int length = trials.size();
        float median;
        if(length%2==0) {
            median = (trials.get((length / 2) + 1).getMeasurement() + trials.get((length / 2) / 2).getMeasurement() / 2);
        } else {
            median = (trials.get((length / 2)+1).getMeasurement());
        }
        return median;
    }

    @Exclude
    @Override
    public float getMean() {
        ArrayList<MeasurementTrial> trials = getTrials();
        float sum = 0;
        for(int i = 0; i<trials.size(); i++){
            sum = sum + trials.get(i).getMeasurement();
        }
        final float mean = sum / trials.size();
        return mean;
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
    public ArrayList<MeasurementTrial> getTrials(){
        return (ArrayList<MeasurementTrial>) trialsArrayList;
    }

}