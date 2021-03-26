package com.example.rocketapp;

import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.experiments.Experiment;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;

public class BinomialExperimentUnitTest {

    public Experiment createMockExperiment() {
        Experiment experiment = new BinomialExperiment();
        ArrayList<BinomialTrial> trials = new ArrayList<>();
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(false));
        experiment.setTrials(trials);
        return experiment;
    }

    @Test
    public void checkMean() {
        Experiment experiment = createMockExperiment();
        assertEquals(experiment.getMean(), 0.75, 0.1);
    }

    @Test
    public void checkMedian() {
        Experiment experiment = createMockExperiment();
        assertEquals(experiment.getMedian(), 1, 0.1);
    }

    @Test
    public void checkStdDev() {
        Experiment experiment = createMockExperiment();
        assertEquals(0.75, experiment.getStdDev(),0.1);
    }
}
