package com.example.rocketapp.model.trials;
import com.google.firebase.firestore.Exclude;

/**
 * Class for Trials of 'Count' type.
 * The trials are either True(Success) or False(Fail)
 */
public class CountTrial extends Trial implements Comparable<CountTrial>{
    public static final String TYPE = "Count";      //Type of trial
    private int count;      //value of trial

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public CountTrial() {}

    /**
     * Constructor for CountTrial where a value for count is passed.
     * @param count
     *          the count for this trial.
     */
    public CountTrial(int count){
        this.count = count;
    }

    /**
     * @return the type of trial, objects of this class will return "Count"
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Setter for count
     * @param count Value to initialize count with
     */
    public void setCount(int count){
        this.count = count;
    }

    /**
     * @return the value of this trial
     */
    public int getCount(){
        return count;
    }

    /**
     * @return string representation of trial value
     */
    @Exclude
    @Override
    public String getValueString(){return Float.toString(count); }

    /**
     * @param trial trial to compare to
     * @return the difference in (this object's trial value - passed object's trial value)
     */
    @Override
    public int compareTo(CountTrial trial) {//references: https://www.geeksforgeeks.org/how-to-sort-an-arraylist-of-objects-by-property-in-java/
        int compareCount = trial.getCount();
        return this.getCount() - compareCount;
    }
}