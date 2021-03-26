package com.example.rocketapp.controller;

import android.util.Log;

import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.users.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserManager {
    private static User user;
    private static ArrayList<User> userArrayList;
    private static ArrayList<FirestoreDocument.Id> subscriptions;
    private static ListenerRegistration subscriptionsListener;
    private static ListenerRegistration usersListener;
    private static final String SUBSCRIPTIONS = "Subscriptions";
    private static final String TAG = "UserManager";
    private static final String USERS = "Users";

    private static CollectionReference usersRef;
    private static DataManager.Callback updateCallback;
    private static final FirebaseFirestore db;

    static {
        db = FirebaseFirestore.getInstance();
        initializeUsers();
    }


    public static Boolean signedIn() {
        return user != null && user.isValid();
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
     * Set callback for when Experiments are updated from firestore. Should use to update listviews of experiments.
     * @param callback
     *      Callback for when experiments are updated from firestore.
     */
    public static void setUpdateCallback(DataManager.Callback callback) {
        updateCallback = callback;
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
     * Creates a new user and logs in.
     * @param userName
     *      Username for new user
     * @param onSuccess
     *      Callback for when user creation is successful
     * @param onFailure
     *      Callback for when username already exists fails
     */
    public static void createUser(String userName, DataManager.UserCallback onSuccess, DataManager.ExceptionCallback onFailure) {
        usersRef.whereEqualTo("name", userName).get().addOnSuccessListener(matchingUserNames -> {
            if (matchingUserNames.size() > 0) {
                Log.e(TAG, "CreateUser failed: Username not available");
                onFailure.callBack(new Exception("Username not available"));
            } else {
                push(new User(userName), (user) -> login(userName, onSuccess, onFailure), onFailure);
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
    public static void login(String userName, DataManager.UserCallback onSuccess, DataManager.ExceptionCallback onFailure) {
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
    public static void updateUser(DataManager.UserCallback onSuccess, DataManager.ExceptionCallback onFailure) {
        push(user, onSuccess, onFailure);
    }

    public static User getUser() {
        return user;
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
     * Adds or updates a user in firebase.
     * @param user
     *      The user class to update or store in firestore.
     * @param onSuccess
     *      Callback for when successful
     * @param onFailure
     *      Callback for when push fails
     */
    private static void push(User user, DataManager.UserCallback onSuccess, DataManager.ExceptionCallback onFailure) {
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
     * Pulls the subscription id's for the current user from firestore.
     * @param onSuccess
     *      Callback for when subscriptions are pulled from firestore successfuly
     * @param onFailure
     *      Callback for when pullSubscriptions fails
     */
    private static void pullSubscriptions(DataManager.Callback onSuccess, DataManager.ExceptionCallback onFailure) {
        usersRef.document(user.getId().getKey()).collection(SUBSCRIPTIONS).get().addOnCompleteListener((subs) -> {
            ArrayList<FirestoreDocument.Id> subscriptionsList = new ArrayList<>();
            for (QueryDocumentSnapshot sub : subs.getResult())
                subscriptionsList.add(sub.toObject(FirestoreDocument.Id.class));
            subscriptions = subscriptionsList;
            Log.d(TAG, "Subscriptions Updated.");
            for (FirestoreDocument.Id id : subscriptions) {
                System.out.println(id.getKey());
            }
            onSuccess.callBack();
        }).addOnFailureListener(e->{
            Log.e(TAG, "Failed to update subscriptions: " + e.toString());
            onFailure.callBack(e);
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

    public static ArrayList<FirestoreDocument.Id> getSubscriptionsIdList() {
        return subscriptions;
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
    public static void subscribe(Experiment experiment, DataManager.Callback onSuccess, DataManager.ExceptionCallback onFailure) {
        if (user == null || !UserManager.getUser().isValid()) {
            Log.d(TAG, "Failed to subscribe. User must be logged in to subscribe to an experiment.");
            if (onFailure != null) onFailure.callBack(new Exception("Failed to subscribe. User must be logged in to subscribe to an experiment."));
            return;
        }

        if (experiment == null || !experiment.isValid()) {
            Log.d(TAG, "Failed to subscribe. Experiment does not have id.");
            if (onFailure != null) onFailure.callBack(new Exception("Failed to subscribe. Experiment does not have id."));
            return;
        }

        if (UserManager.getSubscriptionsIdList().contains(experiment.getId())) {
            Log.d(TAG, "Already subscribed to this experiment.");
            if (onFailure != null) onFailure.callBack(new Exception("Already subscribed to this experiment."));
            return;
        }

        usersRef.document(UserManager.getUser().getId().getKey()).collection(SUBSCRIPTIONS).add(experiment.getId())
                .addOnSuccessListener(task -> {
                    Log.d(TAG, "Subscribed to experiment: " + experiment.getId().toString());
                    onSuccess.callBack();
                }).addOnFailureListener(e -> {
            Log.e(TAG, "Subscribe failed: " + e.toString());
            onFailure.callBack(e);
        });
    }

}
