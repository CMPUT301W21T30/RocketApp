package com.example.rocketapp;
import android.util.Log;
import com.google.firebase.firestore.Exclude;
import static android.content.ContentValues.TAG;

public abstract class FirestoreObject {
    private DataManager.ID id;
    private DataManager.ID ownerId;

    @Exclude
    public DataManager.ID getId() {
        return id;
    }

    public DataManager.ID getOwner() {
        return ownerId;
    }

    // Should only be called in DataManager
    public void setId(DataManager.ID id) {
        if (id == null || !id.isValid())
            Log.d(TAG, "Tried to call setId with invalid id. Function can only be called from DataManager.");
        this.id = id;
    }

    @Exclude
    public boolean isValid() {
        return id != null;
    }

    public void setOwner(DataManager.ID id) {
        if (id == null || !id.isValid())
            Log.d(TAG, "Tried to call setOwnerId with invalid id. Function can only be called from DataManager.");
        ownerId = id;
    }

    @Exclude
    public boolean ownerIsValid() {
        return ownerId != null;
    }
}
