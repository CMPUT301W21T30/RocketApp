package com.example.rocketapp;

public abstract class Trial extends DataManager.FirestoreNestableDocument implements DataManager.Type {

    private String description;

    public Trial() { }

    public Trial(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public abstract String getType();
}
