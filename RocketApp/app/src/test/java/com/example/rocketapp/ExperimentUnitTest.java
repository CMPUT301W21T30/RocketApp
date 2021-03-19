package com.example.rocketapp;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExperimentUnitTest {
    @Test
    public void checkExperimentIsValid() {
        Experiment experiment = new BinomialExperiment();
        assertFalse(experiment.isValid());
    }

    @Test
    public void checkExperimentOwnerIsValid() {
        Experiment experiment = new BinomialExperiment();
        assertFalse(experiment.ownerIsValid());
    }

    @Test
    public void checkExperimentGetTrials() {
        Experiment experiment = new BinomialExperiment();
        ArrayList<BinomialTrial> trials = new ArrayList<>();
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(false));
        experiment.setTrials(trials);
        assertEquals(4, experiment.getTrials().size());
    }

    @Test
    public void checkExperimentGetQuestions() {
        Experiment experiment = new BinomialExperiment();
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question("Question 1"));
        questions.add(new Question("Question 2"));
        questions.add(new Question("Question 3"));
        questions.add(new Question("Question 4"));
        experiment.setQuestions(questions);
        assertEquals(4, experiment.getTrials().size());
    }

}
