package com.example.rocketapp.model.experiments;
import java.util.ArrayList;
import java.util.Date;

import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.trials.CountTrial;
import com.google.firebase.firestore.Exclude;

/**
 * Class for Experiments of 'Binomial' type.
 *Inherits from abstract class Experiment.
 */
public class BinomialExperiment extends Experiment {
    public static final String TYPE = "Binomial";     //Type of experiment

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public BinomialExperiment() {}

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

    /**
     * Get Median of Binomial Experiment.
     * @return median of Binomial Experiment
     */
    @Exclude
    @Override
    public float getMedian() {
        if (getMean() < 0.5) {
            return 0;
        } else if (getMean() == 0.5) {
            return (float) 0.5;
        } else return 1;
    }

    /**
     *
     * @return Mean of all trials in this experiment. - Float
     */
    @Exclude
    @Override
    public float getMean() {
        ArrayList<BinomialTrial> trials = getFilteredTrials();
        if (trials.size() == 0) {return 0;}
        int length = trials.size();
        if(length==0){
            return 0;
        }
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
     * @return Mean of all trials in this experiment up to a given date. - Float
     */
    @Exclude
    @Override
    public float getMean(Date date) {
        ArrayList<BinomialTrial> trials = getFilteredTrials();
        if (trials.size() == 0) {return 0;}
        int length = trials.size();
        if(length==0){
            return 0;
        }
        int success = 0;
        int trialCounter = 0;
        for(int i=0; i<length; i++){
            if(trials.get(i).getTimestamp().toDate().compareTo(date) == 0 || trials.get(i).getTimestamp().toDate().compareTo(date) < 0) {
                System.out.println("Trial timestamp: " + trials.get(i).getTimestamp().toDate());/*For testing, checking if the issue with tie graphs is get mean of binomials, test shows it is now the issue */
                trialCounter++;
                if(trials.get(i).isValue()){
                    success++;
                }
            }
        }
        return  ((float) success)/((float)trialCounter);
    }

    /**
     *
     * @return Standard Deviation of trials in this experiment. - Float
     */
    @Exclude
    @Override
    public float getStdDev() {
        float mean = getMean();
        return (getFilteredTrials().size() * (1 - mean) * mean);
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
     * @return An ArrayList of all the trials that are not ignored by the owner
     */
    @Exclude
    @Override
    public ArrayList<BinomialTrial> getFilteredTrials(){
        ArrayList<BinomialTrial> trials = getTrials();
        ArrayList<BinomialTrial> filteredTrials = new ArrayList<BinomialTrial>();
        for(int i = 0; i <trials.size(); i++){
            if(! trials.get(i).getIgnored()){
                filteredTrials.add(trials.get(i));
            }
        }
        return filteredTrials;
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