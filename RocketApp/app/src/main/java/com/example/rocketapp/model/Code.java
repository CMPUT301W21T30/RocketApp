package com.example.rocketapp.model;

import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;

/**
 * Class to implement the scanned code and its corresponding experiment trial
 */
public class Code {
    private String code;
    private Experiment experiment;
    private Trial trial;

    /**
     * store scanned code to a string
     * @param code
     */
    void setCode(String code){
        this.code = code;
    }

    /**
     * store the experiment that code corresponds to
     * @param experiment
     */
    void setExperiment(Experiment experiment){
        this.experiment = experiment;
    }

    /**
     * store trial that the code corresponds to
     * @param trial
     */
    void setTrial(Trial trial){
        this.trial = trial;
    }

    /**
     * return the code
     * @return
     */
    String getCode(){
        return this.code;
    }

    /**
     * return the experiment
     * @return
     */
    Experiment getExperiment(){
        return this.experiment;
    }

    /**
     * return the trial
     * @return
     */
    Trial getTrial(){
        return this.trial;
    }



}
