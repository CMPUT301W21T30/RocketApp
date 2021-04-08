package com.example.rocketapp.model.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.example.rocketapp.model.trials.CountTrial;
import com.google.firebase.firestore.Exclude;

import static java.lang.Math.sqrt;

/**
 * Class for experiments of type "Count".
 * Inherits from abstract class Experiment.
 */
public class CountExperiment extends Experiment<CountTrial> {
    public static final String TYPE = "Count";

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public CountExperiment() {}

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
     * IMPORTANT NOTE: this method returns the total instead of the mean unlike the other classes, this is to simplify the code
     * Excluded from getting stored inside firestore.
     * Calculates the mean from all trials present in this experiment
     * @return the mean of experiment
     */
    @Exclude
    @Override
    public float getMean() {
        ArrayList<CountTrial> trials = getFilteredTrials();
        if (trials.size() == 0) {return 0;}
        int sum = 0;
        if(trials.size()==0){
            return 0;
        }
        for(int i = 0; i<trials.size(); i++){
            sum = sum + trials.get(i).getCount();
        }
        return ((float) sum);
    }

    /**
     * IMPORTANT NOTE: this method returns the total instead of the mean unlike the other classes, this is to simplify the code
     * Excluded from getting stored inside firestore.
     * Calculates the total from all trials present in this experiment up to a certain date
     * @return the total of experiment to given date
     */
    @Exclude
    @Override
    public float getMean(Date date) {
        ArrayList<CountTrial> trials = getFilteredTrials();
        if (trials.size() == 0) {return 0;}
        float sum = 0;
        if(trials.size()==0){
            return 0;
        }
        int trialCounter = 0;
        for(int i = 0; i<trials.size() ; i++){
            if(trials.get(i).getTimestamp().toDate().after(date)) {continue;}
            sum = sum + trials.get(i).getCount();
            trialCounter++;
        }
        return (float) sum;
    }

}