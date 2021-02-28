package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DataManager.createUser(new User("Morty"), user -> {
            DataManager.publishExperiment(
                    new Experiment(user.getId(), "Delayed Experiment", "This is a new experiment", "Alberta", 10, false),
                    experiment -> {
                        DataManager.push(new Trial("This is a new trial", user), experiment, null);
                        DataManager.push(new Question("This is a new question", user),  experiment, null);
                    });
        });



    }
}