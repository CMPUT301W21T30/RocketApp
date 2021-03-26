package com.example.rocketapp.controller;

import android.util.Log;

import com.example.rocketapp.model.comments.Answer;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.experiments.Experiment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ForumManager {
    private static final String TAG = "ForumManager";
    private static ListenerRegistration forumListener;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String EXPERIMENTS = "Experiments";
    private static final String COMMENTS = "Comments";


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

        forumListener.remove();
        String documentId = experiment.getId().getKey();

        // Listen for changes in Questions
        CollectionReference commentsRef = db.collection(EXPERIMENTS).document(documentId).collection(COMMENTS);
        forumListener = commentsRef.addSnapshotListener((snapshots, e) -> {
            parseCommentsSnapshot(experiment, snapshots);
            Log.d(TAG, "Experiment Questions Updated: " + experiment.getQuestions().size());
            onUpdate.callBack(experiment);
        });

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
}
