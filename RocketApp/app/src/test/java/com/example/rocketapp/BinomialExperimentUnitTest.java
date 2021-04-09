package com.example.rocketapp;

import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;

public class BinomialExperimentUnitTest {
    private Experiment experiment;
    public BinomialExperimentUnitTest() {
        experiment = createMockExperiment();
    }

    public Experiment createMockExperiment() {
        BinomialExperiment experiment = new BinomialExperiment();
        ArrayList<Trial> trials = new ArrayList<>();
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(false));
        experiment.setTrials(trials);
        return experiment;
    }

    @Test
    public void checkMean() {
        assertEquals(experiment.getMean(), 0.75, 0.1);
    }

    @Test
    public void checkMedian() {
        assertEquals(experiment.getMedian(), 1, 0.1);
    }

    @Test
    public void checkStdDev() {
        assertEquals(0.433, experiment.getStdDev(),0.1);
    }

    @Test
    public void checkTopQuartile() {
        assertEquals(1, experiment.getTopQuartile(),0.1);
    }

    @Test
    public void checkBottomQuartile() {
        assertEquals(0.5, experiment.getBottomQuartile(),0.1);
    }
}
