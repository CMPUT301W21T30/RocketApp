package com.example.rocketapp;
import java.util.ArrayList;
import java.lang.Integer;
import com.google.firebase.firestore.Exclude;

public class IntCountExperiment extends Experiment {

    private ArrayList<IntCountTrial> trials = new ArrayList<>();

    public IntCountExperiment() {
        //To be added
    }

    public float getMedian(){
        float length = trials.size();
        float median = 0.0
        if (length%2==1){
            median = trials.get((length/2)+1).floatValue();
        }
        else{
            median = (trials.get((length/2)+1)+trials.get(length/2))/2
        }
        return median;
    }

}
