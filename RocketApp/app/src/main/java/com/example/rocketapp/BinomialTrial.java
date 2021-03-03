package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class BinomialTrial extends Trial {
    public static final String TYPE = "Binomial";

    private boolean value;

    public BinomialTrial(String description) {
        super(description);
        value = false;
    }

    public BinomialTrial(String description, boolean success) {
        super(description);
        value = success;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}