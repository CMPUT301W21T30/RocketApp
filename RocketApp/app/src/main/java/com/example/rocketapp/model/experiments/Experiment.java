package com.example.rocketapp.model.experiments;
import android.util.Log;

import com.example.rocketapp.controller.DataManager;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.trials.Trial;
import com.example.rocketapp.model.users.User;
import com.google.firebase.firestore.Exclude;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

/**
 * Abstract class Experiment
 * Describes the details of an experiment (ExperimentInfo)
 * Describes the state of an experiment (Published, Unpublished, Ended)
 * Has an array list of comments posted on an experiment.
 * Provides abstract functions for all statistical methods used
 * Provides a toString() to describe display
 * Stores data on Firestore
 */
public abstract class Experiment extends DataManager.FirestoreOwnableDocument implements DataManager.Type {

    public ExperimentInfo info;     //description, region, minTrials, geoLocation
    private State state;            //Published, Unpublished or Ended
    protected ArrayList<? extends Trial> trialsArrayList = new ArrayList<>();       //Trials posted on this experiment
    private ArrayList<Question> questionsArrayList = new ArrayList<>();         //Comments posted on this experiment

    public enum State {
        PUBLISHED,      //Experiment is visible and accepting trials
        ENDED,          //Experiment is visible but not accepting trials
        UNPUBLISHED     //Experiment is not visible
    }

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
        this.state = State.PUBLISHED;
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

    /**
     * If the owner wants to end the experiment after concluding it or for any other reason.
     * @param user
     *          Only owner can end the experiment, thus if user passed is not owner, they will be unable to END it
     */
    public void endExperiment(User user) {
        if (user == null || user.getId() != this.getOwnerId()) {
            Log.e(TAG, "Cannot end experiment. Not the owner.");
            return;
        }

        state = State.ENDED;        //Experiment ended
    }

    /**
     * getter for state of experiment
     * @return state of experiment
     */
    public State getState() {
        return state;
    }

    /**
     * setter for state of experiment, can be used to modify an already existing experiment's state
     * @param state
     *          New state of experiment
     *
     */
    public void setState(State state) {
        this.state = state;         //state of experiment modified
    }

    /**
     *
     * @return an array list featuring all trials submitted under this experiment
     */
    @Exclude
    public abstract ArrayList<? extends Trial> getTrials() ;

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
    public void update(ExperimentInfo info, DataManager.ExperimentCallback onComplete) {
        if (DataManager.getUser() == null || this.getOwnerId() != DataManager.getUser().getId()) return;
        this.info = info;

        DataManager.update(this, onComplete, (e) -> {});
    }

    /**
     * setter for array list featuring all trials posted in this experiment
     * @param trials
     *          array list of trials which must be of the same type as experiment
     */
    @Exclude
    public void setTrials(ArrayList<? extends Trial> trials) {
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
                ", state=" + state +
                ", trialsArrayList=" + trialsArrayList +
                ", questionsArrayList=" + questionsArrayList +
                '}';
    }

    /**
     * @return String for search querying
     */
    public String toSearchString() {
        return getOwner().getName() + info.toSearchString() + state;
    }

    /**
     * getter for type of experiment
     * @return type of experiment       - "Binomial" or "Count" or "IntCount" or "Measurement"
     */
    @Override
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




