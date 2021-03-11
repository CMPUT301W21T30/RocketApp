package com.example.rocketapp;
import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * Handles all interaction between the application and firestore.
 * Keeps firestore synced user list, experiments list, and current user.
 * Handles user login and account creation, and adding/modifying/deleting experiments, trials, and questions.
 */
public class DataManager {
    private static User user;
    private static ArrayList<User> userArrayList;
    private static ArrayList<ID> subscriptions;
    private static ArrayList<Experiment> experimentArrayList;

    private static final FirebaseFirestore db;
    private static CollectionReference experimentsRef;
    private static CollectionReference usersRef;
    private static ListenerRegistration
            subscriptionsListener,
            usersListener,
            experimentsListener,
            experimentListener,
            trialsListener,
            questionsListener;

    // Collection names
    private static final String SUBSCRIPTIONS = "Subscriptions";
    private static final String EXPERIMENTS = "Experiments";
    private static final String QUESTIONS = "Questions";
    private static final String USERS = "Users";
    private static final String TRIALS = "Trials";

    // TODO Add any new Experiment types to this map
    private static final ImmutableMap<String, Class<? extends Experiment>> experimentClassMap = ImmutableMap.<String, Class<? extends Experiment>>builder()
            .put(IntCountExperiment.TYPE, IntCountExperiment.class)
            .put(CountExperiment.TYPE, CountExperiment.class)
            .put(BinomialExperiment.TYPE, BinomialExperiment.class)
            .put(MeasurementExperiment.TYPE, MeasurementExperiment.class)
            .build();

    // TODO Add any new Experiment types to this map
    private static final ImmutableMap<String, Class<? extends Trial>> trialClassMap = ImmutableMap.<String, Class<? extends Trial>>builder()
            .put(IntCountTrial.TYPE, IntCountTrial.class)
            .put(MeasurementTrial.TYPE, MeasurementTrial.class)
            .put(BinomialTrial.TYPE, BinomialTrial.class)
            .put(CountTrial.TYPE, CountTrial.class)
            .build();


    static {
        db = FirebaseFirestore.getInstance();
        initializeExperiments();
        initializeUsers();
    }


    // Private to prevent instantiation
    private DataManager() { }


    /**
     * ID class, Creates "Friend" like functionality so function calls requiring a new ID can only be called
     * from this class to make sure updates will be synced with firestore.
     */
    public static class ID {
        private String key;

        public ID() {}

        private ID(String id) {
            this.key = id;
        }

        /**
         * Gets the key corresponding to the documentId in firestore.
         * @return
         *      string corresponding to the documentId in firestore.
         */
        public String getKey() {
            return key;
        }

        /**
         * Returns true if it has a valid key. Will only be true for objects pulled from firestore.
         * @return
         *      true if has valid key
         */
        @Exclude
        public boolean isValid() {
            return key != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id = (ID) o;
            return this.key.equals(id.key);
        }
    }


    public interface Callback {
        void callBack();
    }

    public interface ExceptionCallback {
        void callBack(Exception e);
    }

    public interface ExperimentsCallback {
        void callBack(ArrayList<Experiment> experiments);
    }

    public interface ExperimentCallback {
        void callBack(Experiment experiment);
    }

    public interface QuestionCallback {
        void callBack(Question question);
    }

    public interface TrialCallback {
        void callBack(Trial trial);
    }

    public interface StringArrayCallback {
        void callBack(ArrayList<String> experiments);
    }

    public interface UserCallback {
        void callBack(User user);
    }


    /**
     * Get the current logged in user
     * @return
     *      The current user
     */
    public static User getUser() {
        return user;
    }


