package com.example.rocketapp;

/**
 * Answer is a class that inherits from abstract class Comment.
 * Answer is meant to be posted as a reply to a comment already posted.
 */
public class Answer extends Comment {
    final static String TYPE = "Answer";    //Type of comment.

    /**
     * Constructor without any parameters passed.
     */
    public Answer() {
    }

    /**
     * Constructor with text of comment(Answer) passed.
     * @param text
     *          text of comment passed by user
     */
    public Answer(String text) {
        super(text);
    }

    /**
     *
     * @return the type of Comment, objects of this class return "Answer".  - String
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * describes display behavior of answer comment
     * @return a String detailing about question, answers and column headings.
     */
    @Override
    public String toString() {
        return "Answer{" +
                "owner=" + getOwner().getName() +
                "text=" + getText() +
                '}';
    }
}
