package com.example.rocketapp;
import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public abstract class Experiment extends FirestoreObject {

    public ExperimentInfo info;
    protected ArrayList<? extends Trial> trialsArrayList = new ArrayList<>();
    private ArrayList<Question> questionsArrayList = new ArrayList<>();
    private boolean isActive;

    public Experiment(){}

    public Experiment(ExperimentInfo info) {
        this.info = info;
        isActive = true;
    }

    public Experiment(String name, String description, String region, int minTrials, boolean geoLocationEnabled) {
        this(new ExperimentInfo(name, description, region, minTrials, geoLocationEnabled));
    }

    public void endExperiment(User user) {
        if (user == null || user.getId() != info.getOwner()) {
            Log.e(TAG, "Cannot end experiment. Not the owner.");
            return;
        }

        isActive = false;
    }

    public boolean getIsActive() {
        return isActive;
    }

    @Exclude
    public abstract ArrayList<? extends Trial> getTrials() ;

    @Exclude
    public ArrayList<Question> getQuestions() {
        return questionsArrayList;
    }

    public void update(ExperimentInfo info, DataManager.ExperimentCallback onComplete) {
        if (DataManager.getUser() == null || this.getOwner() != DataManager.getUser().getId()) return;

        this.info = info;

        DataManager.push(this, onComplete);
    }

    @Exclude
    public void setTrials(ArrayList<? extends Trial> trials) {
        trialsArrayList = trials;
    }

    @Exclude
    public void setQuestions(ArrayList<Question> questions) {
        questionsArrayList = questions;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id-" + getId() +
                "info=" + info +
                '}';
    }

    public abstract String getType();

    @Exclude
    public abstract float getMedian();

    @Exclude
    public abstract float getMean();

    @Exclude
    public abstract float getStdDev();

    @Exclude
    public abstract float getQuartiles();

}




