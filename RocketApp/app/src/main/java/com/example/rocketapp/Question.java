package com.example.rocketapp;
import com.google.firebase.firestore.Exclude;
import java.util.ArrayList;

/**
 * Question is a class that inherits from abstract class Comment.
 * Question generally expects an answer to be posted by Owner or fellow Experimenters
 * Question may also just be a discussion or remark based on experiment
 */
public class Question extends Comment {
    final static String TYPE = "Question";          //Type of comment.
    private ArrayList<Answer> answersArrayList = new ArrayList<>();

    /**
     * Default constructor for firestore serialization. Do not use.
     */
    public Question() {}

    /**
     * Constructor with text of comment(Question) passed.
     * @param text
     *          text of comment passed by user
     */
    public Question(String text) {
        super(text);
    }

    /**
     * setter for array list of answers replied to a question comment
     * @param answers
     *          array list of comments of type answer posted to this question
     */
    public void setAnswers(ArrayList<Answer> answers) {
        answersArrayList = answers;
    }

    /**
     * getter for answers
     * @return array list of type answer containing all answer comments posted under this question
     */
    @Exclude
    public ArrayList<Answer> getAnswers() {
        return answersArrayList;
    }

    /**
     * @return the type of Comment, objects of this class return "Question".  - String
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * describes display behavior of question comment
     * @return a String detailing about question, answers and column headings.
     */
    @Override
    public String toString() {
        return "Question{" +
                "owner=" + getOwner().getName() +
                "text=" + getText() +
                "answersArrayList=" + answersArrayList +
                '}';
    }
}
