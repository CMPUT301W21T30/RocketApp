package com.example.rocketapp.model.experiments;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.example.rocketapp.model.trials.IntCountTrial;
import com.google.firebase.firestore.Exclude;

import static java.lang.Math.sqrt;

/**
 * Class for experiments of type "IntCount".
 * Inherits from abstract class Experiment.
 */
public class IntCountExperiment extends Experiment {
    public static final String TYPE = "IntCount";

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public IntCountExperiment() {}

    /**Constructor for IntCountExperiment
     * @param description   - String
     *          Details regarding what the experiment is and how to perform
     * @param region    - String
     *          Region where the experiment is performed.
     * @param minTrials     - Int
     *          The minimum number of trials required to derive a conclusion.
     * @param geoLocationEnabled    - Boolean
     *          True if the trial requires user to submit their geoLocation, False otherwise.
     */
    public IntCountExperiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(description, region, minTrials, geoLocationEnabled);
    }

    /**
     * @return Type of experiment. Objects of this class return "IntCount" - String
     */
    @Override
    public String getType() {
        return TYPE;
    }

}