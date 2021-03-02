package com.example.rocketapp;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public abstract class Experiment extends FirestoreObject {

    public ExperimentInfo info;

    private String id;  // id is generated by firebase

    private ArrayList<Trial> trialsArrayList = new ArrayList<>();
    private ArrayList<Question> questionsArrayList = new ArrayList<>();

    public Experiment(){}

    public Experiment(ExperimentInfo info) {
        this.info = info;
    }

    public Experiment(String ownerId, String name, String description, String region, int minTrials, boolean geoLocationEnabled) {
        this(new ExperimentInfo(ownerId, name, description, region, minTrials, geoLocationEnabled));
    }

    public void pullTrials(DataManager.PullTrialsCallback callback) {
        DataManager.pullTrials(this, callback);
    }

    @Exclude
    public ArrayList<Trial> getTrials() {
        return trialsArrayList;
    }

    @Exclude
    public ArrayList<Question> getQuestions() {
        return questionsArrayList;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public void update(ExperimentInfo info, DataManager.PushExperimentCallback onComplete) {
        if (this.info.getOwnerId() != info.getOwnerId()) return;

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

