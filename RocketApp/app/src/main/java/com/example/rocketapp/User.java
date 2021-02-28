package com.example.rocketapp;
import com.google.firebase.firestore.Exclude;

public class User {

    private String name;
    private String id;

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
    public String getId() {
        return id;
    }

    @Exclude  // Should only be called in DataManager
    public void setId(String id) {
        this.id = id;
    }

    public void update(User user, DataManager.PushUserCallback onComplete) {
        if (this.id != user.id) return;

        this.name = user.name;

        DataManager.push(this, onComplete);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public boolean equals(User user) {
        return user.id.equals(id);
    }
}
