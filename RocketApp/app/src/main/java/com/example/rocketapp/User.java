package com.example.rocketapp;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

public class User extends FirestoreObject {

    private String name;
    // TODO add contact info

    public User(){}

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public DataManager.ID getOwner() {
        return super.getOwner();
    }

    public boolean equals(User user) {
        return user.getId().equals(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + getId() + '\'' +
                '}';
    }


}
