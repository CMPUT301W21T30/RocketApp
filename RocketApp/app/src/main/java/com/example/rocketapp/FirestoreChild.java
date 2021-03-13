package com.example.rocketapp;

public abstract class FirestoreChild extends FirestoreObject {
    private DataManager.ID parent;

    public DataManager.ID getParent() {
        return parent;
    }
}
