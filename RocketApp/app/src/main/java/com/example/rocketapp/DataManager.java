package com.example.rocketapp;
import android.app.Activity;
import android.provider.ContactsContract;
import android.util.Log;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Handles all interaction between the application and firestore.
 * Keeps firestore synced user list, experiments list, and current user.
 * Handles user login and account creation, and adding/modifying/deleting experiments, trials, and questions.
 */
public class DataManager {
    private static String TAG = "DataManager";
    private static User user;
    private static ArrayList<User> userArrayList;
    private static ArrayList<FirestoreDocument.Id> subscriptions;
    private static ArrayList<Experiment> experimentArrayList;

    private static final FirebaseFirestore db;
    private static CollectionReference experimentsRef, usersRef;
    private static ListenerRegistration subscriptionsListener, usersListener, experimentsListener;
    private static ArrayList<ListenerRegistration> experimentListeners = new ArrayList<>();
    private static Callback updateCallback;

    // Collection names
    private static final String SUBSCRIPTIONS = "Subscriptions";
    private static final String EXPERIMENTS = "Experiments";
    private static final String COMMENTS = "Comments";
    private static final String USERS = "Users";
    private static final String TRIALS = "Trials";

    // TODO Add any new Experiment types to this map
    static final ImmutableMap<String, Class<? extends Experiment>> experimentClassMap = ImmutableMap.<String, Class<? extends Experiment>>builder()
            .put(IntCountExperiment.TYPE, IntCountExperiment.class)
            .put(CountExperiment.TYPE, CountExperiment.class)
            .put(BinomialExperiment.TYPE, BinomialExperiment.class)
            .put(MeasurementExperiment.TYPE, MeasurementExperiment.class)
            .build();

    // TODO Add any new Trial types to this map
    static final ImmutableMap<String, Class<? extends Trial>> trialClassMap = ImmutableMap.<String, Class<? extends Trial>>builder()
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


    /**
     * A class object that will be stored on firestore. Contains information relating to its own documentId, as well
     * as the documentId of an object that "owns" this object.
     * Subclassed in DataManager since id's must be retrieved through firestore, and so should only be set in DataManager.
     */
    public abstract static class FirestoreDocument  {
        /**
         * Id represents a documentId in firestore for finding and referencing documents.
         * Private class creates "Friend" like functionality so function calls requiring a new ID can only be called
         * from DataManager to make sure updates will be synced with firestore.
         */
        final private static class Id implements Serializable {
            private String key;

            /**
             * Default constructor only used for interface with Firestore.
             */
            public Id() {}

