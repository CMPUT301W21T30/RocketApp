package com.example.rocketapp;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.lang.Integer;
import java.util.Collections;

import com.google.firebase.firestore.Exclude;

import static java.lang.Math.sqrt;

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
        ArrayList<CountTrial> trials = getTrials();
        float mean = getMean();
        float squareSum = 0;
        float meanDif = 0;
        for(int i = 0; i<trials.size(); i++){
            meanDif = (trials.get(i).getCount() - mean);
            squareSum = squareSum + (meanDif * meanDif);
        }
        final double stdDev = sqrt(squareSum / trials.size());
        return (float) stdDev;
    }

    @Exclude
    @Override
    public float getTopQuartile() {
        float quart;
        ArrayList<CountTrial> trials = getTrials();
        Collections.sort(trials);
        switch(trials.size()%4){
            case (0):
                quart = ( (float)(trials.get(( trials.size() * 3) / 4 - 1).getCount() + trials.get((trials.size() * 3) / 4 ).getCount()) )/ 2;
                return quart;
            case (1):
                quart =  ((float) (trials.get(((trials.size() - 1) * 3) / 4 ).getCount() + trials.get(((trials.size() - 1) * 3) / 4 + 1).getCount())) / 2;
                return quart;
            case (2):
                quart = (float)(trials.get(((trials.size() - 2)* 3) / 4 + 1).getCount());
                return quart;
            default:
                quart = (float)(trials.get(((trials.size() - 3)* 3) / 4 + 2).getCount());
                return quart;
        }
    }

    @Exclude
    @Override
    public float getBottomQuartile() {
        float quart;
        ArrayList<CountTrial> trials = getTrials();
        Collections.sort(trials);
        switch (trials.size()%4){
            case (0):
                quart = ( (float)(trials.get( trials.size()  / 4 - 1).getCount() + trials.get(trials.size() / 4 ).getCount()) )/ 2;
                return quart;
            case (1):
                quart = ( (float)(trials.get( (trials.size() - 1 )/ 4 - 1).getCount() + trials.get((trials.size() - 1 )/ 4 ).getCount()) )/ 2;
                return quart;
            case (2):
                quart = (float)(trials.get((trials.size() - 2) / 4 ).getCount());
                return quart;
            default:
                quart = (float)(trials.get((trials.size() - 3) / 4 ).getCount());
                return quart;
        }
    }

    @Exclude
    @Override
    public ArrayList<CountTrial> getTrials(){
        return (ArrayList<CountTrial>) trialsArrayList;
    }

}