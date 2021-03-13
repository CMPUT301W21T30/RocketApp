package com.example.rocketapp;

import android.util.Log;
import static android.content.ContentValues.TAG;

public abstract class Comment extends FirestoreObject {
    private String text;
    private DataManager.ID parent;

    public Comment() {};

    public Comment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public DataManager.ID getParent() {
        return parent;
    }

    public void setText(User user, String text) {
        if (this.getOwner() != user.getId()) {
            Log.e(TAG, "Owner does not have permission to edit this question");
            return;
        }

        this.text = text;
        // TODO make this sync with firestore
    }
}
