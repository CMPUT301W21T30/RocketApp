package com.example.rocketapp;
import com.google.firebase.firestore.Exclude;
import java.util.ArrayList;

public class Question extends Comment {
    final static String TYPE = "Question";
    private ArrayList<Answer> answersArrayList = new ArrayList<>();

    public Question() {}

    public Question(String text) {
        super(text);
    }

    public void setAnswers(ArrayList<Answer> answers) {
        answersArrayList = answers;
    }

    @Exclude
    public ArrayList<Answer> getAnswers() {
        return answersArrayList;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text" + getText() +
                "answersArrayList=" + answersArrayList +
                '}';
    }
}
