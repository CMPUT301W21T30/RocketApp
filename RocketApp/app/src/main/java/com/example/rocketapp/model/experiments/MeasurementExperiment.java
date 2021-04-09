package com.example.rocketapp.model.experiments;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.example.rocketapp.model.trials.MeasurementTrial;
import com.google.firebase.firestore.Exclude;

import static java.lang.Math.sqrt;

/**
 * Class for experiments of type "Measurement".
 * Inherits from abstract class Experiment.
 */

public class MeasurementExperiment extends Experiment {
    public static final String TYPE = "Measurement";

    public MeasurementExperiment() {
        // Don't need anything here, but default constructor is necessary for firestore serialization
    }

    /**Constructor for MeasurementExperiment
     * @param description   - String
     *          Details regarding what the experiment is and how to perform
     * @param region    - String
     *          Region where the experiment is performed.
     * @param minTrials     - Int
     *          The minimum number of trials required to derive a conclusion.
     * @param geoLocationEnabled    - Boolean
     *          True if the trial requires user to submit their geoLocation, False otherwise.
     */
    public MeasurementExperiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        super(description, region, minTrials, geoLocationEnabled);
    }

    /**
     * @return Type of experiment. Objects of this class return "Measurement" - String
     */
    @Override
    public String getType() {
        return TYPE;
    }

}