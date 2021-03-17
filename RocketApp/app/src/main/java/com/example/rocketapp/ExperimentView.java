package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * View for unsubscribed experiments
 * TODO use it inside actual code
 */
public class ExperimentView extends AppCompatActivity {

    private ArrayList<Trial> trials;

    /**
     * Set up experiment view for unsubscribed users
     * Display type, region, description and minTrials
     * Does not have functionality to add trial
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_view);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        Experiment experiment = DataManager.getExperiment(i.getSerializableExtra("id"));
        TextView expType = findViewById(R.id.experimentTypeTextView);
        TextView expDescription = findViewById(R.id.descriptionTextView);
        expType.setText(experiment.getType());
        expDescription.setText(experiment.info.getDescription());
    }
}