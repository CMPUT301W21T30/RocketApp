package com.example.rocketapp;

import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.experiments.IntCountExperiment;
import com.example.rocketapp.model.trials.IntCountTrial;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class IntCountExperimentUnitTest {

    public Experiment createMockExperiment() {
        IntCountExperiment experiment = new IntCountExperiment();
        ArrayList<IntCountTrial> trials = new ArrayList<>();
        trials.add(new IntCountTrial(20));
        trials.add(new IntCountTrial(10));
        trials.add(new IntCountTrial(15));
        trials.add(new IntCountTrial(15));

        experiment.setTrials(trials);
        return experiment;
    }

    @Test
    public void checkMean() {
        Experiment<?> experiment = createMockExperiment();
        assertEquals(15.0, experiment.getMean(), 0.1);
    }

    @Test
    public void checkMedian() {
        Experiment<?> experiment = createMockExperiment();
        assertEquals(15.0, experiment.getMedian(),0.1);
    }

    @Test
    public void checkStdDev() {
        Experiment<?> experiment = createMockExperiment();
        assertEquals(3.54, experiment.getStdDev(),0.1);
    }

    @Test
    public void checkTopQuartile() {
        Experiment<?> experiment = createMockExperiment();
        assertEquals(17.5, experiment.getTopQuartile(),0.1);
    }

    @Test
    public void checkBottomQuartile() {
        Experiment<?> experiment = createMockExperiment();
        assertEquals(12.5, experiment.getBottomQuartile(),0.1);
    }

}
