package com.example.rocketapp;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import static android.content.ContentValues.TAG;

public class Question {

    private String id;
    private String text;
    private User owner;
//    private List<Answer> answerList;

    public Question() {}

    public Question(String text, User user) {
        this.text = text;
        this.owner = user;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        owner = user;
    }

    public String getText() {
        return text;
    }

    public void setText(User user, String text) {
        if (this.owner.getId() != user.getId()) {
            Log.e(TAG, "Owner does not have permission to edit this question");
            return;
        }

        this.text = text;
        // TODO make this sync with firestore
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }
}
