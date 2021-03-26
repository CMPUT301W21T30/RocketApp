package com.example.rocketapp.controller;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

public abstract class FirestoreNestableDocument extends FirestoreOwnableDocument {
    private FirestoreDocument.Id parentId;

    /**
     * @return firestore documentId for this objects parent
     */
    public FirestoreDocument.Id getParentId() {
        return parentId;
    }

    /**
     * @param id parent Id for nested object
     */
    void setParent(FirestoreDocument.Id id) {
        if (id == null || !id.isValid())
            Log.d(TAG, "Tried to call setParent with invalid id.");
        parentId = id;
    }

    /**
     * @return true if id, ownerId, and parentId are valid
     */
    @Exclude
    public boolean parentIsValid() {
        return parentId != null && parentId.isValid();
    }

    /**
     * @return true if documentId, ownerDocumentId, and parentId are valid.
     */
    @Override
    @Exclude
    public boolean isValid() {
        return super.isValid() && parentIsValid();
    }
}