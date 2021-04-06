package com.example.rocketapp.controller;

import com.example.rocketapp.model.users.User;
import com.google.firebase.firestore.Exclude;


/**
 * An object with an id as well as an id of the user that owns the object.
 */
public abstract class FirestoreOwnableDocument extends FirestoreDocument {
    private FirestoreDocument.Id ownerId;     // The firestore documentId for this objects owner

    /**
     * @return firestore documentId for this objects owner
     */
    public FirestoreDocument.Id getOwnerId() {
        return ownerId;
    }


    /**
     * Set the ownerDocumentId for this object from a User.
     * @param owner owning user for this object.
     */
    void setOwner(User owner) {
        if (owner.isValid()) {
            ownerId = owner.getId();
        }
    }


    /**
     * @return The User that owns this object. null if user not found.
     */
    @Exclude
    public User getOwner() {
        return UserManager.getUser(ownerId);
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
