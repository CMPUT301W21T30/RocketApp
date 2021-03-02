package com.example.rocketapp;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DataManager {
    private static User user;
    private static ArrayList<User> userArrayList;
    private static ArrayList<Experiment> ownedExperimentsArrayList;
    private static ArrayList<Experiment> subscribedExperimentArrayList;
    private static ArrayList<Experiment> experimentArrayList;

    private static final FirebaseFirestore db;
    private static CollectionReference experimentsRef;
    private static CollectionReference usersRef;

    // Collection names
    private static final String SUBSCRIPTIONS = "Subscriptions";
    private static final String EXPERIMENTS = "Experiments";
    private static final String QUESTIONS = "Questions";
    private static final String USERS = "Users";
    private static final String TRIALS = "Trials";

    static {
        db = FirebaseFirestore.getInstance();
        initializeExperiments();
        initializeUsers();
    }

    // Creates "Friend" like functionality so function calls requiring a new ID can only be called from this class
    public static class ID {
        private String id;

        public ID() {}

        private ID(String id) {
            this.id = id;
        }

        public String getKey() {
            return id;
        }

        @Exclude
        public boolean isValid() {
            return id != null;
        }

        public boolean equals(ID other) {
            return this.id != null && other != null && this.id.equals(other.id);
        }
    }

    private DataManager() { }

    public static User getUser() {
        return user;
    }

    public static ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public static ArrayList<Experiment> getExperimentArrayList() {
        return experimentArrayList;
    }

    public static ArrayList<Experiment> getOwnedExperimentsArrayList() {
        return ownedExperimentsArrayList;
    }

    public static ArrayList<Experiment> getSubscribedExperimentArrayList() {
        return subscribedExperimentArrayList;
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

    /** Experiments **/

    // Get experiment by id. Should send Id through intent when creating a new ExperimentActivity,
    // then get the experiment with this.
    public static Experiment getExperiment(ID id) {
        for (Experiment experiment : experimentArrayList)
            if (experiment.getId().equals(id))
                return experiment;
        Log.e(TAG, "getExperiment() Experiment not found");
        return new Experiment();
    }

    // Add or sync local experiment to FireStore
    public static void push(Experiment experiment) {
        push(experiment, null, null);
    }

    // Add or sync local experiment to FireStore
    public static void push(Experiment experiment, ExperimentCallback onComplete) {
        push(experiment, onComplete, null);
    }

    // Add or sync local experiment to FireStore
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

    public static void publishExperiment(Experiment experiment) {
        publishExperiment(experiment, null, null);
    }

    public static void publishExperiment(Experiment experiment, ExperimentCallback onComplete) {
        publishExperiment(experiment, onComplete, null);
    }

        // Publish a new experiment
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

        push(experiment, exp -> {
            Map<String, String> data = new HashMap<>();
            data.put("id", exp.getId().getKey());
            usersRef.document(experiment.info.getOwner().getKey()).collection(EXPERIMENTS).add(data);
            if (onComplete != null) onComplete.callBack(exp);
        }, onFailure );
    }

    // End an experiment
    public static void endExperiment(Experiment experiment) {
        endExperiment(experiment, null, null);
    }

    // End an experiment
    public static void endExperiment(Experiment experiment, ExperimentCallback onSuccess) {
        endExperiment(experiment, onSuccess, null);
    }

    // End an experiment
    public static void endExperiment(Experiment experiment, ExperimentCallback onSuccess, ExceptionCallback onFailure) {
        if (user == null || experiment.info.getOwner() != user.getId()) {
            Log.e(TAG, "User does not have permission to end this experiment. Not logged in, or doesn't own it.");
            return;
        }

        experiment.endExperiment(user);
        push(experiment, onSuccess, onFailure);
    }

    // Pull all experiments from firestore
    public static void pullAllExperiments(ExperimentsCallback onSuccess) {
        pullAllExperiments(onSuccess, null);
    }

    // Pull all experiments from firestore
    public static void pullAllExperiments(ExperimentsCallback onSuccess, ExceptionCallback onFailure) {
        experimentsRef.get().addOnSuccessListener(experimentSnapshots -> {
            updateExperiments(experimentSnapshots);
            if (onSuccess != null) onSuccess.callBack(experimentArrayList);
        })
        .addOnFailureListener(e -> {
            if (onFailure != null) onFailure.callBack(e);
        });
    }

    // Call when opening an experiment to get all it's data. This will load and listen for changes to questions and trials for this experiment from FireStore.
    public static void listen(Experiment experiment, ExperimentCallback onUpdate) {
        if (!experiment.isValid()) {
            Log.e(TAG, "Cannot listen to an experiment without an id.");
            return;
        }

        String documentId = experiment.getId().getKey();

        // Listen for changes to experiment info
        experimentsRef.document(documentId).addSnapshotListener((snapshot, e) -> {
            Experiment updatedExperiment = readFirebaseObjectSnapshot(Experiment.class, snapshot);
            experiment.info = updatedExperiment.info;
            onUpdate.callBack(experiment);
        });

        // Listen for changes in Trials
        CollectionReference trialsRef = experimentsRef.document(documentId).collection(TRIALS);
        trialsRef.addSnapshotListener((snapshots, e) -> {
                    setTrials(experiment, snapshots);
                    Log.d(TAG, "Pulled Trials: " + snapshots.size());
                });

        // Listen for changes in Questions
        CollectionReference questionsRef = experimentsRef.document(documentId).collection(QUESTIONS);
        questionsRef.addSnapshotListener((snapshots, e) -> {
                    setQuestions(experiment, snapshots);
                    Log.d(TAG, "Pulled Questions: " + snapshots.size());
                });
    }

    // Subscribe to an experiment
    public static void subscribe(Experiment experiment) {
        subscribe(experiment, null, null);
    }

    // Subscribe to an experiment
    public static void subscribe(Experiment experiment, Callback onSuccess) {
        subscribe(experiment, onSuccess, null);
    }

    // Subscribe to an experiment
    public static void subscribe(Experiment experiment, Callback onSuccess, ExceptionCallback onFailure) {
        if (user == null || !user.isValid()) {
            Log.d(TAG, "Failed to subscribe. User must be logged in to subscribe to an experiment.");
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.d(TAG, "Failed to subscribe. Experiment does not have id.");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("id", experiment.getId().getKey());
        usersRef.document(user.getId().getKey()).collection(SUBSCRIPTIONS).add(data)
                .addOnSuccessListener(task -> {
                    if (onSuccess != null) onSuccess.callBack();
                }).addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.callBack(e);
                });
    }

    // Updates the subscribed experiments list of the user
    public static void pullSubscriptions(ExperimentsCallback onComplete) {
        if (user == null || !user.isValid()) {
            Log.d(TAG, "User must be logged in to call pullSubscriptions()");
            return;
        }

        pullAllFromCollection(user, usersRef.document(user.getId().getKey()).collection(SUBSCRIPTIONS), "id", experimentIds -> {
            if (experimentIds.isEmpty()) {
                Log.d(TAG, "No subscriptions for user found in FireStore.");
                return;
            }
            ArrayList<Experiment> subscriptions = new ArrayList<>();
            experimentsRef.whereIn(FieldPath.documentId(), experimentIds).get()
                    .addOnSuccessListener(experiments -> {
                        for (DocumentSnapshot snapshot : experiments) {
                            subscriptions.add(readFirebaseObjectSnapshot(Experiment.class, snapshot));
                        }
                        subscribedExperimentArrayList = subscriptions;
                        onComplete.callBack(subscribedExperimentArrayList);
                    });
        });
    }

    // Updates the owned experiments list of the user
    public static void pullOwnedExperiments(ExperimentsCallback onComplete) {
        pullOwnedExperiments(onComplete, null);
    }

    // Updates the owned experiments list of the user
    public static void pullOwnedExperiments(ExperimentsCallback onComplete, ExceptionCallback onFailure) {
        if (user == null || !user.isValid()) {
            Log.d(TAG, "User must be logged in to call pullOwnedExperiments()");
            return;
        }

        pullAllFromCollection(user, usersRef.document(user.getId().getKey()).collection(EXPERIMENTS), "id", experimentIds -> {
            if (experimentIds.isEmpty()) {
                Log.d(TAG, "No owned experiments for user found in firestore.");
                return;
            }
            ArrayList<Experiment> experiments = new ArrayList<>();
            experimentsRef.whereIn(FieldPath.documentId(), experimentIds).get()
                    .addOnSuccessListener(experimentSnapshots -> {
                        for (DocumentSnapshot snapshot : experimentSnapshots)
                            experiments.add(readFirebaseObjectSnapshot(Experiment.class, snapshot));
                        ownedExperimentsArrayList = experiments;
                        if (onComplete != null) onComplete.callBack(ownedExperimentsArrayList);
                    }).addOnFailureListener(e -> {
                        if (onFailure != null) onFailure.callBack(e);
            });
        });
    }



    /** Users **/

    // Create a new user and login
    public static void createUser(String userName, UserCallback onSuccess, ExceptionCallback onFailure) {
        usersRef.whereEqualTo("name", userName).get().addOnSuccessListener(matchingUserNames -> {
            if (matchingUserNames.size() > 0) {
                onFailure.callBack(new Exception("Username not available"));
            } else {
                push(new User(userName), onSuccess, onFailure);
            }
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.callBack(e);
        });
    }

    // password-less "Login" functionality. Just needs username.
    public static void login(String userName, UserCallback onSuccess, ExceptionCallback onFailure) {
        usersRef.whereEqualTo("name", userName).get()
                .addOnSuccessListener(task -> {
                    if (task.getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getDocuments().get(0);
                        user = readFirebaseObjectSnapshot(User.class, snapshot);
                        Log.d(TAG, "Login successful: " + user.toString());
                        if (onSuccess != null) onSuccess.callBack(user);
                    } else {
                        if (onFailure != null) onFailure.callBack(new Exception("User not found"));
                    }})
                .addOnFailureListener((exception) -> {
                    if (onFailure != null) onFailure.callBack(exception);
        });
    }

    // Add or sync local user to FireStore
    public static void push(User user) {
        push(user, null, null);
    }

    // Add or sync local user to FireStore
    public static void push(User user, UserCallback onSuccess) {
        push(user, onSuccess, null);
    }

    // Add or sync local user to FireStore
    public static void push(User user, UserCallback onSuccess, ExceptionCallback onFailure) {
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

    // Get User data from id
    // Call to view other profiles, and to load names for comments and experiment owners
    public static User getUser(String id) {
            for (User user : userArrayList)
                if (user.getId().getKey().equals(id))
                    return user;
        Log.e(TAG, "getUser() User not found.");
        return new User();
    }



    /** Trials **/

    // Sync local trial change to FireStore
    public static void push(Trial trial, Experiment experiment) {
        push(trial, experiment, null, null);
    }

    // Sync local trial change to FireStore
    public static void push(Trial trial, Experiment experiment, TrialCallback onComplete) {
        push(trial, experiment, onComplete, null);
    }

    // Sync local trial change to FireStore
    public static void push(Trial trial, Experiment experiment, TrialCallback onComplete, ExceptionCallback onFailure) {
        if (experiment == null || !experiment.isValid()) {
            Log.e(TAG, "Push failed. Tried to add trial to experiment without ID");
            return;
        }

        if (user == null || user.getId() == null || !user.getId().isValid()) {
            Log.e(TAG, "Push failed. Must be logged in to push.");
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





    /** Questions **/

    // Sync local trial change to FireStore
    public static void push(Question question, Experiment experiment) {
        push(question, experiment, null, null);
    }

    // Sync local trial change to FireStore
    public static void push(Question question, Experiment experiment, QuestionCallback onComplete) {
        push(question, experiment, onComplete, null);
    }

    // Sync local trial change to FireStore
    public static void push(Question question, Experiment experiment, QuestionCallback onComplete, ExceptionCallback onFailure) {
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




    /** Private **/

    // Converts firebase snapshot to classes that extend FirebaseObjects and adds id
    private static <ClassType extends FirestoreObject> ClassType readFirebaseObjectSnapshot(Class<ClassType> typeClass, DocumentSnapshot snapshot) {
        ClassType object = snapshot.toObject(typeClass);
        object.setId(new ID(snapshot.getId()));
        return object;
    }

    // Gets a list of all string values of a field from all objects in a collection
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

    private static void initializeExperiments() {
        experimentArrayList = new ArrayList<>();
        experimentsRef = db.collection(EXPERIMENTS);
        experimentsRef.addSnapshotListener((snapshot, e) -> updateExperiments(snapshot));
//        experimentsRef.get().addOnCompleteListener(task -> updateExperiments(task.getResult()));
    }

    private static void updateExperiments(QuerySnapshot experimentsSnapshot) {
        experimentArrayList.clear();
        for (QueryDocumentSnapshot snapshot : experimentsSnapshot)
            experimentArrayList.add(readFirebaseObjectSnapshot(Experiment.class, snapshot));

        for (Experiment experiment : experimentArrayList)
            System.out.println("Experiment: " + experiment.toString());
    }

    private static void initializeUsers() {
        userArrayList = new ArrayList<>();
        usersRef = db.collection(USERS);
        usersRef.addSnapshotListener((snapshot, e) -> updateUsers(snapshot));
//        usersRef.get().addOnCompleteListener(task -> updateUsers(task.getResult()));
    }

    private static void updateUsers(QuerySnapshot userSnapshots) {
        ArrayList<User> users = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots)
            users.add(readFirebaseObjectSnapshot(User.class, snapshot));

        userArrayList = users;
    }

    private static void setTrials(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Trial> trials = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots)
            trials.add(readFirebaseObjectSnapshot(Trial.class, snapshot));

        experiment.setTrials(trials);
    }

    private static void setQuestions(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Question> questions = new ArrayList<>();

        for (QueryDocumentSnapshot snapshot : userSnapshots)
            questions.add(readFirebaseObjectSnapshot(Question.class, snapshot));

        experiment.setQuestions(questions);
    }
}