    /**
     * Gets the current list of all users
     * @return
     *      Array of users
     */
    public static ArrayList<User> getUserArrayList() {
        return userArrayList;
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
     * @param filter
     *      String keyword to search for
     * @return
     *      filtered list of experiments
     */
    public static ArrayList<Experiment> getExperimentArrayList(String filter) {
        ArrayList<Experiment> filteredExperiments = new ArrayList<>();

        for (Experiment experiment : experimentArrayList) {
            if (experiment.info.contains(filter))
                filteredExperiments.add(experiment);
        }

        return experimentArrayList;
    }


    /**
     * Gets all experiments owned by current user
     * @return
     *      list of owned experiments
     */
    public static ArrayList<Experiment> getOwnedExperimentsArrayList() {
        ArrayList<Experiment> filteredExperiments = new ArrayList<>();

        for (Experiment experiment : experimentArrayList) {
            if (experiment.info.getOwner().getKey().equals(user.getId().getKey()))
                filteredExperiments.add(experiment);
        }

        return filteredExperiments;
    }


    /**
     * Gets all experiments subscribed to by current user
     * @return
     *      list of subscribed experiments
     */
    public static ArrayList<Experiment> getSubscribedExperimentArrayList() {
        ArrayList<Experiment> filteredExperiments = new ArrayList<>();

        for (Experiment experiment : experimentArrayList) {
            if (experiment.info.getOwner() == user.getId())
                filteredExperiments.add(experiment);
        }

        return filteredExperiments;
    }


    /**
     * Retrieves an experiment from the list of experiments.
     * Should pass an ID through an intent and use this to get an experiment in ExperimentActivity.
     * @param id
     *      ID of the experiment to retrieve
     * @return
     *      Returns the experiment object matching the ID
     */
    public static Experiment getExperiment(ID id) {
        for (Experiment experiment : experimentArrayList)
            if (experiment.getId().equals(id))
                return experiment;
        Log.e(TAG, "getExperiment() Experiment not found");
        return null;
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
    public static void push(Experiment experiment, ExperimentCallback onComplete, ExceptionCallback onFailure) {
        DataManager.ID id = experiment.getId();
        if (id != null && id.isValid()) {
            experimentsRef.document(experiment.getId().getKey()).set(experiment);;
        } else {
            experimentsRef.add(experiment)
                    .addOnSuccessListener(task -> {
                        experiment.setId(new ID(task.getId()));
                        if (onComplete != null) onComplete.callBack(experiment);
                    })
                    .addOnFailureListener((e -> {
                        if (onFailure != null) onFailure.callBack(e);
                    }));
        }
    }


    /**
     * Publish a new experiment
     * @param experiment
     *      Experiment to publish
     * @param onComplete
     *      Callback for when successful
     * @param onFailure
     *      Callback for failure
     */
    public static void publishExperiment(Experiment experiment, ExperimentCallback onComplete, ExceptionCallback onFailure) {
        if (user == null || !user.isValid()) {
            if (onFailure != null)
                onFailure.callBack(new Exception("Publish Experiment Failed. Must be signed in to publish experiment"));
            return;
        }

        experiment.setOwner(user.getId());
        experiment.info.setOwner(user.getId());

        if (experiment.info.getOwner() == null) {
            if (onFailure != null)
                onFailure.callBack(new Exception("Owner does not have id. Cannot publish experiment"));
            return;
        }

        push(experiment, onComplete, onFailure);
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
    public static void unpublishExperiment(Experiment experiment, Callback onSuccess, ExceptionCallback onFailure) {
        if (experiment == null) {
            Log.e(TAG, "unpublishExperiment called with null experiment.");
            onFailure.callBack(new Exception("unpublishExperiment called with null experiment."));
            return;
        }

        if (user == null || experiment.info.getOwner() != user.getId()) {
            Log.e(TAG, "User does not have permission to end this experiment. Not logged in, or doesn't own it.");
            onFailure.callBack(new Exception("User does not have permission to end this experiment. Not logged in, or doesn't own it."));
            return;
        }

        experimentsRef.document(experiment.getId().getKey()).delete().addOnSuccessListener(aVoid -> {
            onSuccess.callBack();
        }).addOnFailureListener((e -> onFailure.callBack(e)));
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
    public static void endExperiment(Experiment experiment, ExperimentCallback onSuccess, ExceptionCallback onFailure) {

        if (user == null) {
            Log.e(TAG, "Not logged in, cannot end experiment");
            onFailure.callBack(new Exception("Not logged in, cannot end experiment"));
            return;
        }

        if (experiment == null || experiment.info.getOwner() != user.getId()) {
            Log.e(TAG, "User does not own this experiment. Cannot end experiment.");
            onFailure.callBack(new Exception("User does not own this experiment. Cannot end experiment."));
            return;
        }

        experiment.endExperiment(user);
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
    public static void listen(Experiment experiment, ExperimentCallback onUpdate) {
        if (!experiment.isValid()) {
            Log.e(TAG, "Cannot listen to an experiment without an id.");
            return;
        }

        String documentId = experiment.getId().getKey();

        // Listen for changes to experiment info
        if (experimentListener != null) experimentListener.remove();
        experimentListener = experimentsRef.document(documentId).addSnapshotListener((snapshot, e) -> {
            Class<? extends Experiment> classType = experimentClassMap.get(snapshot.getString("type"));
            Experiment updatedExperiment = readFirebaseObjectSnapshot(classType, snapshot);
            if (updatedExperiment != null) experiment.info = updatedExperiment.info;
            else Log.e(TAG, "classType null in listen");

            onUpdate.callBack(experiment);
        });

        // Listen for changes in Trials
        CollectionReference trialsRef = experimentsRef.document(documentId).collection(TRIALS);
        if (trialsListener != null) trialsListener.remove();
        trialsListener = trialsRef.addSnapshotListener((snapshots, e) -> {
                    updateTrials(experiment, snapshots);
                    Log.d(TAG, "Pulled Trials: " + snapshots.size());
                });

        // Listen for changes in Questions
        CollectionReference questionsRef = experimentsRef.document(documentId).collection(QUESTIONS);
        if (questionsListener != null) questionsListener.remove();
        questionsListener = questionsRef.addSnapshotListener((snapshots, e) -> {
                    updateQuestions(experiment, snapshots);
                    Log.d(TAG, "Pulled Questions: " + snapshots.size());
                });
    }


    /**
     * Subscribe to an experiment. Subscribed experiments can be retrieved with getSubscribedExperimentArrayList().
     * @param experiment
     *      The experiment to subscribe the current user to
     * @param onSuccess
     *      Callback for when subscribing is successful
     * @param onFailure
     *      Callback for when subscribing fails
     */
    public static void subscribe(Experiment experiment, Callback onSuccess, ExceptionCallback onFailure) {
        if (user == null || !user.isValid()) {
            Log.d(TAG, "Failed to subscribe. User must be logged in to subscribe to an experiment.");
            if (onFailure != null) onFailure.callBack(new Exception("Failed to subscribe. User must be logged in to subscribe to an experiment."));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.d(TAG, "Failed to subscribe. Experiment does not have id.");
            if (onFailure != null) onFailure.callBack(new Exception("Failed to subscribe. Experiment does not have id."));
            return;
        }

        if (subscriptions.contains(experiment.getId())) {
            Log.d(TAG, "Already subscribed to this experiment.");
            if (onFailure != null) onFailure.callBack(new Exception("Already subscribed to this experiment."));
            return;
        }

        usersRef.document(user.getId().getKey()).collection(SUBSCRIPTIONS).add(experiment.getId())
                .addOnSuccessListener(task -> {
                    if (onSuccess != null) onSuccess.callBack();
                }).addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.callBack(e);
                });
    }


    /**
     * Creates a new user and logs in.
     * @param userName
     *      Username for new user
     * @param onSuccess
     *      Callback for when user creation is successful
     * @param onFailure
     *      Callback for when username already exists fails
     */
    public static void createUser(String userName, UserCallback onSuccess, ExceptionCallback onFailure) {
        usersRef.whereEqualTo("name", userName).get().addOnSuccessListener(matchingUserNames -> {
            if (matchingUserNames.size() > 0) {
                onFailure.callBack(new Exception("Username not available"));
            } else {
                push(new User(userName), (user) -> {
                    listen(user);
                    onSuccess.callBack(user);
                }, onFailure);
            }
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.callBack(e);
        });
    }


    /**
     * Login to user account with user name.
     * @param userName
     *      Username for new user
     * @param onSuccess
     *      Callback for when login is successful
     * @param onFailure
     *      Callback for when login fails (username does not exist)
     */
    public static void login(String userName, UserCallback onSuccess, ExceptionCallback onFailure) {
        usersRef.whereEqualTo("name", userName).get()
                .addOnSuccessListener(task -> {
                    if (task.getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getDocuments().get(0);
                        user = readFirebaseObjectSnapshot(User.class, snapshot);
                        if (user != null) {
                            listen(user);  // Listen to subscriptions
                            Log.d(TAG, "Login successful: " + user.toString());
                        }
                        else if (onFailure != null) {
                            onFailure.callBack(new Exception("readFirebaseObjectSnapshot returned null"));
                            return;
                        };
                        if (onSuccess != null) onSuccess.callBack(user);
                    } else {
                        if (onFailure != null) onFailure.callBack(new Exception("User not found"));
                    }})
                .addOnFailureListener((exception) -> {
                    if (onFailure != null) onFailure.callBack(exception);
        });
    }


    /**
     * Adds or updates a user in firebase.
     * @param user
     *      The user class to update or store in firestore.
     * @param onSuccess
     *      Callback for when successful
     * @param onFailure
     *      Callback for when push fails
     */
    private static void push(User user, UserCallback onSuccess, ExceptionCallback onFailure) {
        if (user.isValid()) {
            usersRef.document(user.getId().getKey()).set(user);
        } else {
            usersRef.add(user)
                    .addOnSuccessListener(u -> {
                        user.setId(new ID(u.getId()));
                        if (onSuccess != null) onSuccess.callBack(user); })
                    .addOnFailureListener(e -> {
                        if (onFailure != null) onFailure.callBack(e);
                    });
        }
    }


    /**
     * Retrieve a user from an ID
     * @param id
     *      ID of user to retrieve user data for.
     * @return
     *      User object matching id
     */
    public static User getUser(ID id) {
            for (User user : userArrayList)
                if (user.getId().equals(id))
                    return user;
        Log.e(TAG, "getUser() User not found.");
        return new User();
    }


    /**
     * Add a new trial for an experiment.
     * @param trial
     *      New trial to add.
     * @param experiment
     *      Experiment to add trial to.
     * @param onComplete
     *      Callback for when push is successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void addTrial(Trial trial, Experiment experiment, TrialCallback onComplete, ExceptionCallback onFailure) {
        push(trial, experiment, onComplete, onFailure);
    }









    //** Questions **/

    /**
     * Add a question.
     * @param question
     *      The question object
     * @param experiment
     *      The experiment to add the question to
     * @param onComplete
     *      Callback for when push successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void addQuestion(Question question, Experiment experiment, QuestionCallback onComplete, ExceptionCallback onFailure) {
        push(question, experiment, onComplete, onFailure);
    }







    //** Private **/
    private static <ClassType extends FirestoreObject> ClassType readFirebaseObjectSnapshot(Class<ClassType> typeClass, DocumentSnapshot snapshot) {
        ClassType object = snapshot.toObject(typeClass);
        if (object != null) object.setId(new ID(snapshot.getId()));
        else Log.e(TAG, "readFirebaseObjectSnapshot returned null");
        return object;
    }


    private static void pullAllFromCollection(User user, CollectionReference collectionReference, String field, StringArrayCallback onComplete) {
        if (!user.isValid()) return;

        collectionReference.get().addOnCompleteListener(task -> {
            ArrayList<String> subscriptions = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                subscriptions.add(snapshot.getString(field));
            }
            if (onComplete != null) onComplete.callBack(subscriptions);
        });
    }

    private static void listen(User user) {
        if (subscriptionsListener != null) subscriptionsListener.remove();

        subscriptionsListener = usersRef.document(user.getId().getKey()).collection(SUBSCRIPTIONS).addSnapshotListener((snapshots, e) -> {
            ArrayList<ID> subscriptionsList = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : snapshots)
                subscriptionsList.add(snapshot.toObject(ID.class));
            subscriptions = subscriptionsList;
            System.out.println("subscriptions updated");
            for (ID id : subscriptions) {
                System.out.println(id.key);

            }
        });
    }

    private static void initializeExperiments() {
        experimentArrayList = new ArrayList<>();
        experimentsRef = db.collection(EXPERIMENTS);
        experimentsListener = experimentsRef.addSnapshotListener((snapshot, e) -> updateExperiments(snapshot));
    }

    private static void updateExperiments(QuerySnapshot experimentsSnapshot) {
        ArrayList<Experiment> array = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : experimentsSnapshot) {
            Class<? extends Experiment> classType = experimentClassMap.get(snapshot.getString("type"));
            if (classType != null) array.add(readFirebaseObjectSnapshot(classType, snapshot));
            else Log.e(TAG, "classType null in updateExperiments");
        }
        experimentArrayList = array;
    }

