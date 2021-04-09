package com.example.rocketapp.controller;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.trials.IntCountTrial;
import com.example.rocketapp.model.trials.MeasurementTrial;
import com.example.rocketapp.model.trials.Trial;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.example.rocketapp.controller.FirestoreDocument.readFirebaseObjectSnapshot;

/**
 * Class that handles adding, retrieving, and modifying experiment trials from firestore.
 */
public class TrialManager {
    private static final String TAG = "TrialManager";
    private static final String EXPERIMENTS = "Experiments";
    private static final String TRIALS = "Trials";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ListenerRegistration trialsListener;
    protected ObjectCallback<Experiment> onUpdate;
    private static TrialManager instance;

    static final ImmutableMap<String, Class<? extends Trial>> trialClassMap = ImmutableMap.<String, Class<? extends Trial>>builder()
            .put(IntCountTrial.TYPE, IntCountTrial.class)
            .put(MeasurementTrial.TYPE, MeasurementTrial.class)
            .put(BinomialTrial.TYPE, BinomialTrial.class)
            .put(CountTrial.TYPE, CountTrial.class)
            .build();

    /**
     * Private constructor, should not be instantiated
     */
    protected TrialManager() {}

    public static void inject(TrialManager injection) {
        instance = injection;
    }

    private static TrialManager getInstance() {
        if (instance == null) instance = new TrialManager();
        return instance;
    }

    /**
     * Listens to firestore for changes to this experiment. You MUST use this to get the trials for an experiment.
     * You may want to update the UI in onUpdate.
     * @param experiment
     *      The experiment to listen to.
     * @param onUpdate
     *      Callback for implementing desired behaviour when the experiment is updated in firestore.
     */
    public static void listen(Experiment experiment, ObjectCallback<Experiment> onUpdate) {
        getInstance().listenImp(experiment, onUpdate);
    }
    protected void listenImp(Experiment experiment, ObjectCallback<Experiment> onUpdate) {
        this.onUpdate = onUpdate;

        if (!experiment.isValid()) {
            Log.e(TAG, "Cannot listen to an experiment without an id.");
            return;
        }

        // Listen for changes in Trials
        CollectionReference trialsRef = db.collection(EXPERIMENTS).document(experiment.getId().getKey()).collection(TRIALS);
        if (trialsListener != null) trialsListener.remove();
        trialsListener = trialsRef.addSnapshotListener((snapshots, e) -> {
            parseTrialsSnapshot(experiment, snapshots);
            Log.d(TAG, "Experiment Trials Updated: " + experiment.getTrials(true).size());
            onUpdate.callBack(experiment);
        });
    }


    /**
     * Add a new trial for an experiment.
     * @param trial
     *      New trial to add.
     * @param experiment
     *      Experiment to add trial to.
     * @param onSuccess
     *      Callback for when push is successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void addTrial(Trial trial, Experiment experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        getInstance().addTrialImp(trial, experiment, onSuccess, onFailure);
    }
    protected void addTrialImp(Trial trial, Experiment experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        push(trial, experiment, onSuccess, onFailure);
    }


    /**
     * Update a trial for an experiment.
     * @param trial
     *      New trial to add.
     * @param experiment
     *      Experiment to add trial to.
     * @param onSuccess
     *      Callback for when push is successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void update(Trial trial, Experiment experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        getInstance().updateImp(trial, experiment, onSuccess, onFailure);
    }
    protected void updateImp(Trial trial, Experiment experiment, ObjectCallback<Trial> onSuccess, ObjectCallback<Exception> onFailure) {
        push(trial, experiment, onSuccess, onFailure);
    }

    /**
     * Add or modify a trial on firestore
     * @param trial
     *      The trial to add/modify
     * @param experiment
     *      The experiment to add the trial to
     * @param onComplete
     *      Callback for when trial is added/modified successfully
     * @param onFailure
     *      Callback for when trial push fails
     */
    private static void push(Trial trial, Experiment experiment, ObjectCallback<Trial> onComplete, ObjectCallback<Exception> onFailure) {
        if (!UserManager.isSignedIn()) {
            Log.e(TAG, "Push failed. Must be logged in to push.");
            onFailure.callBack(new Exception("Push failed. Must be logged in to push."));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Push failed. Tried to add trial to experiment without ID.");
            onFailure.callBack(new Exception("Push failed. Tried to add trial to experiment without ID."));
            return;
        }

        if (trial == null) {
            Log.e(TAG, "Push failed. Tried to add null trial to experiment.");
            onFailure.callBack(new Exception("Push failed. Tried to add null trial to experiment."));
            return;
        }

        if (trial.ownerIsValid() && !UserManager.getUser().isOwner(experiment)) {
            Log.e(TAG, "Push failed. User does not own experiment. Cannot update trial.");
            onFailure.callBack(new Exception("Push failed. User does not own experiment. Cannot update trial."));
            return;
        }

        CollectionReference trialsRef = instance.db.collection(EXPERIMENTS).document(experiment.getId().getKey()).collection(TRIALS);

        if (trial.getId() != null) {
            trialsRef.document(trial.getId().getKey()).set(trial).addOnSuccessListener(trialSnapshot -> {
                Log.d(TAG, "Trial Updated.");
                onComplete.callBack(trial);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to update Trial.");
                onFailure.callBack(e);
            });
        } else {
            ((FirestoreOwnableDocument) trial).setOwner(UserManager.getUser());
            ((FirestoreNestableDocument) trial).setParent(experiment.getId());

            trialsRef.add(trial).addOnSuccessListener(trialSnapshot -> {
                ((FirestoreDocument) trial).setId(new FirestoreDocument.Id(trialSnapshot.getId()));
                Log.d(TAG, "Trial Added.");
                onComplete.callBack(trial);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to add Trial.");
                onFailure.callBack(e);
            });
        }
    }


    static Trial createTrial(String type, String value) {
        switch(type) {
            case BinomialTrial.TYPE:
                return new BinomialTrial(value.equals("true"));
            case IntCountTrial.TYPE:
                return new IntCountTrial(Integer.parseInt(value));
            case CountTrial.TYPE:
                return new CountTrial(Integer.parseInt(value));
            case MeasurementTrial.TYPE:
                return new MeasurementTrial(Float.parseFloat(value));
            default:
                return null;
        }
    }


    /**
     * Parses a trials list snapshot from firestore and adds them to an experiment.
     * @param experiment
     *      The experiment the trials belong to
     * @param userSnapshots
     *      The snapshot from firestore to parse
     */
    private void parseTrialsSnapshot(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Trial> array = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots) {
            Class<? extends Trial> classType = trialClassMap.get(snapshot.getString("type"));
            if (classType != null) array.add( readFirebaseObjectSnapshot(classType, snapshot, TAG));
            else Log.e(TAG, "classType null in parseTrialsSnapshot.");
        }

        experiment.setTrials(array);
    }
}
