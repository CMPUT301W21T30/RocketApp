package com.example.rocketapp;

public class Answer extends Comment {
    final static String TYPE = "Answer";

    public Answer() {
    }

    public Answer(String text) {
        super(text);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
