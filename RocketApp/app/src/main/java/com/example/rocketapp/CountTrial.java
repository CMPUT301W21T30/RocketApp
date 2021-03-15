package com.example.rocketapp;
import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'Count' type.
 * The trials are either True(Success) or False(Fail)
 */
public class CountTrial extends Trial implements Comparable<CountTrial>{
    public static final String TYPE = "Count";      //Type of trial

    private int numberCounted;      //value of trial

    /**
     * Constructor for CountTrial initialized with 0 if no value is passed
     */
    public CountTrial() {
        numberCounted = 0;
    }

    /**
     * Constructor for CountTrial where a value for count is passed.
     * @param number
     *          number is set as the value of this trial.
     */
    public CountTrial(int number){
        numberCounted = number;
    }

    /**
     * @return the type of trial, objects of this class will return "Count"
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Increment a value already present in a trial by 1
     */
    public void addCount(){
        numberCounted = numberCounted + 1;
    }

    /**
     * @return the value of this trial
     */
    public int getCount(){
        return numberCounted;
    }

    /**
     * @param trial
     *          trial parameter is a different object of CountTrial class which gets compared to this trial based on their value
     * @return the difference in (this object's trial value - passed object's trial value)
     */
    @Override
    public int compareTo(CountTrial trial) {//references: https://www.geeksforgeeks.org/how-to-sort-an-arraylist-of-objects-by-property-in-java/
        int compareCount = ((CountTrial)trial).getCount();
        return this.getCount() - compareCount;
    }
}