package com.example.rocketapp.controller;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

/**
 * A class object that will be stored on firestore. Contains information relating to its own documentId, as well
 * as the documentId of an object that "owns" this object.
 * Subclassed in DataManager since id's must be retrieved through firestore, and so should only be set in DataManager.
 */
public abstract class FirestoreDocument  {
    public final String TAG = "FirestoreDocument";


    /**
     * Id represents a documentId in firestore for finding and referencing documents.
     * Private class creates "Friend" like functionality so function calls requiring a new ID can only be called
     * from DataManager to make sure updates will be synced with firestore.
     */
    final static class Id implements Serializable {
        private String key;

        /**
         * Default constructor only used for interface with Firestore.
         */
        public Id() {}

        /**
         * Generates a new DocumentId. Private so new documentId's can only be created by DataManager.
         * @param id documentId string from firestore
         */
        Id(String id) {
            this.key = id;
        }

        /**
         * Returns true if it has a valid key. Will only be true for objects pulled from firestore.
         * @return true if has valid key (must be set in DataManager to be valid)
         */
        @Exclude
        public boolean isValid() {
            return key != null;
        }

        /**
         * Returns the documentId.
         */
        public String getKey() {
            return key;
        }

        /**
         * @param o object to compare
         * @return true if keys match
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return this.key.equals(id.key);
        }
    }

    private Id id;         // The firestore documentId for this object

    /**
     * @return firestore documentId for this object
     */
    @Exclude
    public Id getId() {
        return id;
    }

    /**
     * @param id documentId for this object retrieved from firestore
     */
    void setId(Id id) {
        if (id == null || !id.isValid())
            Log.e(TAG, "Tried to call setId with invalid id.");
        this.id = id;
    }

    /**
     * @return true if documentId is valid.
     */
    @Exclude
    public boolean isValid() {
        return id != null && id.isValid();
    }

    /**
     * @param o object to compare
     * @return true if documentId's match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirestoreDocument that = (FirestoreDocument) o;
        return id.equals(that.id);
    }

}
