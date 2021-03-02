package com.example.rocketapp;
import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Experiment extends FirestoreObject {

    public ExperimentInfo info;
    private ArrayList<Trial> trialsArrayList = new ArrayList<>();
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
    public ArrayList<Trial> getTrials() {
        return trialsArrayList;
    }

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
    public void setTrials(ArrayList<Trial> trials) {
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
}





