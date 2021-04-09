package com.example.rocketapp.controller;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.CountExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.experiments.IntCountExperiment;
import com.example.rocketapp.model.experiments.MeasurementExperiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.trials.Trial;

import java.util.ArrayList;
import java.util.Random;

public class MockExperimentManager extends ExperimentManager {
    private static Random rand = new Random();

    public MockExperimentManager(){}

    private Experiment createMockExperiment(Experiment experiment, int userIndex) {
        ((FirestoreOwnableDocument) experiment).setOwner(UserManager.getUserArrayList().get(userIndex));
        ((FirestoreOwnableDocument) experiment).setId(new FirestoreDocument.Id(String.valueOf(rand.nextInt())));
        return experiment;
    }

    @Override
    protected void initializeExperiments() {
        experimentArrayList = new ArrayList<>();

        Experiment experiment = createMockExperiment(new BinomialExperiment("Coin flip experiment", "Alberta", 50, false), 0);

        ArrayList<Trial> trials = new ArrayList<>();
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(true));
        trials.add(new BinomialTrial(false));
        trials.add(new BinomialTrial(false));
        experiment.setTrials(trials);

        experimentArrayList.add(experiment);
        experimentArrayList.add(createMockExperiment(new MeasurementExperiment("Daily walking distance experiment", "Edmonton", 50, false), 1));
        experimentArrayList.add(createMockExperiment(new IntCountExperiment("Counting broken eggs in dropped egg carton", "Calgary", 50, false), 2));
        experimentArrayList.add(createMockExperiment(new CountExperiment("Red car count experiment", "Calgary", 50, false), 3));
    }

    @Override
    protected void createExperimentImp(Experiment experiment, ObjectCallback<Experiment> onSuccess, ObjectCallback<Exception> onFailure) {
        ((FirestoreOwnableDocument) experiment).setOwner(UserManager.getUser());
        ((FirestoreOwnableDocument) experiment).setId(new FirestoreDocument.Id(String.valueOf(rand.nextInt())));
        experimentArrayList.add(experiment);
        onSuccess.callBack(experiment);
        updateCallback.callBack();
    }

    @Override
    protected void publishExperimentImp(Experiment experiment, ObjectCallback<Experiment> onSuccess, ObjectCallback<Exception> onFailure) {
        experiment.setPublished(true);
        onSuccess.callBack(experiment);
        updateCallback.callBack();
    }

    @Override
    protected void unpublishExperimentImp(Experiment experiment, ObjectCallback<Experiment> onSuccess, ObjectCallback<Exception> onFailure) {
        experiment.setPublished(false);
        onSuccess.callBack(experiment);
        updateCallback.callBack();
    }

    @Override
    protected void endExperimentImp(Experiment experiment, ObjectCallback<Experiment> onSuccess, ObjectCallback<Exception> onFailure) {
        experiment.setActive(false);
        onSuccess.callBack(experiment);
        updateCallback.callBack();
    }

    @Override
    protected void listenImp(Experiment experiment, ObjectCallback<Experiment> onUpdate) {
        onUpdate.callBack(experiment);
        updateCallback.callBack();
    }

    @Override
    protected void updateImp(Experiment experiment, ObjectCallback<Experiment> onSuccess, ObjectCallback<Exception> onFailure) {
        onSuccess.callBack(experiment);
        updateCallback.callBack();
    }
}
