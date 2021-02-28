package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // This is an example of how to create a user, publish an experiment, and add a trial and question to that experiment
        // Always use the callback lambdas to do work following these activities, since these are asynchronous commands their effects
        // won't exist until after they synchronize with firebase (they will be synchronized at the point when lambdas are called).
        DataManager.createUser(new User("Morty"), user -> {
            DataManager.publishExperiment(
                    new Experiment(user.getId(), "Delayed Experiment", "This is a new experiment", "Alberta", 10, false),
                    experiment -> {

                        DataManager.push(new Trial("This is a new trial", user), experiment, null);
                        DataManager.push(new Question("This is a new question", user),  experiment, null);

                        // This makes the datamanager update all data for this experiment, need to subscribe when opening an experiment to
                        // get updated trials and questions
                        DataManager.subscribe(experiment);
                    });
        });





    }
}