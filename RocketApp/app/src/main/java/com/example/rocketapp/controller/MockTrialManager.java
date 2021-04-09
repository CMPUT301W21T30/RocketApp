package com.example.rocketapp.controller;

import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;

public class MockTrialManager extends TrialManager {

    public MockTrialManager() {}

    @Override
    protected void listenImp(Experiment experiment, ObjectCallback<Experiment> onUpdate) {
        this.onUpdate = onUpdate;
        this.onUpdate.callBack(experiment);
    }

    @Override
    protected void addTrialImp(Trial trial, Experiment experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        experiment.getTrials(true).add(trial);
        onUpdate.callBack(experiment);
    }

    @Override
    protected void updateImp(Trial trial, Experiment experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        onUpdate.callBack(experiment);
    }
}
