package com.example.rocketapp.model.experiments;

import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.FirestoreOwnableDocument;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.trials.Trial;
import com.google.firebase.firestore.Exclude;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static java.lang.Math.sqrt;

/**
 * Abstract class Experiment
 * Describes the details of an experiment (ExperimentInfo)
 * Describes the state of an experiment (Published, Unpublished, Ended)
 * Has an array list of comments posted on an experiment.
 * Provides abstract functions for all statistical methods used
 * Provides a toString() to describe display
 * Stores data on Firestore
 */
public abstract class Experiment extends FirestoreOwnableDocument {
    public static final String ID_KEY = "ID";
    public ExperimentInfo info;     //description, region, minTrials, geoLocation
    protected ArrayList<Trial> trialsArrayList = new ArrayList<>();       //Trials posted on this experiment
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

    /**
     * getter for type of experiment
     * @return type of experiment       - "Binomial" or "Count" or "IntCount" or "Measurement"
     */
    public abstract String getType();

    /**
     * @param isActive set experiment active and open to add trials, or inactive (ended)
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @param isActive set experiment to be published (visible to others) or unpublished (hidden)
     */
    public void setPublished(boolean isActive) {
        this.isPublished = isActive;
    }

    /**
     * @return is this experiment open to new trials
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @return is this experiment visible
     */
    public boolean isPublished() {
        return isPublished;
    }

    /**
     *
     * @return an array list featuring all trials submitted under this experiment
     */
    @Exclude
    public ArrayList<Trial> getTrials(boolean includeIgnored) {
        if (includeIgnored) {
            return trialsArrayList;
        } else {
            ArrayList<Trial> filteredTrials = new ArrayList<>();
            for(int i = 0; i < trialsArrayList.size(); i++){
                if(!trialsArrayList.get(i).getIgnored()){
                    filteredTrials.add(trialsArrayList.get(i));
                }
            }
            return filteredTrials;
        }
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
     * setter for array list featuring all trials posted in this experiment
     * @param trials
     *          array list of trials which must be of the same type as experiment
     */
    @Exclude
    public void setTrials(ArrayList<Trial> trials) {
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
     * Excluded from getting stored inside firestore.
     * Calculates the median from all trials present in this experiment
     * @return the median of experiment
     */
    @Exclude
    public float getMedian(){
        ArrayList<Trial> trials = getTrials(false);
        Collections.sort(trials);
        if (trials.size() == 0) return 0;
        int length = trials.size();
        float median;
        if(length%2==0) {
            median = (trials.get((length / 2) - 1).getValue() + trials.get(length / 2).getValue()) / 2;
        }
        else {
            median = (trials.get((length / 2)).getValue());
        }
        return median;
    }

    /**
     * getter for mean of this experiment
     * @return mean
     */
    @Exclude
    public float getMean() {
        ArrayList<Trial> trials = getTrials(false);
        if (trials.size() == 0) return 0;

        float sum = 0.0f;
        for (Trial trial : trials) {
            sum += trial.getValue();
        }

        return sum / trials.size();
    }

    /**
     * getter for data filtered mean of this experiment
     * @param date will get mean for all filtered trials before data
     * @return mean of trials before date
     */
    @Exclude
    public float getMean(Date date) {
        ArrayList<Trial> trials = getTrials(false);
        if (trials.size() == 0) {return 0;}
        float sum = 0;
        if(trials.size()==0){
            return 0;
        }
        int trialCounter = 0;
        for(int i = 0; i<trials.size() ; i++){
            if(trials.get(i).getTimestamp().toDate().after(date)) {continue;}
            sum = sum + trials.get(i).getValue();
            trialCounter++;
        }
        return sum / trialCounter;
    }

    /**
     * getter for standard deviation of this experiment under normal curve
     * @return standard deviation
     */
    public float getStdDev() {
        ArrayList<Trial> trials = getTrials(false);
        if (trials.size() == 0) return 0;
        float mean = getMean();
        float squareSum = 0;
        float meanDif;
        for(int i = 0; i<trials.size(); i++){
            meanDif = (trials.get(i).getValue() - mean);
            squareSum = squareSum + (meanDif * meanDif);
        }
        final double stdDev = sqrt(squareSum / trials.size());
        return (float) stdDev;
    }

    /**
     * getter for 75th percentile
     * @return Q3
     */
    @Exclude
    public float getTopQuartile() {
        float quart;
        ArrayList<Trial> trials = getTrials(false);
        if (trials.size() == 0) {return 0;}
        Collections.sort(trials);
        switch(trials.size() % 4){
            case (0):
                quart = (trials.get(( trials.size() * 3) / 4 - 1).getValue() + trials.get((trials.size() * 3) / 4 ).getValue()) / 2;
                return quart;
            case (1):
                quart = (trials.get(((trials.size() - 1) * 3) / 4 ).getValue() + trials.get(((trials.size() - 1) * 3) / 4 + 1).getValue()) / 2;
                return quart;
            case (2):
                quart = trials.get(((trials.size() - 2)* 3) / 4 + 1).getValue();
                return quart;
            default:
                quart = trials.get(((trials.size() - 3)* 3) / 4 + 2).getValue();
                return quart;
        }
    }

    /**
     * getter for 25th percentile
     * @return Q1
     */
    @Exclude
    public float getBottomQuartile() {
        float quart;
        ArrayList<Trial> trials = getTrials(false);
        if (trials.size() == 0) return 0;
        Collections.sort(trials);
        switch (trials.size() % 4){
            case (0):
                quart = ((trials.get( trials.size()  / 4 - 1).getValue() + trials.get(trials.size() / 4 ).getValue()) )/ 2;
                return quart;
            case (1):
                quart = ((trials.get( (trials.size() - 1 )/ 4 - 1).getValue() + trials.get((trials.size() - 1 )/ 4 ).getValue()) )/ 2;
                return quart;
            case (2):
                quart = (trials.get((trials.size() - 2) / 4 ).getValue());
                return quart;
            default:
                quart =(trials.get((trials.size() - 3) / 4 ).getValue());
                return quart;
        }
    }

}




