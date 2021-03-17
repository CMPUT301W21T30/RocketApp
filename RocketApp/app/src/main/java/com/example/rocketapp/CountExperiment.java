package com.example.rocketapp;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.lang.Integer;
import java.util.Collections;

import com.google.firebase.firestore.Exclude;

import static java.lang.Math.sqrt;

/**
 * Class for experiments of type "Count".
 * Inherits from abstract class Experiment.
 */
public class CountExperiment extends Experiment {
    public static final String TYPE = "Count";


    public CountExperiment() {
        //TODO
    }

    /**Constructor for CountExperiment
     * @param description   - String
     *          Details regarding what the experiment is and how to perform
     * @param region    - String
     *          Region where the experiment is performed.
     * @param minTrials     - Int
     *          The minimum number of trials required to derive a conclusion.
     * @param geoLocationEnabled    - Boolean
     *          True if the trial requires user to submit their geoLocation, False otherwise.
     */
    public CountExperiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(description, region, minTrials, geoLocationEnabled);
    }

    /**
     * @return Type of experiment. In this case it returns "Count" - String
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Excluded from getting stored inside firestore.
     * Calculates the median from all trials present in this experiment
     * @return the median of experiment
     */
    @Exclude
    public float getMedian(){
        ArrayList<CountTrial> trials = getTrials();
        if (trials.size() == 0) {return 0;}
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

    /**
     * Excluded from getting stored inside firestore.
     * Calculates the mean from all trials present in this experiment
     * @return the mean of experiment
     */
    @Exclude
    @Override
    public float getMean() {
        ArrayList<CountTrial> trials = getTrials();
        if (trials.size() == 0) {return 0;}
        int sum = 0;
        for(int i = 0; i<trials.size(); i++){
            sum = sum + trials.get(i).getCount();
        }
        return (float) ((sum/trials.size())*1.0);
    }

    /**
     * Excluded from getting stored inside firestore.
     * Calculates the standard deviation based on normal distribution from all trials present in this experiment
     * @return the standard deviation of experiment
     */
    @Exclude
    @Override
    public float getStdDev() {
        ArrayList<CountTrial> trials = getTrials();
        if (trials.size() == 0) {return 0;}
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

    /**
     * Excluded from getting stored inside firestore.
     * Calculates the 75th percentile from all trials present in this experiment
     * @return the Q3 of experiment
     */
    @Exclude
    @Override
    public float getTopQuartile() {
        float quart;
        ArrayList<CountTrial> trials = getTrials();
        if (trials.size() == 0) {return 0;}
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

    /**
     * Excluded from getting stored inside firestore.
     * Calculates the 25th percentile from all trials present in this experiment
     * @return Q1 of this experiment
     */
    @Exclude
    @Override
    public float getBottomQuartile() {
        float quart;
        ArrayList<CountTrial> trials = getTrials();
        if (trials.size() == 0) {return 0;}
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

    /**
     * @return All the trials in this experiment in the form of an Array List, indexed such as the earliest submitted trial is at 0th position.
     */
    @Exclude
    @Override
    public ArrayList<CountTrial> getTrials(){
        return (ArrayList<CountTrial>) trialsArrayList;
    }

}