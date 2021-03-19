package com.example.rocketapp;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CountExperimentUnitTest {
    public Experiment createMockExperiment() {
        Experiment experiment = new CountExperiment();
        ArrayList<CountTrial> trials = new ArrayList<>();
        trials.add(new CountTrial(20));
        trials.add(new CountTrial(10));
        trials.add(new CountTrial(15));
        trials.add(new CountTrial(15));
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
