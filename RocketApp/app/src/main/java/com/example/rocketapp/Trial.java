package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public abstract class Trial extends FirestoreChild {

    private String description;

    public Trial() { }

    public Trial(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getType();
}
