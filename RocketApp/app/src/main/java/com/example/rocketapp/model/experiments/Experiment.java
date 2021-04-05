package com.example.rocketapp.model.experiments;
import android.util.Log;

import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.FirestoreOwnableDocument;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.trials.Trial;
import com.example.rocketapp.model.users.User;
import com.google.firebase.firestore.Exclude;
import java.util.ArrayList;
import java.util.Date;

/**
 * Abstract class Experiment
 * Describes the details of an experiment (ExperimentInfo)
 * Describes the state of an experiment (Published, Unpublished, Ended)
 * Has an array list of comments posted on an experiment.
 * Provides abstract functions for all statistical methods used
 * Provides a toString() to describe display
 * Stores data on Firestore
 */
public abstract class Experiment<TrialType extends Trial> extends FirestoreOwnableDocument {

    public ExperimentInfo info;     //description, region, minTrials, geoLocation
    protected ArrayList<TrialType> trialsArrayList = new ArrayList<>();       //Trials posted on this experiment
    private ArrayList<Question> questionsArrayList = new ArrayList<>();         //Comments posted on this experiment
    private boolean isPublished, isActive;

    /**
     * Default constructor for firestore serialization.
     */
    public Experiment(){}

    /**
     *
     * @param info
     *          Sets the details of an experiment - description, region, minTrials and geoLocation
     *          Sets the state of Experiment as PUBLISHED default. Can be modified later
     */
    public Experiment(ExperimentInfo info) {
        this.info = info;
        this.isPublished = true;
        this.isActive = true;
    }

    /**
     * Constructor for an experiment where instead of passing an object of class ExperimentInfo, the info is directly passed
     * @param description   - String
     *          Details regarding what the experiment is and how to perform
     * @param region    - String
     *          Region where the experiment is performed.
     * @param minTrials     - Int
     *          The minimum number of trials required to derive a conclusion.
     * @param geoLocationEnabled    - Boolean
     *          True if the trial requires user to submit their geoLocation, False otherwise.
     */
    public Experiment(String description, String region, int minTrials, boolean geoLocationEnabled) {
        this(new ExperimentInfo(description, region, minTrials, geoLocationEnabled));
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setPublished(boolean isActive) {
        this.isPublished = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isPublished() {
        return isPublished;
    }

    /**
     *
     * @return an array list featuring all trials submitted under this experiment
     */
    @Exclude
    public ArrayList<TrialType> getTrials() {
        return trialsArrayList;
    }

    /**
     * @return An ArrayList of all the trials that are not ignored by the owner
     */
    @Exclude
    public ArrayList<TrialType> getFilteredTrials() {
        ArrayList<TrialType> trials = getTrials();
        ArrayList<TrialType> filteredTrials = new ArrayList<>();
        for(int i = 0; i < trials.size(); i++){
            if(! trials.get(i).getIgnored()){
                filteredTrials.add(trials.get(i));
            }
        }
        return filteredTrials;
    }

    /**
     *
     * @return an array list of all questions under this experiment
     */
    @Exclude
    public ArrayList<Question> getQuestions() {
        return questionsArrayList;
    }

    /**
     * Owner wants to update experiment information
     * @param info
     *          New info to be updated with
     * @param onComplete
     *          Callback to DataManager to update Firestore
     */
    public void update(ExperimentInfo info, ExperimentManager.ExperimentCallback onComplete) {
        if (!UserManager.isSignedIn() || !this.getOwner().equals(UserManager.getUser())) return;
        this.info = info;

        ExperimentManager.update(this, onComplete, (e) -> {});
    }

    /**
     * setter for array list featuring all trials posted in this experiment
     * @param trials
     *          array list of trials which must be of the same type as experiment
     */
    @Exclude
    public void setTrials(ArrayList<TrialType> trials) {
        trialsArrayList = trials;
    }

    /**
     * setter for array list featuring all questions posted in this experiment
     * @param questions
     *          array list of questions
     */
    @Exclude
    public void setQuestions(ArrayList<Question> questions) {
        questionsArrayList = questions;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id=" + getId() +
                "owner=" + getOwner().getName() +
                "info=" + info +
                ", isActive=" + isActive +
                ", isPublished" + isPublished +
                ", trialsArrayList=" + trialsArrayList +
                ", questionsArrayList=" + questionsArrayList +
                '}';
    }

    /**
     * @return String for search querying
     */
    public String toSearchString() {
        return getOwner().getName() + info.toSearchString();
    }

    /**
     * getter for type of experiment
     * @return type of experiment       - "Binomial" or "Count" or "IntCount" or "Measurement"
     */
    public abstract String getType();

    /**
     * getter for Median statistic of this experiment
     * @return median
     */
    @Exclude
    public abstract float getMedian();

    /**
     * getter for mean of this experiment
     * @return mean
     */
    @Exclude
    public abstract float getMean();

    @Exclude
    public abstract float getMean(Date date);

    /**
     * getter for standard deviation of this experiment under normal curve
     * @return standard deviation
     */
    @Exclude
    public abstract float getStdDev();

    /**
     * getter for 75th percentile
     * @return Q3
     */
    @Exclude
    public abstract float getTopQuartile();

    /**
     * getter for 25th percentile
     * @return Q1
     */
    @Exclude
    public abstract float getBottomQuartile();

}




