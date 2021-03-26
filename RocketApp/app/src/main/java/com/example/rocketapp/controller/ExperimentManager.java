package com.example.rocketapp.controller;

import android.util.Log;

import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.CountExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.experiments.IntCountExperiment;
import com.example.rocketapp.model.experiments.MeasurementExperiment;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExperimentManager {
    private static final String TAG = "ExperimentManager";
    private static ArrayList<Experiment> experimentArrayList;
    private static ArrayList<ListenerRegistration> experimentListeners = new ArrayList<>();
    private static ListenerRegistration experimentListener;
    private static final FirebaseFirestore db;
    private static CollectionReference experimentsRef;
    private static final String EXPERIMENTS = "Experiments";
    private static final String TRIALS = "Trials";
    private static DataManager.Callback updateCallback;

    // TODO Add any new Experiment types to this map
    static final ImmutableMap<String, Class<? extends Experiment>> experimentClassMap = ImmutableMap.<String, Class<? extends Experiment>>builder()
            .put(IntCountExperiment.TYPE, IntCountExperiment.class)
            .put(CountExperiment.TYPE, CountExperiment.class)
            .put(BinomialExperiment.TYPE, BinomialExperiment.class)
            .put(MeasurementExperiment.TYPE, MeasurementExperiment.class)
            .build();

    static {
        db = FirebaseFirestore.getInstance();
        initializeExperiments();
    }

    /**
     * Set callback for when Experiments are updated from firestore. Should use to update listviews of experiments.
     * @param callback
     *      Callback for when experiments are updated from firestore.
     */
    public static void setUpdateCallback(DataManager.Callback callback) {
        updateCallback = callback;
    }

    /**
     * Gets the list of all experiments
     * @return
     *      List of all experiments
     */
    public static ArrayList<Experiment> getExperimentArrayList() {
        return experimentArrayList;
    }


    /**
     * Get a filtered list of all experiments
     * @param searchWords
     *      String keywords to search for
     * @return
     *      filtered list of experiments
     */
    public static ArrayList<Experiment> getExperimentArrayList(String searchWords, boolean includeSubscribed, boolean includeOwned) {
        String[] words = searchWords.split(" ");
        ArrayList<FirestoreDocument.Id> ignored = new ArrayList<>();
        if (!includeSubscribed)
            ignored.addAll(UserManager.getSubscriptionsIdList());
        for(Experiment experiment: experimentArrayList){
            if (experiment.getState().equals(Experiment.State.UNPUBLISHED)) {
                ignored.add(experiment.getId());
            }
        }

        return getExperimentArrayList(experiment -> {
            if (ignored.contains(experiment.getId())) return false;
            System.out.println(experiment.getOwnerId().getKey() + " " + UserManager.getUser().getId().getKey());
            if (!includeOwned && experiment.getOwnerId().equals(UserManager.getUser().getId())) {
                System.out.println("false");
                return false;
            }

            for (String word : words)
                if (!experiment.toSearchString().toLowerCase().contains(word.toLowerCase()))
                    return false;
            return true;
        });
    }


    /**
     * Get a filtered list of all experiments
     * @param find
     *      Lambda for matching an experiment
     * @return
     *      filtered list of experiments
     */
    public static ArrayList<Experiment> getExperimentArrayList(DataManager.ExperimentSearch find) {
        ArrayList<Experiment> filteredExperiments = new ArrayList<>();

        for (Experiment experiment : experimentArrayList) {
            if (find.match(experiment))
                filteredExperiments.add(experiment);
        }

        return filteredExperiments;
    }


    /**
     * Gets all experiments owned by current user
     * @return
     *      list of owned experiments
     */
    public static ArrayList<Experiment> getOwnedExperimentsArrayList() {
        ArrayList<Experiment> filteredExperiments = new ArrayList<>();

        for (Experiment experiment : experimentArrayList) {
            if (experiment.isValid() && experiment.getOwnerId().equals(UserManager.getUser().getId()))
                filteredExperiments.add(experiment);
        }

        return filteredExperiments;
    }

    /**
     * Gets all experiments not subscribed by current user
     * @return
     *      list of not subscribed
     */
    public static ArrayList<Experiment> getNotSubscribedExperimentsArrayList() {
        return getExperimentArrayList(experiment -> !UserManager.getSubscriptionsIdList().contains(experiment.getId()));
    }

    /**
     * Gets all experiments subscribed to by current user
     * @return
     *      list of subscribed experiments
     */
    public static ArrayList<Experiment> getSubscribedExperimentArrayList() {
        ArrayList<Experiment> filteredExperiments = new ArrayList<>();

        for (FirestoreDocument.Id id : UserManager.getSubscriptionsIdList()) {
            for (Experiment experiment : experimentArrayList) {
                if (experiment.isValid() && experiment.getId().getKey().equals(id.getKey())) {
                    if(!(experiment.getState().equals(Experiment.State.UNPUBLISHED))) {
                        filteredExperiments.add(experiment);
                    }
                }
            }
        }

        return filteredExperiments;
    }


    public static Experiment getExperiment(Object id) {
        for (Experiment experiment : experimentArrayList)
            if (experiment.isValid() && experiment.getId().equals(id))
                return experiment;
        Log.e(TAG, "getExperiment() Experiment not found");
        return null;
    }

    public static Experiment getExperiment(String id) {
        for (Experiment experiment : experimentArrayList)
            if (experiment.isValid() && experiment.getId().toString().equals(id))
                return experiment;
        Log.e(TAG, "getExperiment() Experiment not found");
        return null;
    }



    /**
     * Publish a new experiment
     * @param experiment
     *      Experiment to publish
     * @param onSuccess
     *      Callback for when successful
     * @param onFailure
     *      Callback for failure
     */
    public static void publishExperiment(Experiment experiment, DataManager.ExperimentCallback onSuccess, DataManager.ExceptionCallback onFailure) {
        if (!UserManager.signedIn()) {
            Log.e(TAG, "publishExperiment() Failed. User must be signed in to publish experiment.");
            onFailure.callBack(new Exception("Publish Experiment Failed. User must be signed in to publish experiment."));
            return;
        }

        if (experiment == null) {
            Log.e(TAG, "publishExperiment() Failed. Experiment was null.");
            onFailure.callBack(new Exception("Publish Experiment Failed. Experiment was null."));
            return;
        }

        ((FirestoreOwnableDocument) experiment).setOwnerId(UserManager.getUser().getId());
        experiment.setState(Experiment.State.PUBLISHED);
        push(experiment, onSuccess, onFailure);
    }


    /**
     * Deletes an experiment
     * @param experiment
     *      Experiment to delete
     * @param onSuccess
     *      Callback for when successful
     * @param onFailure
     *      Callback for when fails
     */
    public static void unpublishExperiment(Experiment experiment, DataManager.ExperimentCallback onSuccess, DataManager.ExceptionCallback onFailure) {
        if (!UserManager.signedIn()) {
            Log.e(TAG, "User not logged in. Cannot un-publish experiment");
            onFailure.callBack(new Exception("User not logged in. Cannot un-publish experiment"));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Invalid experiment. Cannot un-publish.");
            onFailure.callBack(new Exception("Invalid experiment. Cannot un-publish experiment"));
            return;
        }

        if (!experiment.getOwnerId().equals(UserManager.getUser().getId())) {
            Log.e(TAG, "User (" + UserManager.getUser().getId().getKey() + ") does not own this experiment " + experiment.getOwnerId().getKey() + ". Cannot un-publish.");
            onFailure.callBack(new Exception("User does not own this experiment. Cannot un-publish."));
            return;
        }

        experiment.setState(Experiment.State.UNPUBLISHED);
        push(experiment, onSuccess, onFailure);
    }


    /**
     * Ends an experiment. Leaves the experiment visible, but should not be modified.
     * @param experiment
     *      The experiment to end.
     * @param onSuccess
     *      Callback for when successful
     * @param onFailure
     *      Callback with exception for when fails
     */
    public static void endExperiment(Experiment experiment, DataManager.ExperimentCallback onSuccess, DataManager.ExceptionCallback onFailure) {
        if (!UserManager.signedIn()) {
            Log.e(TAG, "User not logged in. Cannot end experiment");
            onFailure.callBack(new Exception("User not logged in. Cannot end experiment"));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Invalid experiment. Cannot end.");
            onFailure.callBack(new Exception("Invalid experiment. Cannot end."));
            return;
        }

        if (!experiment.getOwnerId().equals(UserManager.getUser().getId())) {
            Log.e(TAG, "User (" + UserManager.getUser().getId().getKey() + ") does not own this experiment " + experiment.getOwnerId().getKey() + ". Cannot end.");
            onFailure.callBack(new Exception("User does not own this experiment. Cannot end."));
            return;
        }

        experiment.setState(Experiment.State.ENDED);
        push(experiment, onSuccess, onFailure);
    }


    /**
     * Listens to firestore for changes to this experiment. You MUST use this to get the trials and questions for an experiment.
     * You may want to update the UI in onUpdate.
     * @param experiment
     *      The experiment to listen to.
     * @param onUpdate
     *      Callback for implementing desired behaviour when the experiment is updated in firestore.
     */
    public static void listen(Experiment experiment, DataManager.ExperimentCallback onUpdate) {
        if (!experiment.isValid()) {
            Log.e(TAG, "Cannot listen to an experiment without an id.");
            return;
        }

        if (experimentListeners == null) experimentListeners = new ArrayList<>();
        for (ListenerRegistration listener : experimentListeners) listener.remove();

        String documentId = experiment.getId().getKey();

        // Listen for changes to experiment info
        experimentListeners.add(experimentsRef.document(documentId).addSnapshotListener((snapshot, e) -> {
            Class<? extends Experiment> classType = experimentClassMap.get(snapshot.getString("type"));
            Experiment updatedExperiment = readFirebaseObjectSnapshot(classType, snapshot);
            if (updatedExperiment != null) experiment.info = updatedExperiment.info;
            else Log.e(TAG, "classType null in listen");

            onUpdate.callBack(experiment);
        }));

        TrialManager.listen(experiment, onUpdate);
        ForumManager.listen(experiment, onUpdate);
    }

    /**
     * Update an experiment. Can only be called by the experiments owner.
     * @param experiment
     *      Experiment to update
     * @param onSuccess
     *      Callback for when update is successful
     * @param onFailure
     *      Callback for when update fails
     */
    public static void update(Experiment experiment, DataManager.ExperimentCallback onSuccess, DataManager.ExceptionCallback onFailure) {
        push(experiment, onSuccess, onFailure);
    }

    /**
     * Add or update and experiment.
     * @param experiment
     *      Experiment to add or update
     * @param onComplete
     *      Callback for when push is successful
     * @param onFailure
     *      Callback for when push fails
     */
    private static void push(Experiment experiment, DataManager.ExperimentCallback onComplete, DataManager.ExceptionCallback onFailure) {
        FirestoreDocument.Id id = experiment.getId();
        if (id != null && id.isValid()) {
            experimentsRef.document(experiment.getId().getKey()).set(experiment)
                    .addOnSuccessListener((aVoid -> {
                        Log.d(TAG, "Experiment Updated.");
                        onComplete.callBack(experiment);
                    }))
                    .addOnFailureListener(e->{
                        Log.e(TAG, "Failed to update experiment experiment: " + e.toString());
                        onFailure.callBack(e);
                    });
        } else {
            experimentsRef.add(experiment)
                    .addOnSuccessListener(task -> {
                        ((FirestoreDocument) experiment).setId(new FirestoreDocument.Id(task.getId()));
                        Log.d(TAG, "Experiment Added.");
                        onComplete.callBack(experiment);
                    })
                    .addOnFailureListener((e -> {
                        Log.e(TAG, "Failed add experiment: " + e.toString());
                        onFailure.callBack(e);
                    }));
        }
    }


    /**
     * Set listener for new experiments
     */
    private static void initializeExperiments() {
        experimentArrayList = new ArrayList<>();
        experimentsRef = db.collection(EXPERIMENTS);
        experimentListener = experimentsRef.addSnapshotListener((snapshot, e) -> {
            parseExperimentsSnapshot(snapshot);
            Log.d(TAG, "Updated Experiments.");
            if (updateCallback != null) updateCallback.callBack();
        });
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
    private static <ClassType extends FirestoreDocument> ClassType readFirebaseObjectSnapshot(Class<ClassType> typeClass, DocumentSnapshot snapshot) {
        ClassType object = snapshot.toObject(typeClass);
        if (object != null) ((FirestoreDocument) object).setId(new FirestoreDocument.Id(snapshot.getId()));
        else Log.e(TAG, "readFirebaseObjectSnapshot returned null");
        return object;
    }

    /**
     * Parses an experiments list snapshot and stores the experiments in experimentArrayList.
     * @param experimentsSnapshot
     *      The snapshot from firestore to parse
     */
    private static void parseExperimentsSnapshot(QuerySnapshot experimentsSnapshot) {
        ArrayList<Experiment> array = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : experimentsSnapshot) {
            Class<? extends Experiment> classType = experimentClassMap.get(snapshot.getString("type"));
            if (classType != null) array.add(readFirebaseObjectSnapshot(classType, snapshot));
            else Log.e(TAG, "classType null in parseExperimentsSnapshot.");
        }
        experimentArrayList = array;
    }
}
