package com.example.rocketapp.controller;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A class object that will be stored on firestore.
 */
public abstract class FirestoreDocument  {
    public static final String TAG = "FirestoreDocument";
    private Timestamp timestamp;
    private Id id;


    /**
     * Id represents a documentId in firestore.
     */
    final static class Id implements Serializable {
        private String key;

        /**
         * Default constructor only used for interface with Firestore.
         */
        public Id() {}

        /**
         * Generates a new DocumentId. Package private so new documentId's can only be created by Controllers.
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


    public FirestoreDocument() {
        newTimestamp();
    }

    void newTimestamp() {
        timestamp = new Timestamp(Calendar.getInstance().getTime());
    }

    /**
     * @return the timestamp when this document was created
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }


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



    /**
     * Parses and adds id to FirestoreDocument objects from a firestore snapshot
     * @param typeClass
     *      The type of object to return
     * @param snapshot
     *      The snapshot from firestore
     * @param <ClassType>
     *     The type of object to return
     * @return
     *      Returns an object extending FirestoreDocument of type ClassType
     */
    static <ClassType extends FirestoreDocument> ClassType readFirebaseObjectSnapshot(Class<ClassType> typeClass, DocumentSnapshot snapshot, String TAG) {
        ClassType object = snapshot.toObject(typeClass);
        if (object != null) object.setId(new FirestoreDocument.Id(snapshot.getId()));
        else Log.e(TAG, "readFirebaseObjectSnapshot returned null");
        return object;
    }

}
