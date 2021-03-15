package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class ExperimentsListActivity extends AppCompatActivity /*implements CreateExperimentDialog.OnInputListener*/ {

    //use this button to navigate to the profile page of the user
    private static final String TAG = "ExperimentsListActivity";
    public ImageButton profileBtn;
    public Button createExpBtn;

    protected ArrayList<Experiment> experiments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_list);

        // Join Two list of Experiment Together
        Set<Experiment> set = new LinkedHashSet<>(DataManager.getSubscribedExperimentArrayList());
        // TODO: There is a problem with getOwnedExperimentsArrayList() Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'boolean com.example.rocketapp.DataManager$ID.equals(java.lang.Object)' on a null object reference at DataManager.getOwnedExperimentsArrayList(DataManager.java:215)
//        set.addAll(DataManager.getOwnedExperimentsArrayList());
        experiments = new ArrayList<>(set);
        initRecyclerView();


        profileBtn = findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(userProfileIntent);

        });

        createExpBtn = findViewById(R.id.createExpBtn);
        createExpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateExperimentDialog().show(getSupportFragmentManager(), "Add_experiment");
//                IntCountExperiment newExperiment = new IntCountExperiment("test int count", "AB", 10, true);
//                DataManager.publishExperiment(newExperiment, experiment -> {
//                    Toast.makeText(getApplicationContext(), "Experiment published", Toast.LENGTH_LONG).show();
//                }, exception -> {
//                    Toast.makeText(getApplicationContext(), "Experiment could not be added", Toast.LENGTH_LONG).show();
//                });
            }
        });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerView);
        ExperimentRecylerViewAdapter adapter = new ExperimentRecylerViewAdapter(this, experiments);
        experimentRecyclerView.setAdapter(adapter);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

            /*
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        String username = usernameEditText.getText().toString();
        loginOrCreateUser(username, isOwner.get());
    });
    */
}