package com.example.rocketapp;
import java.util.ArrayList;

import com.google.firebase.firestore.Exclude;

/**
    * Class for Experiments of 'Binomial' type.
    *Inherits from abstract class Experiment.
    */
public class BinomialExperiment extends Experiment {
    public static final String TYPE = "Binomial";     //Type of experiment

    public BinomialExperiment() {
        //TODO
    }

    /**Constructor for BinomialExperiment
     * @param description   - String
     *          Details regarding what the experiment is and how to perform
     * @param region    - String
     *          Region where the experiment is performed.
     * @param minTrials     - Int
     *          The minimum number of trials required to derive a conclusion.
     * @param geoLocationEnabled    - Boolean
     *          True if the trial requires user to submit their geoLocation, False otherwise.
     */
    public BinomialExperiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(description, region, minTrials, geoLocationEnabled);
    }

    /**
     *
     * @return Type of experiment. In this case it returns "Binomial" - String
     */
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

    /**
     *
     * @return Mean of all trials in this experiment. - Float
     */
    @Exclude
    @Override
    public float getMean() {
        ArrayList<BinomialTrial> trials = getTrials();
        if (trials.size() == 0) {return 0;}
        int length = trials.size();
        int success = 0;
        for(int i=0; i<length; i++){
            if(trials.get(i).isValue()) {
                success = success + 1;
            }
        }
        return  ((float) success)/((float)trials.size());
    }

    /**
     *
     * @return Standard Deviation of trials in this experiment. - Float
     */
    @Exclude
    @Override
    public float getStdDev() {
        float mean = getMean();
        return (getTrials().size() * (1 - mean) * mean);
    }

    /**
     *
     * @return Value at 75th percentile.
     */
    @Exclude
    @Override
    public float getTopQuartile() {
        if (getMean() < 0.25) {
            return 0;
        } else if (getMean() == 0.25) {
            return (float) 0.5;
        } else return 1;
    }

    /**
     *
     * @return Value at 25th percentile.
     */
    @Exclude
    @Override
    public float getBottomQuartile() {
        if (getMean() < 0.75) {
            return 0;
        } else if (getMean() == 0.75) {
            return (float) 0.5;
        } else return 1;
    }

    /**
     *
     * @return All the trials in this experiment in the form of an Array List, indexed such as the earliest submitted trial is at 0th position.
     */
    @Exclude
    @Override
    public ArrayList<BinomialTrial> getTrials(){
        return (ArrayList<BinomialTrial>) trialsArrayList;
    }

}