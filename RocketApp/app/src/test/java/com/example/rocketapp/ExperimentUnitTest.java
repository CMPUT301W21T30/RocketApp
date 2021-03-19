package com.example.rocketapp;

import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExperimentUnitTest {

    private Experiment createMockExperiment() {
        Experiment experiment = new BinomialExperiment();
        ArrayList<BinomialTrial> trials = new ArrayList<>();
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(false));
        experiment.setTrials(trials);
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question("1st question"));
        questions.add(new Question("2nd question"));
        questions.add(new Question("3rd question"));
        questions.add(new Question("4th question"));
        experiment.setQuestions(questions);
        return experiment;
    }

    @Test
    public void checkExperimentIsValid() {
        Experiment experiment = createMockExperiment();
        assertFalse(experiment.isValid());
    }

    @Test
    public void checkExperimentOwnerIsValid() {
        Experiment experiment = createMockExperiment();
        assertFalse(experiment.ownerIsValid());
    }

    @Test
    public void checkExperimentGetTrials() {
        Experiment experiment = createMockExperiment();
        assertEquals(4, experiment.getTrials().size());
    }

    @Test
    public void checkExperimentGetQuestions() {
        Experiment experiment = createMockExperiment();
        assertEquals(4, experiment.getQuestions().size());
    }

}