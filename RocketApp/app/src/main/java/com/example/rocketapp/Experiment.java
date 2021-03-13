package com.example.rocketapp;
import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public abstract class Experiment extends FirestoreObject {

    public ExperimentInfo info;
    private State state;
    protected ArrayList<? extends Trial> trialsArrayList = new ArrayList<>();
    private ArrayList<Question> questionsArrayList = new ArrayList<>();

    enum State {
        ACTIVE,
        ENDED,
        UNPUBLISHED
    }

    public Experiment(){}

    public Experiment(ExperimentInfo info) {
        this.info = info;
        this.state = State.ACTIVE;
    }

    public Experiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        this(new ExperimentInfo(description, region, minTrials, geoLocationEnabled));
    }

    public void endExperiment(User user) {
        if (user == null || user.getId() != info.getOwner()) {
            Log.e(TAG, "Cannot end experiment. Not the owner.");
            return;
        }

        state = State.ENDED;
    }

    public State getState() {
        return state;
    }

    public void setState(DataManager.ID ownerId, State state) {
        this.state = state;
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

        DataManager.update(this, onComplete, (e) -> {});
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
    public abstract float getTopQuartile();

    @Exclude
    public abstract float getBottomQuartile();

}




