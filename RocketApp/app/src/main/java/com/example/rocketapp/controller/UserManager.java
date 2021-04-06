package com.example.rocketapp.controller;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.rocketapp.controller.callbacks.Callback;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.users.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import static com.example.rocketapp.controller.FirestoreDocument.readFirebaseObjectSnapshot;

/**
 * Handles creating, retrieving, and modifying users as well as signing in to firestore.
 */
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
    private static final FirebaseFirestore db;
    private static Callback updateCallback;


    static {
        db = FirebaseFirestore.getInstance();
        initializeUsers();
    }


    /**
     * Private constructor, should not be instantiated
     */
    private UserManager() {}


    /**
     * Callback returning a User
     */
    public interface UserCallback {
        void callBack(User user);
    }


    /**
     * @return the currently signed in user
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
     * @return true if a valid user is signed in
     */
    public static Boolean isSignedIn() {
        return user != null && user.isValid();
    }


    /**
     * Set callback for when Experiments are updated from firestore. Should use to update listviews of experiments.
     * @param callback
     *      Callback for when experiments are updated from firestore.
     */
    public static void setUpdateCallback(Callback callback) {
        updateCallback = callback;
        updateCallback.callBack();
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
    public static void createUser(String userName, SharedPreferences sharedPreferences, UserCallback onSuccess, ObjectCallback<Exception> onFailure) {
        usersRef.whereEqualTo("name", userName).get().addOnSuccessListener(matchingUserNames -> {
            if (matchingUserNames.size() > 0) {
                Log.e(TAG, "Username not available.");
                onFailure.callBack(new Exception("Username not available."));
            } else {
                push(new User(userName), user -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", user.getId().getKey());
                    editor.apply();
                    login(sharedPreferences, onSuccess, onFailure);
                    }, onFailure);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, e.getMessage());
            onFailure.callBack(e);
        });
    }


    /**
     * Login to user account with user name.
     * @param onSuccess
     *      Callback for when login is successful
     * @param onFailure
     *      Callback for when login fails (username does not exist)
     */
    public static void login(SharedPreferences sharedPreferences, UserCallback onSuccess, ObjectCallback<Exception> onFailure) {
        String userId = sharedPreferences.getString("userId", "not found");
        Log.e(TAG, "Read UserID: "+ userId);
        usersRef.document(userId).get().addOnSuccessListener(snapshot -> {
            Log.d(TAG, snapshot.toString());
                user = readFirebaseObjectSnapshot(User.class, snapshot, TAG);
                if (user != null) {
                    listen(user);  // Listen to subscriptions
                    pullSubscriptions(()-> {
                        Log.d(TAG, "Login successful: " + user.toString());
                        onSuccess.callBack(user);
                    }, onFailure);
                }
            }).addOnFailureListener((e) -> {
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
    public static void updateUser(UserCallback onSuccess, ObjectCallback<Exception> onFailure) {
        usersRef.whereEqualTo("name", user.getName()).get().addOnSuccessListener(matchingUserNames -> {
            if (matchingUserNames.size() == 1 && !matchingUserNames.getDocuments().get(0).getId().equals(user.getId().getKey())) {
                Log.e(TAG, "Update User Failed: Username not available");
                onFailure.callBack(new Exception("Username not available"));
            } else {
                push(user, onSuccess, onFailure);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "CreateUser failed: " + e.toString());
            onFailure.callBack(e);
        });
    }


    /**
     * Retrieve a user from an ID
     * @param id
     *      ID of user to retrieve user data for.
     * @return
     *      User object matching id
     */
    public static User getUser(Object id) {
        for (User user : userArrayList)
            if (user.getId().equals(id))
                return user;
        Log.e(TAG, "getUser() User not found.");
        return new User("User not found.");
    }


    /**
     * @return list of ids for subscribed experiments
     */
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
    public static void subscribe(Experiment<?> experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        if (!isSignedIn()) {
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
    private static void push(User user, UserCallback onSuccess, ObjectCallback<Exception> onFailure) {
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
     * Pulls the subscription id's for the current user from firestore.
     * @param onSuccess
     *      Callback for when subscriptions are pulled from firestore successfuly
     * @param onFailure
     *      Callback for when pullSubscriptions fails
     */
    private static void pullSubscriptions(Callback onSuccess, ObjectCallback<Exception> onFailure) {
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
            users.add(readFirebaseObjectSnapshot(User.class, snapshot, TAG));

        userArrayList = users;
    }

}
