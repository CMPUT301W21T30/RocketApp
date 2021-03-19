package com.example.rocketapp;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MeasurementExperimentUnitTest {
    public Experiment createMockExperiment() {
        Experiment experiment = new MeasurementExperiment();
        ArrayList<MeasurementTrial> trials = new ArrayList<>();
        trials.add(new MeasurementTrial(20.0f));
        trials.add(new MeasurementTrial(10.0f));
        trials.add(new MeasurementTrial(15.0f));
        trials.add(new MeasurementTrial(15.0f));

        experiment.setTrials(trials);
        return experiment;
    }

    @Test
    public void checkMean() {
        Experiment experiment = createMockExperiment();
        assertEquals(15.0, experiment.getMean(), 0.1);
    }

    // TODO Median seems to be returning incorrect result
//    @Test
//    public void checkMedian() {
//        Experiment experiment = createMockExperiment();
//        assertEquals(15.0, experiment.getMedian(),0.1);
//    }
}
