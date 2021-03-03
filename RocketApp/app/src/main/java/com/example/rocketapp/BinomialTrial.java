package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class BinomialTrial extends Trial {
    public static final String TYPE = "Binomial";

    private boolean value;

    public BinomialTrial(String description) {
        super(description);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Exclude
    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}