    private static void initializeUsers() {
        userArrayList = new ArrayList<>();
        subscriptions = new ArrayList<>();
        usersRef = db.collection(USERS);
        usersListener = usersRef.addSnapshotListener((snapshot, e) -> updateUsers(snapshot));
    }



    private static void updateUsers(QuerySnapshot userSnapshots) {
        ArrayList<User> users = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots)
            users.add(readFirebaseObjectSnapshot(User.class, snapshot));

        userArrayList = users;
    }

    private static void updateTrials(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Trial> array = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots) {
            Class<? extends Trial> classType = trialClassMap.get(snapshot.getString("type"));
            if (classType != null) array.add(readFirebaseObjectSnapshot(classType, snapshot));
            else Log.e(TAG, "classType null in updateTrials");
        }

        experiment.setTrials(array);
    }

    private static void updateQuestions(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Question> questions = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots)
            questions.add(readFirebaseObjectSnapshot(Question.class, snapshot));

        experiment.setQuestions(questions);
    }

    private static void push(Trial trial, Experiment experiment, TrialCallback onComplete, ExceptionCallback onFailure) {
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

        if (user == null || user.getId() == null || !user.getId().isValid()) {
            Log.e(TAG, "Push failed. Must be logged in to push.");
            onFailure.callBack(new Exception("Push failed. Must be logged in to push."));
            return;
        }

        trial.setOwner(user.getId());
        CollectionReference trialsRef = experimentsRef.document(experiment.getId().getKey()).collection(TRIALS);

        if (trial.getId() != null) {
            trialsRef.document(trial.getId().getKey()).set(trial).addOnSuccessListener(trialSnapshot -> {
                if (onComplete != null) onComplete.callBack(trial);
            }).addOnFailureListener(e -> {
                if (onFailure != null) onFailure.callBack(e);
            });
        } else {
            trialsRef.add(trial).addOnSuccessListener(trialSnapshot -> {
                trial.setId(new ID(trialSnapshot.getId()));
                if (onComplete != null) onComplete.callBack(trial);
            }).addOnFailureListener(e -> {
                if (onFailure != null) onFailure.callBack(e);
            });
        }
    }

    private static void push(Question question, Experiment experiment, QuestionCallback onComplete, ExceptionCallback onFailure) {
        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Push failed. Tried to add question to experiment without ID");
            return;
        }

        if (user == null || user.getId() == null || !user.getId().isValid())
        {
            Log.e(TAG, "Push failed. Must be logged in to push.");
            return;
        }

        question.setOwner(user.getId());
        CollectionReference trialsRef = experimentsRef.document(experiment.getId().getKey()).collection(QUESTIONS);

        if (question.getId() != null) {
            trialsRef.document(question.getId().getKey()).set(question).addOnSuccessListener(questionSnapshot -> {
                if (onComplete != null) onComplete.callBack(question);
            }).addOnFailureListener(e -> {
                if (onFailure != null) onFailure.callBack(e);
            });
        } else {
            trialsRef.add(question).addOnCompleteListener(task -> {
                question.setId(new ID(task.getResult().getId()));
                if (onComplete != null) onComplete.callBack(question);
            }).addOnFailureListener(e -> {
                if (onFailure != null) onFailure.callBack(e);
            });
        }
    }
}
