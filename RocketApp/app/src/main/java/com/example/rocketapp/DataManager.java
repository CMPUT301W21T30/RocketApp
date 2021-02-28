package com.example.rocketapp;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DataManager {
    private static ArrayList<Experiment> experimentArrayList;
    private static ArrayList<User> userArrayList;
    private static User user;

    private static FirebaseFirestore db;
    private static CollectionReference experimentsRef;
    private static CollectionReference usersRef;


    static {
        db = FirebaseFirestore.getInstance();
        initializeExperiments();
        initializeUsers();
    }

    private DataManager() { }

    public interface Callback {
        void callBack();
    }


    /** Experiments **/

    public interface PushExperimentCallback {
        void callBack(Experiment experiment);
    }

    // Add or sync local experiment to FireStore
    public static void push(Experiment experiment, PushExperimentCallback onComplete) {
        if (experiment.getId() != null) {
            experimentsRef.document(experiment.getId()).set(experiment);;
        } else {
            experimentsRef.add(experiment).addOnCompleteListener(task -> {
                experiment.setId(task.getResult().getId());
                if (onComplete != null) onComplete.callBack(experiment);
            });
        }
    }

    // Publish a new experiment
    public static void publishExperiment(Experiment experiment, PushExperimentCallback onComplete) {
        if (experiment.info.getOwnerId() == null) {
            Log.e(TAG, "Owner does not have id. Cannot publish experiment");
            return;
        }
        push(experiment, onComplete);
    }

    public interface PullExperimentsCallback {
        void callBack(ArrayList<Experiment> experiments);
    }

    // Pull all experiments from firestore
    public static void pullExperiments(PullExperimentsCallback onComplete) {
        experimentsRef.get().addOnCompleteListener(task -> {
            updateExperiments(task.getResult());
            if (onComplete != null) onComplete.callBack(experimentArrayList);
        });
    }



    /** Users **/

    public interface PushUserCallback {
        void callBack(User user);
    }

    // Create a new user
    public static void createUser(User newUser, PushUserCallback onComplete) {
        push(newUser, onComplete);
    }

    // Add or sync local user to FireStore
    public static void push(User user, PushUserCallback onComplete) {
        if (user.getId() != null) {
            usersRef.document(user.getId()).set(user);
        } else {
            usersRef.add(user).addOnCompleteListener(task -> {
                user.setId(task.getResult().getId());
                if (onComplete != null) onComplete.callBack(user);
            });
        }
    }



    /** Trials **/

    public interface PushTrialCallback {
        void callBack(Trial trial);
    }

    // Sync local trial change to FireStore
    public static void push(Trial trial, Experiment experiment, PushTrialCallback onComplete) {
        if (experiment.getId() == null) {
            Log.e(TAG, "Tried to add trial to experiment without ID");
            return;
        }

        CollectionReference trialsRef = experimentsRef.document(experiment.getId()).collection("Trials");

        if (trial.getId() != null) {
            trialsRef.document(trial.getId()).set(trial);;
        } else {
            trialsRef.add(trial).addOnCompleteListener(task -> {
                trial.setId(task.getResult().getId());
                if (onComplete != null) onComplete.callBack(trial);
            });
        }
    }

    public interface PullTrialsCallback {
        void callBack(ArrayList<Trial> trials);
    }

    // Pull trials from firestore
    public static void pullTrials(Experiment experiment, PullTrialsCallback onComplete) {
        if (experiment.getId() == null) {
            Log.e(TAG, "Tried pulling trials of experiment without ID");
            return;
        }

        experimentsRef.document(experiment.getId()).collection("Trials").get()
                .addOnCompleteListener(task -> {
                        setTrials(experiment, task.getResult());
                        if (onComplete != null) onComplete.callBack(experiment.getTrials());
                });
    }




    /** Questions **/

    public interface PushQuestionCallback {
        void callBack(Question question);
    }

    // Sync local trial change to FireStore
    public static void push(Question question, Experiment experiment, PushQuestionCallback onComplete) {
        if (experiment.getId() == null) {
            Log.e(TAG, "Tried to add question to experiment without ID");
            return;
        }

        CollectionReference trialsRef = experimentsRef.document(experiment.getId()).collection("Questions");

        if (question.getId() != null) {
            trialsRef.document(question.getId()).set(question);;
        } else {
            trialsRef.add(question).addOnCompleteListener(task -> {
                question.setId(task.getResult().getId());
                if (onComplete != null) onComplete.callBack(question);
            });
        }
    }

    public interface PullQuestionsCallback {
        void callBack(ArrayList<Question> trials);
    }

    // Pull questions from firestore
    public static void pullQuestions(Experiment experiment, PullQuestionsCallback onComplete) {
        if (experiment.getId() == null) {
            Log.e(TAG, "Tried pulling questions of experiment without ID");
            return;
        }

        experimentsRef.document(experiment.getId()).collection("Questions").get()
                .addOnCompleteListener(task -> {
                    setQuestions(experiment, task.getResult());
                    if (onComplete != null) onComplete.callBack(experiment.getQuestions());
                });
    }

    public static ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public static ArrayList<Experiment> getExperimentArrayList() {
        return experimentArrayList;
    }

    public static User getUser() {
        return user;
    }

    public static void endExperiment(User owner, Experiment experiment) { }

    public static void addQuestion(User owner, Experiment experiment, String question) {}


    private static void initializeExperiments() {
        experimentArrayList = new ArrayList<>();
        experimentsRef = db.collection("Experiments");
        experimentsRef.addSnapshotListener((snapshot, e) -> updateExperiments(snapshot));
        experimentsRef.get().addOnCompleteListener(task -> updateExperiments(task.getResult()));
    }

    private static void updateExperiments(QuerySnapshot experimentsSnapshot) {
        experimentArrayList.clear();
        for (QueryDocumentSnapshot experimentSnapshot : experimentsSnapshot) {
            Experiment experiment = experimentSnapshot.toObject(Experiment.class);
            experiment.setId(experimentSnapshot.getId());
            experimentArrayList.add(experiment);
        }
        for (Experiment experiment : experimentArrayList)
            System.out.println("Experiment: " + experiment.toString());
    }

    private static void initializeUsers() {
        userArrayList = new ArrayList<>();
        usersRef = db.collection("Users");
        usersRef.addSnapshotListener((snapshot, e) -> updateUsers(snapshot));
        usersRef.get().addOnCompleteListener(task -> updateUsers(task.getResult()));
    }

    private static void updateUsers(QuerySnapshot userSnapshots) {
        ArrayList<User> users = new ArrayList<>();

        for (QueryDocumentSnapshot userSnapshot : userSnapshots) {
            User user = userSnapshot.toObject(User.class);
            user.setId(userSnapshot.getId());
            users.add(user);
        }

        userArrayList = users;
    }

    private static void setTrials(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Trial> trials = new ArrayList<>();

        for (QueryDocumentSnapshot userSnapshot : userSnapshots) {
            Trial trial = userSnapshot.toObject(Trial.class);
            trial.setId(userSnapshot.getId());
            trials.add(trial);
        }

        experiment.setTrials(trials);
    }

    private static void setQuestions(Experiment experiment, QuerySnapshot userSnapshots) {
        ArrayList<Question> questions = new ArrayList<>();

        for (QueryDocumentSnapshot userSnapshot : userSnapshots) {
            Question question = userSnapshot.toObject(Question.class);
            question.setId(userSnapshot.getId());
            questions.add(question);
        }

        experiment.setQuestions(questions);
    }
}
