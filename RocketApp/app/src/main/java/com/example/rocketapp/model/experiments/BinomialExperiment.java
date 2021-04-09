package com.example.rocketapp.model.experiments;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import com.example.rocketapp.model.trials.BinomialTrial;
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

}