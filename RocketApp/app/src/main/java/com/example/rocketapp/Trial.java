package com.example.rocketapp;

import com.google.firebase.firestore.Exclude;

public class Trial extends FirestoreObject {

    private String description;

    public Trial() { }

    public Trial(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