            /**
             * Generates a new DocumentId. Private so new documentId's can only be created by DataManager.
             * @param id documentId string from firestore
             */
            private Id(String id) {
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

        private Id id;          // The firestore documentId for this object

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
        private void setId(Id id) {
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


    public abstract static class FirestoreOwnableDocument extends FirestoreDocument {

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
        private void setOwnerId(FirestoreDocument.Id id) {
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
            return getUser(ownerId);
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

    public abstract static class FirestoreNestableDocument extends FirestoreOwnableDocument {
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
        private void setParent(FirestoreDocument.Id id) {
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


    // Private to prevent instantiation
    private DataManager() { }

    public interface Type {
        String getType();
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

    public interface AnswerCallback {
        void callBack(Answer answer);
    }

    public interface CommentCallback {
        void callBack(Comment comment);
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

    public interface ExperimentSearch {
        boolean match(Experiment experiment);
    }


    /**
     * Set callback for when Experiments are updated from firestore. Should use to update listviews of experiments.
     * @param callback
     *      Callback for when experiments are updated from firestore.
     */
    public static void setUpdateCallback(Callback callback) {
        updateCallback = callback;
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
     * @param searchWords
     *      String keywords to search for
     * @return
     *      filtered list of experiments
     */
    public static ArrayList<Experiment> getExperimentArrayList(String searchWords, boolean includeSubscribed, boolean includeOwned) {
        String[] words = searchWords.split(" ");
        ArrayList<FirestoreDocument.Id> ignored = new ArrayList<>();
        if (!includeSubscribed)
            ignored.addAll(subscriptions);
        for(Experiment experiment: experimentArrayList){
            if (experiment.getState().equals(Experiment.State.UNPUBLISHED)) {
                ignored.add(experiment.getId());
            }
        }

        return getExperimentArrayList(experiment -> {
            if (ignored.contains(experiment.getId())) return false;
            System.out.println(experiment.getOwnerId().key + " " + user.getId().key);
            if (!includeOwned && experiment.getOwnerId().equals(user.getId())) {
                System.out.println("false");
                return false;
            }

            for (String word : words)
                if (!experiment.searchString().toLowerCase().contains(word.toLowerCase()))
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
    public static ArrayList<Experiment> getExperimentArrayList(ExperimentSearch find) {
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
            if (experiment.isValid() && experiment.getOwnerId().equals(user.getId()))
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
        return getExperimentArrayList(experiment -> !subscriptions.contains(experiment.getId()));
    }

    /**
     * Gets all experiments subscribed to by current user
     * @return
     *      list of subscribed experiments
     */
    public static ArrayList<Experiment> getSubscribedExperimentArrayList() {
        ArrayList<Experiment> filteredExperiments = new ArrayList<>();

        for (FirestoreDocument.Id id : subscriptions) {
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


//    /**
//     * Retrieves an experiment from the list of experiments.
//     * Should pass an ID through an intent and use this to get an experiment in ExperimentActivity.
//     * @param id
//     *      ID of the experiment to retrieve
//     * @return
//     *      Returns the experiment object matching the ID
//     */
//    public static Experiment getExperiment(FirestoreDocument.Id id) {
//        for (Experiment experiment : experimentArrayList)
//            if (experiment.isValid() && experiment.getId().equals(id))
//                return experiment;
//        Log.e(TAG, "getExperiment() Experiment not found");
//        return null;
//    }

    public static Experiment getExperiment(Object id) {
//        FirestoreDocument.Id documentId = (FirestoreDocument.Id) id;
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
    public static void publishExperiment(Experiment experiment, ExperimentCallback onSuccess, ExceptionCallback onFailure) {
        if (user == null || !user.isValid()) {
            Log.e(TAG, "publishExperiment() Failed. User must be signed in to publish experiment.");
            onFailure.callBack(new Exception("Publish Experiment Failed. User must be signed in to publish experiment."));
            return;
        }

        if (experiment == null) {
            Log.e(TAG, "publishExperiment() Failed. Experiment was null.");
            onFailure.callBack(new Exception("Publish Experiment Failed. Experiment was null."));
            return;
        }

        ((FirestoreOwnableDocument) experiment).setOwnerId(user.getId());
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
    public static void unpublishExperiment(Experiment experiment, ExperimentCallback onSuccess, ExceptionCallback onFailure) {
        if (user == null || !user.isValid()) {
            Log.e(TAG, "User not logged in. Cannot un-publish experiment");
            onFailure.callBack(new Exception("User not logged in. Cannot un-publish experiment"));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Invalid experiment. Cannot un-publish.");
            onFailure.callBack(new Exception("Invalid experiment. Cannot un-publish experiment"));
            return;
        }

        if (!experiment.getOwnerId().equals(user.getId())) {
            Log.e(TAG, "User (" + user.getId().key + ") does not own this experiment " + experiment.getOwnerId().getKey() + ". Cannot un-publish.");
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
    public static void endExperiment(Experiment experiment, ExperimentCallback onSuccess, ExceptionCallback onFailure) {
        if (user == null || !user.isValid()) {
            Log.e(TAG, "User not logged in. Cannot end experiment");
            onFailure.callBack(new Exception("User not logged in. Cannot end experiment"));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Invalid experiment. Cannot end.");
            onFailure.callBack(new Exception("Invalid experiment. Cannot end."));
            return;
        }

        if (!experiment.getOwnerId().equals(user.getId())) {
            Log.e(TAG, "User (" + user.getId().key + ") does not own this experiment " + experiment.getOwnerId().getKey() + ". Cannot end.");
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
    public static void listen(Experiment experiment, ExperimentCallback onUpdate) {
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

        // Listen for changes in Trials
        CollectionReference trialsRef = experimentsRef.document(documentId).collection(TRIALS);
        experimentListeners.add(trialsRef.addSnapshotListener((snapshots, e) -> {
            parseTrialsSnapshot(experiment, snapshots);
            Log.d(TAG, "Experiment Trials Updated: " + experiment.getTrials().size());
            onUpdate.callBack(experiment);
        }));

        // Listen for changes in Questions
        CollectionReference commentsRef = experimentsRef.document(documentId).collection(COMMENTS);
        experimentListeners.add(commentsRef.addSnapshotListener((snapshots, e) -> {
            parseCommentsSnapshot(experiment, snapshots);
            Log.d(TAG, "Experiment Questions Updated: " + experiment.getQuestions().size());
            onUpdate.callBack(experiment);
        }));

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
                    Log.d(TAG, "Subscribed to experiment: " + experiment.getId().toString());
                    onSuccess.callBack();
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Subscribe failed: " + e.toString());
                    onFailure.callBack(e);
                });
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
    public static void update(Experiment experiment, ExperimentCallback onSuccess, ExceptionCallback onFailure) {
        push(experiment, onSuccess, onFailure);
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
                Log.e(TAG, "CreateUser failed: Username not available");
                onFailure.callBack(new Exception("Username not available"));
            } else {
                push(new User(userName), (user) -> {
                    listen(user);
                    onSuccess.callBack(user);
                }, onFailure);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "CreateUser failed: " + e.toString());
            onFailure.callBack(e);
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
                            pullSubscriptions(()-> {
                                Log.d(TAG, "Login successful: " + user.toString());
                                onSuccess.callBack(user);
                                }, onFailure);
                        }
                        else {
                            Log.e(TAG, "Login failed: readFirebaseObjectSnapshot returned null.");
                            onFailure.callBack(new Exception("readFirebaseObjectSnapshot returned null"));
                        }
                    } else {
                        Log.e(TAG, "Login failed: User not found.");
                        onFailure.callBack(new Exception("User not found"));
                    }})
                .addOnFailureListener((e) -> {
                    Log.e(TAG, "Login failed: " + e.toString());
                    onFailure.callBack(e);
        });
    }


    /**
     * Sync user info to firebase
     * @param onSuccess
     *      Callback for when update is successful
     * @param onFailure
     *      Callback for when update fails
     */
    public static void updateUser(UserCallback onSuccess, ExceptionCallback onFailure) {
        push(user, onSuccess, onFailure);
    }


    /**
     * Retrieve a user from an ID
     * @param id
     *      ID of user to retrieve user data for.
     * @return
     *      User object matching id
     */
    public static User getUser(FirestoreDocument.Id id) {
            for (User user : userArrayList)
                if (user.getId().equals(id))
                    return user;
        Log.e(TAG, "getUser() User not found.");
        return new User("User not found.");
    }


    //** Trials **/

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
    public static void addTrial(Trial trial, Experiment experiment, TrialCallback onSuccess, ExceptionCallback onFailure) {
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
    public static void update(Trial trial, Experiment experiment, TrialCallback onSuccess, ExceptionCallback onFailure) {
        push(trial, experiment, onSuccess, onFailure);
    }


    //** Questions **/

    /**
     * Add a question to an experiment.
     * @param question
     *      The question object
     * @param experiment
     *      The experiment to add the question to
     * @param onSuccess
     *      Callback for when push successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void addQuestion(Question question, Experiment experiment, Callback onSuccess, ExceptionCallback onFailure) {
        ((FirestoreOwnableDocument) question).setOwnerId(user.getId());
        ((FirestoreNestableDocument) question).setParent(experiment.getId());
        push(question, experiment, onSuccess, onFailure);

    }

    /**
     * Update a question.
     * @param question
     *      The question object
     * @param experiment
     *      The experiment the question belongs to
     * @param onSuccess
     *      Callback for when push successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void update(Question question, Experiment experiment, Callback onSuccess, ExceptionCallback onFailure) {
        if (!question.getOwnerId().equals(user.getId())) {
            onFailure.callBack(new Exception("Cannot update question. Not owned by user."));
        }
        addQuestion(question, experiment, onSuccess, onFailure);
    }

    /**
     * Add an answer to a question.
     * @param answer
     *      The answer object
     * @param question
     *      The question the answer belongs to
     * @param onSuccess
     *      Callback for when push successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void addAnswer(Answer answer, Question question, Callback onSuccess, ExceptionCallback onFailure) {
        ((FirestoreOwnableDocument) answer).setOwnerId(user.getId());
        ((FirestoreNestableDocument) answer).setParent(question.getId());
        push(answer, getExperiment(question.getParentId()), onSuccess, onFailure);
    }


    /**
     * Update an answer.
     * @param answer
     *      The answer object
     * @param question
     *      The question the answer belongs to
     * @param onSuccess
     *      Callback for when push successful
     * @param onFailure
     *      Callback for when push fails
     */
    public static void update(Answer answer, Question question, Callback onSuccess, ExceptionCallback onFailure) {
        if (!answer.getOwnerId().equals(user.getId())) {
            onFailure.callBack(new Exception("Cannot update answer. Not owned by user."));
        } else {
            addAnswer(answer, question, onSuccess, onFailure);
        }
    }



    //** Private **/


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


    private static void pullAllFromCollection(User user, CollectionReference collectionReference, String field, StringArrayCallback onComplete) {
        if (!user.isValid()) return;

        collectionReference.get().addOnCompleteListener(task -> {
            ArrayList<String> subscriptions = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                subscriptions.add(snapshot.getString(field));
            }

            onComplete.callBack(subscriptions);
        });
    }


    /**
     * Sets listener for new user subscriptions and updates the subscriptions list when it's state changes in firestore.
     * @param user
     *      User to listen to
     */
    private static void listen(User user) {
        if (subscriptionsListener != null) subscriptionsListener.remove();

        subscriptionsListener = usersRef.document(user.getId().getKey()).collection(SUBSCRIPTIONS).addSnapshotListener((snapshots, e) -> {
            ArrayList<FirestoreDocument.Id> subscriptionsList = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : snapshots)
                subscriptionsList.add(snapshot.toObject(FirestoreDocument.Id.class));
            subscriptions = subscriptionsList;
            Log.d(TAG, "Subscriptions updated");
            if (updateCallback != null) updateCallback.callBack();
        });
    }

    /**
     * Set listener for new experiments
     */
    private static void initializeExperiments() {
        experimentArrayList = new ArrayList<>();
        experimentsRef = db.collection(EXPERIMENTS);
        experimentsListener = experimentsRef.addSnapshotListener((snapshot, e) -> {
            parseExperimentsSnapshot(snapshot);
            Log.d(TAG, "Updated Experiments.");
            if (updateCallback != null) updateCallback.callBack();
        });
    }

    /**
     * Set listener for new users
     */
    private static void initializeUsers() {
        userArrayList = new ArrayList<>();
        subscriptions = new ArrayList<>();
        usersRef = db.collection(USERS);
        usersListener = usersRef.addSnapshotListener((snapshot, e) -> {
            parseUsersSnapshot(snapshot);
            Log.d(TAG, "Updated Users.");
            if (updateCallback != null) updateCallback.callBack();
        });
    }


    /**
     * Parses users list snapshot from firestore and stores them in the userArrayList.
     * @param userSnapshots
     *      The snapshot from firestore to parse
     */
    private static void parseUsersSnapshot(QuerySnapshot userSnapshots) {
        ArrayList<User> users = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots)
            users.add(readFirebaseObjectSnapshot(User.class, snapshot));

        userArrayList = users;
    }

    /**
     * Parses a trials list snapshot from firestore and adds them to an experiment.
     * @param experiment
     *      The experiment the trials belong to
     * @param userSnapshots
     *      The snapshot from firestore to parse
     */
    private static void parseTrialsSnapshot(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Trial> array = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots) {
            Class<? extends Trial> classType = trialClassMap.get(snapshot.getString("type"));
            if (classType != null) array.add(readFirebaseObjectSnapshot(classType, snapshot));
            else Log.e(TAG, "classType null in parseTrialsSnapshot.");
        }

        experiment.setTrials(array);
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

    /**
     * Parses a comment list snapshot from firestore into questions and answers and adds them to an experiment.
     * @param experiment
     *      The experiment the comments are for
     * @param userSnapshots
     *      The snapshot from firestore to parse
     */
    private static void parseCommentsSnapshot(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Answer> answersArrayList = new ArrayList<>();
        ArrayList<Question> questionsArrayList = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots) {
            System.out.println(snapshot.toString());
            String type = snapshot.getString("type");
            if (type.equals(Question.TYPE)) {
                questionsArrayList.add(readFirebaseObjectSnapshot(Question.class, snapshot));
            } else if (type.equals(Answer.TYPE)) {
                answersArrayList.add(readFirebaseObjectSnapshot(Answer.class, snapshot));
            } else {
                Log.e(TAG, "parseCommentsSnapshot: Bad class.");
            }

            for (Question question : questionsArrayList) {

                ArrayList<Answer> answers = new ArrayList<>();
                for (Answer answer : answersArrayList) {
                    if (answer.getParentId().equals(question.getId())) {
                        answers.add(answer);
                    }
                }
                question.setAnswers(answers);
            }
        }

        experiment.setQuestions(questionsArrayList);
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
            usersRef.document(user.getId().getKey()).set(user)
                    .addOnSuccessListener(u -> {
                       onSuccess.callBack(user);
                    });
        } else {
            usersRef.add(user)
                    .addOnSuccessListener(u -> {
                        ((FirestoreDocument) user).setId(new FirestoreDocument.Id(u.getId()));
                        Log.d(TAG, "User created.");
                        if (onSuccess != null) onSuccess.callBack(user); })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to create user: " + e.toString());
                        if (onFailure != null) onFailure.callBack(e);
                    });
        }
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
    private static void push(Experiment experiment, ExperimentCallback onComplete, ExceptionCallback onFailure) {
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
    private static void push(Trial trial, Experiment experiment, TrialCallback onComplete, ExceptionCallback onFailure) {
        if (user == null || user.getId() == null || !user.getId().isValid()) {
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

        ((FirestoreOwnableDocument) trial).setOwnerId(user.getId());
        ((FirestoreNestableDocument) trial).setParent(user.getId());
        CollectionReference trialsRef = experimentsRef.document(experiment.getId().getKey()).collection(TRIALS);

        if (trial.getId() != null) {
            trialsRef.document(trial.getId().getKey()).set(trial).addOnSuccessListener(trialSnapshot -> {
                Log.d(TAG, "Trial Updated.");
                onComplete.callBack(trial);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to update Trial.");
                onFailure.callBack(e);
            });
        } else {
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

    /**
     * Add or modify a comment on firestore
     * @param comment
     *      The comment to sync
     * @param experiment
     *      The experiment to comment on
     * @param onSuccess
     *      Callback for when comment is pushed successfully
     * @param onFailure
     *      Callback for when comment push fails
     */
    private static void push(Comment comment, Experiment experiment, Callback onSuccess, ExceptionCallback onFailure) {
        if (comment == null || !comment.parentIsValid()) {
            Log.e(TAG, "Push failed. Tried to add null or un-parented comment.");
            onFailure.callBack(new Exception("Push failed. Tried to add null or un-parented comment."));
            return;
        }

        if (user == null || user.getId() == null || !user.getId().isValid())
        {
            Log.e(TAG, "Push failed. Must be logged in to push comment.");
            onFailure.callBack(new Exception("Push failed. Must be logged in to push comment."));
            return;
        }

        ((FirestoreOwnableDocument) comment).setOwnerId(user.getId());
        CollectionReference commentsRef = experimentsRef.document(experiment.getId().getKey()).collection(COMMENTS);

        if (comment.getId() != null) {
            commentsRef.document(comment.getId().getKey()).set(comment).addOnSuccessListener(questionSnapshot -> {
                Log.d(TAG, "Comment Updated.");
                onSuccess.callBack();
            }).addOnFailureListener(e->{
                Log.e(TAG, "Failed to update comment: " + e.toString());
                onFailure.callBack(e);
            });
        } else {
            commentsRef.add(comment).addOnCompleteListener(task -> {
                ((FirestoreDocument) comment).setId(new FirestoreDocument.Id(task.getResult().getId()));
                Log.d(TAG, "Comment Added.");
                onSuccess.callBack();
            }).addOnFailureListener(e->{
                Log.e(TAG, "Failed to add comment: " + e.toString());
                onFailure.callBack(e);
            });
        }
    }

    /**
     * Pulls the subscription id's for the current user from firestore.
     * @param onSuccess
     *      Callback for when subscriptions are pulled from firestore successfuly
     * @param onFailure
     *      Callback for when pullSubscriptions fails
     */
    private static void pullSubscriptions(Callback onSuccess, ExceptionCallback onFailure) {
        usersRef.document(user.getId().getKey()).collection(SUBSCRIPTIONS).get().addOnCompleteListener((subs) -> {
            ArrayList<FirestoreDocument.Id> subscriptionsList = new ArrayList<>();
            for (QueryDocumentSnapshot sub : subs.getResult())
                subscriptionsList.add(sub.toObject(FirestoreDocument.Id.class));
            subscriptions = subscriptionsList;
            Log.d(TAG, "Subscriptions Updated.");
            for (FirestoreDocument.Id id : subscriptions) {
                System.out.println(id.key);
            }
            onSuccess.callBack();
        }).addOnFailureListener(e->{
            Log.e(TAG, "Failed to update subscriptions: " + e.toString());
            onFailure.callBack(e);
        });
    }
}
