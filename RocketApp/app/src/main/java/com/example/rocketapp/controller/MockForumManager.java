package com.example.rocketapp.controller;

import android.util.Log;

import com.example.rocketapp.controller.callbacks.Callback;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.comments.Answer;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.experiments.Experiment;

import java.util.ArrayList;
import java.util.Random;

public class MockForumManager extends ForumManager {
    Random rand = new Random();

    public MockForumManager() {}

    @Override
    protected void listenImp(Experiment experiment, Callback onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    protected void addQuestionImp(Question question, Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        ((FirestoreNestableDocument) question).setParent(experiment.getId());
        ((FirestoreNestableDocument) question).setOwner(UserManager.getUser());
        ((FirestoreNestableDocument) question).setId(new FirestoreDocument.Id(String.valueOf(rand.nextInt())));
        ArrayList<Question> questions= experiment.getQuestions();
        questions.add(question);
        experiment.setQuestions(questions);
        Log.e("MForManager", questions.toString());
        onUpdate.callBack();
    }

    @Override
    protected void updateImp(Question question, Experiment experiment, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        onSuccess.callBack();
    }

    @Override
    protected void addAnswerImp(Answer answer, Question question, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        ((FirestoreNestableDocument) answer).setParent(question.getId());
        ((FirestoreNestableDocument) answer).setOwner(UserManager.getUser());
        ((FirestoreNestableDocument) answer).setId(new FirestoreDocument.Id(String.valueOf(rand.nextInt())));
        ArrayList<Answer> answers = question.getAnswers();
        answers.add(answer);
        question.setAnswers(answers);
        onUpdate.callBack();
    }

    @Override
    protected void updateImp(Answer answer, Question question, Callback onSuccess, ObjectCallback<Exception> onFailure) {
        onSuccess.callBack();
    }
}
