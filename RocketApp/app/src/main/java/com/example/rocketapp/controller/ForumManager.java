package com.example.rocketapp.controller;

import android.util.Log;

import com.example.rocketapp.controller.callbacks.Callback;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.comments.Answer;
import com.example.rocketapp.model.comments.Comment;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import static com.example.rocketapp.controller.FirestoreDocument.readFirebaseObjectSnapshot;


/**
 * Handles adding, retrieving, and modifying questions and answers for experiment forums.
 */
public class ForumManager {
    private static final String TAG = "ForumManager";
    private static final String EXPERIMENTS = "Experiments";
    private static final String COMMENTS = "Comments";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ListenerRegistration forumListener;
    private static ForumManager instance;
    protected Callback onUpdate;

    /**
     * Private constructor, should not be instantiated
     */
    protected ForumManager() { }

    public static void inject(ForumManager injection) {
        instance = injection;
    }

    private static ForumManager getInstance() {
        if (instance == null) instance = new ForumManager();
        return instance;
    }

    /**
     * Listens to firestore for changes to this experiment. You MUST use this to get the trials and questions for an experiment.
     * You may want to update the UI in onUpdate.
     * @param experiment
     *      The experiment to listen to.
     * @param onUpdate
     *      Callback for implementing desired behaviour when the experiment is updated in firestore.
     */
    public static void listen(Experiment experiment, Callback onUpdate) {
        getInstance().listenImp(experiment, onUpdate);
    }
    protected void listenImp(Experiment experiment, Callback onUpdate) {
        if (!experiment.isValid()) {
            Log.e(TAG, "Cannot listen to an experiment without an id.");
            return;
        }

        this.onUpdate = onUpdate;

        if (forumListener != null) forumListener.remove();
        String documentId = experiment.getId().getKey();

        // Listen for changes in Questions
        CollectionReference commentsRef = db.collection(EXPERIMENTS).document(documentId).collection(COMMENTS);
        forumListener = commentsRef.addSnapshotListener((snapshots, e) -> {
            parseCommentsSnapshot(experiment, snapshots);
            Log.d(TAG, "Experiment Questions Updated: " + experiment.getQuestions().size());
            onUpdate.callBack();
        });
    }


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
    public static void addQuestion(Question question, Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        getInstance().addQuestionImp(question, experiment, onSuccess, onFailure);
    }
    protected void addQuestionImp(Question question, Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        ((FirestoreOwnableDocument) question).setOwner(UserManager.getUser());
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
    public static void update(Question question, Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        getInstance().updateImp(question, experiment, onSuccess, onFailure);
    }
    protected void updateImp(Question question, Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        if (!question.getOwner().equals(UserManager.getUser())) {
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
    public static void addAnswer(Answer answer, Question question, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        getInstance().addAnswerImp(answer, question, onSuccess, onFailure);
    }
    protected void addAnswerImp(Answer answer, Question question, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        ((FirestoreOwnableDocument) answer).setOwner(UserManager.getUser());
        ((FirestoreNestableDocument) answer).setParent(question.getId());
        push(answer, ExperimentManager.getExperiment(question.getParentId()), onSuccess, onFailure);
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
    public static void update(Answer answer, Question question, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        getInstance().updateImp(answer, question, onSuccess, onFailure);
    }
    protected void updateImp(Answer answer, Question question, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        if (!answer.getOwner().equals(UserManager.getUser())) {
            onFailure.callBack(new Exception("Cannot update answer. Not owned by user."));
        } else {
            addAnswer(answer, question, onSuccess, onFailure);
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
    private static void push(Comment comment, Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        if (comment == null || !comment.parentIsValid()) {
            Log.e(TAG, "Push failed. Tried to add null or un-parented comment.");
            onFailure.callBack(new Exception("Push failed. Tried to add null or un-parented comment."));
            return;
        }

        if (!UserManager.isSignedIn()) {
            Log.e(TAG, "Push failed. Must be logged in to push comment.");
            onFailure.callBack(new Exception("Push failed. Must be logged in to push comment."));
            return;
        }

        CollectionReference commentsRef = db.collection(EXPERIMENTS).document(experiment.getId().getKey()).collection(COMMENTS);

        if (comment.getId() != null) {
            commentsRef.document(comment.getId().getKey()).set(comment).addOnSuccessListener(questionSnapshot -> {
                Log.d(TAG, "Comment Updated.");
                onSuccess.callBack();
            }).addOnFailureListener(e->{
                Log.e(TAG, "Failed to update comment: " + e.toString());
                onFailure.callBack(e);
            });
        } else {

            ((FirestoreOwnableDocument) comment).setOwner(UserManager.getUser());
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
                questionsArrayList.add(readFirebaseObjectSnapshot(Question.class, snapshot, TAG));
            } else if (type.equals(Answer.TYPE)) {
                answersArrayList.add(readFirebaseObjectSnapshot(Answer.class, snapshot, TAG));
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
}
