package com.example.rocketapp.model;

import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;

/**
 * Class to implement the scanned code and its corresponding experiment trial
 */
public class Code {
    private String code;
    private String experimentinfo;
    private Boolean ifBinomial;
    private Float ifNotBinomial;

    public Code (){}

    /**
     * store scanned code to a string
     * @param code
     */
    public void setCode(String code){
        this.code = code;
    }

    /**
     * store the experiment that code corresponds to
     * @param experiment
     */
    public void setExperiment(String experiment){
        this.experimentinfo = experiment;
    }

    /**
     * if a binomial trial is registered, register a pass of fail value
     * @param success
     */
    public void setIfBinomial(Boolean success){
        this.ifBinomial = success;
    }

    public Boolean getIfBinomial(){
        return this.ifBinomial;
    }

    public void setIfNotBinomial(Float trial){
        this.ifNotBinomial = trial;
    }


    public Float getIfNotBinomial(){
        return this.ifNotBinomial;
    }

    /**
     * return the code
     * @return
     */
    public String getCode(){
        return this.code;
    }

    /**
     * return the experiment
     * @return
     */
    public String getExperiment(){
        return this.experimentinfo;
    }


}
