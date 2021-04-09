package com.example.rocketapp;

import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.experiments.MeasurementExperiment;
import com.example.rocketapp.model.trials.MeasurementTrial;
import com.example.rocketapp.model.trials.Trial;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MeasurementExperimentUnitTest {
    public Experiment createMockExperiment() {
        MeasurementExperiment experiment = new MeasurementExperiment();
        ArrayList<Trial> trials = new ArrayList<>();
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
    @Test
    public void checkTopQuartile() {
        Experiment experiment = createMockExperiment();
        assertEquals(17.5, experiment.getTopQuartile(),0.1);
    }

    @Test
    public void checkBottomQuartile() {
        Experiment experiment = createMockExperiment();
        assertEquals(12.5, experiment.getBottomQuartile(),0.1);
    }

}
