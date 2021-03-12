package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import java.util.Collections;

import com.google.firebase.firestore.Exclude;

import static java.lang.Math.sqrt;

public class IntCountExperiment extends Experiment {
    public static String TYPE = "IntCount";

    public IntCountExperiment() {
        //TODO
    }

    public IntCountExperiment(String name, String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(name, description, region, minTrials, geoLocationEnabled);
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
        ArrayList<IntCountTrial> trials = getTrials();
        float mean = getMean();
        float squareSum = 0;
        float meanDif = 0;
        for(int i = 0; i<trials.size(); i++){
            meanDif = (trials.get(i).getPCount() - mean);
            squareSum = squareSum + (meanDif * meanDif);
        }
        final double stdDev = sqrt(squareSum / trials.size());
        return (float) stdDev;
    }

    @Exclude
    @Override
    public float getTopQuartile() {
        float quart;
        ArrayList<IntCountTrial> trials = getTrials();
        Collections.sort(trials);
        switch(trials.size()%4){
            case (0):
                quart = ( (float)(trials.get(( trials.size() * 3) / 4 - 1).getPCount() + trials.get((trials.size() * 3) / 4 ).getPCount()) )/ 2;
                return quart;
            case (1):
                quart =  ((float) (trials.get(((trials.size() - 1) * 3) / 4 ).getPCount() + trials.get(((trials.size() - 1) * 3) / 4 + 1).getPCount())) / 2;
                return quart;
            case (2):
                quart = (float)(trials.get(((trials.size() - 2)* 3) / 4 + 1).getPCount());
                return quart;
            default:
                quart = (float)(trials.get(((trials.size() - 3)* 3) / 4 + 2).getPCount());
                return quart;
        }
    }

    @Exclude
    @Override
    public float getBottomQuartile() {
        float quart;
        ArrayList<IntCountTrial> trials = getTrials();
        Collections.sort(trials);
        switch (trials.size()%4){
            case (0):
                quart = ( (float)(trials.get( trials.size()  / 4 - 1).getPCount() + trials.get(trials.size() / 4 ).getPCount()) )/ 2;
                return quart;
            case (1):
                quart = ( (float)(trials.get( (trials.size() - 1 )/ 4 - 1).getPCount() + trials.get((trials.size() - 1 )/ 4 ).getPCount()) )/ 2;
                return quart;
            case (2):
                quart = (float)(trials.get((trials.size() - 2) / 4 ).getPCount());
                return quart;
            default:
                quart = (float)(trials.get((trials.size() - 3) / 4 ).getPCount());
                return quart;
        }
    }

    @Exclude
    @Override
    public ArrayList<IntCountTrial> getTrials(){
        return (ArrayList<IntCountTrial>) trialsArrayList;
    }

}