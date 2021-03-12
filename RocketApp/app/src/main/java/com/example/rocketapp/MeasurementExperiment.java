package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import java.util.Collections;

import com.google.firebase.firestore.Exclude;

import static java.lang.Math.sqrt;

public class MeasurementExperiment extends Experiment {
    public static String TYPE = "Measurement";

    public MeasurementExperiment() {
        //TODO
    }

    public MeasurementExperiment(String name, String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(name, description, region, minTrials, geoLocationEnabled);
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
        ArrayList<MeasurementTrial> trials = getTrials();
        float mean = getMean();
        float squareSum = 0;
        float meanDif = 0;
        for(int i = 0; i<trials.size(); i++){
            meanDif = (trials.get(i).getMeasurement() - mean);
            squareSum = squareSum + (meanDif * meanDif);
        }
        final double stdDev = sqrt(squareSum / trials.size());
        return (float) stdDev;
    }

    @Exclude
    @Override
    public float getTopQuartile() {
        float quart;
        ArrayList<MeasurementTrial> trials = getTrials();
        Collections.sort(trials);
        switch(trials.size()%4){
            case (0):
                quart = ( (float)(trials.get(( trials.size() * 3) / 4 - 1).getMeasurement() + trials.get((trials.size() * 3) / 4 ).getMeasurement()) )/ 2;
                return quart;
            case (1):
                quart =  ((float) (trials.get(((trials.size() - 1) * 3) / 4 ).getMeasurement() + trials.get(((trials.size() - 1) * 3) / 4 + 1).getMeasurement())) / 2;
                return quart;
            case (2):
                quart = (float)(trials.get(((trials.size() - 2)* 3) / 4 + 1).getMeasurement());
                return quart;
            default:
                quart = (float)(trials.get(((trials.size() - 3)* 3) / 4 + 2).getMeasurement());
                return quart;
        }
    }

    @Exclude
    @Override
    public float getBottomQuartile() {
        float quart;
        ArrayList<MeasurementTrial> trials = getTrials();
        Collections.sort(trials);
        switch (trials.size()%4){
            case (0):
                quart = ( (float)(trials.get( trials.size()  / 4 - 1).getMeasurement() + trials.get(trials.size() / 4 ).getMeasurement()) )/ 2;
                return quart;
            case (1):
                quart = ( (float)(trials.get( (trials.size() - 1 )/ 4 - 1).getMeasurement() + trials.get((trials.size() - 1 )/ 4 ).getMeasurement()) )/ 2;
                return quart;
            case (2):
                quart = (float)(trials.get((trials.size() - 2) / 4 ).getMeasurement());
                return quart;
            default:
                quart = (float)(trials.get((trials.size() - 3) / 4 ).getMeasurement());
                return quart;
        }
    }

    @Exclude
    @Override
    public ArrayList<MeasurementTrial> getTrials(){
        return (ArrayList<MeasurementTrial>) trialsArrayList;
    }

}