package com.example.rocketapp;

import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.experiments.MeasurementExperiment;
import com.example.rocketapp.model.trials.MeasurementTrial;

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

    @Test
    public void checkMedian() {
        Experiment experiment = createMockExperiment();
        System.out.println(experiment.getMedian());
        assertEquals(15.0, experiment.getMedian(),0.1);
    }

    @Test
    public void checkStdDev() {
        Experiment experiment = createMockExperiment();
        assertEquals(3.54, experiment.getStdDev(),0.1);
    }

}
