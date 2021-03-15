package com.example.rocketapp;

import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Comments are posted by Users (Experimenters and Owner alike) to have a discussion regarding the experiment or trial.
 * Generally these will be used in the form of Question & Answer.
 */
public abstract class Comment extends DataManager.FirestoreNestableDocument implements DataManager.Type {
    private String text;    // Text of comment

    /**
     * Constructor for an object created of a class that inherits Comment.
     * No text is passed if this constructor is called.
     */
    public Comment() {};

    /**
     *
     * @param text
     *          User passes the text to be written inside comment box.
     */
    public Comment(String text) {
        this.text = text;
    }

    /**
     *
     * @return the type of Comment.     - "Question", "Answer", or "None".
     */
    @Override
    public abstract String getType();

    /**
     *
     * @return the text of comment.
     */
    public String getText() {
        return text;
    }

    /**
     * Poster can edit comment through this function.
     *
     * @param user
     *          poster of this comment
     * @param text
     *          text of the comment
     */
    public void setText(User user, String text) {
        if (this.getOwnerId() != user.getId()) {
            Log.e(TAG, "Owner does not have permission to edit this question");
            return;
        }       //Only the poster of comment can edit the comment.

        this.text = text;
        // TODO make this sync with firestore
    }
}
