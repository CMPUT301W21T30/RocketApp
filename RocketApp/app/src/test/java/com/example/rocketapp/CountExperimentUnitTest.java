package com.example.rocketapp;

import com.example.rocketapp.model.experiments.CountExperiment;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CountExperimentUnitTest {
    private Experiment experiment;

    public CountExperimentUnitTest() {
        experiment = createMockExperiment();
    }

    public Experiment createMockExperiment() {

        CountExperiment experiment = new CountExperiment();
        ArrayList<Trial> trials = new ArrayList<>();
        trials.add(new CountTrial(20));
        trials.add(new CountTrial(10));
        trials.add(new CountTrial(15));
        trials.add(new CountTrial(15));
        experiment.setTrials(trials);
        return experiment;
    }

    @Test
    public void checkMean() {
        assertEquals(60.0, experiment.getMean(), 0.1);
    }

    @Test
    public void checkMedian() {
        assertEquals(15.0, experiment.getMedian(),0.1);
    }

    @Test
    public void checkTopQuartile() {
        assertEquals(17.5, experiment.getTopQuartile(),0.1);
    }

    @Test
    public void checkBottomQuartile() {
        assertEquals(12.5, experiment.getBottomQuartile(),0.1);
    }

}
