package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExperimentActivity extends AppCompatActivity {

    private Experiment experiment;
    private TextView meanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);
        meanView = findViewById(R.id.meanView);
        TextView meanText = findViewById(R.id.meanText);
        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));
        if (experiment.getType().equals("Binomial")) {
            meanText.setText("Success Ratio - x");
        }
//        getMean(experiment, meanView);
        TextView experimentType = findViewById(R.id.experimentTypeTextView);
        experimentType.setText(experiment.getType());

        TextView experimentDescription = findViewById(R.id.descriptionTextView);
        experimentDescription.setText(experiment.info.getDescription());

        Button addTrial = findViewById(R.id.addTrialButton);
        addTrial.setVisibility(View.VISIBLE);

        addTrial.setOnClickListener(view -> {
            new TrialFragment(experiment.getType(), newTrial -> {
                DataManager.addTrial(newTrial, experiment, t -> {
                    Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show();
                }, e -> {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                });
            }).show(getSupportFragmentManager(), "ADD_TRIAL");
        });

        DataManager.listen(experiment, this::update);
    }

    void update(Experiment experiment) {
        // Could add all updates here
        try {
            // TODO getMean() and other stats functions will try to divide by zero, need to fix those functions to return a default case when no trials exist, then we don't need this to be in a try catch
            meanView.setText(String.valueOf(experiment.getMean()));
        } catch (Exception e) {
            meanView.setText("0.0");
        }

    }
}