package com.example.rocketapp.controller;

import android.util.Log;

import com.example.rocketapp.model.users.User;
import com.google.firebase.firestore.Exclude;

public abstract class FirestoreOwnableDocument extends FirestoreDocument {

    private FirestoreDocument.Id ownerId;     // The firestore documentId for this objects owner

    /**
     * @return firestore documentId for this objects owner
     */
    public FirestoreDocument.Id getOwnerId() {
        return ownerId;
    }

    /**
     * Set the ownerDocumentId for this object. Use when creating objects in subcollections of an object in firestore.
     * @param id owner documentId for this object.
     */
    void setOwnerId(FirestoreDocument.Id id) {
        if (id == null || !id.isValid())
            Log.e(TAG, "Tried to call setOwnerId with invalid id.");
        else
            ownerId = id;
    }


    /**
     * @return The User that owns this object. null if user not found.
     */
    @Exclude
    public User getOwner() {
        return DataManager.getUser(ownerId);
    }

    /**
     * @return true if owner documentId is valid.
     */
    @Exclude
    public boolean ownerIsValid() {
        return ownerId != null && ownerId.isValid();
    }

    /**
     * @return true if documentId and ownerDocumentId are valid.
     */
    @Override
    @Exclude
    public boolean isValid() {
        return super.isValid() && ownerIsValid();
    }
}
