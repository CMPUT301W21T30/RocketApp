package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Display view for Experiment
 */
public class ExperimentActivity extends AppCompatActivity {

    private Experiment experiment;
    private TextView meanView;

    private ImageButton experimentOptions;

    private TextView medianView;
    private TextView stdDevView;
    private TextView regionView;
    private TextView minTrialsView;

    /**
     * Setup the view for Experiment
     * Display type, description, region and Minimum Trials.
     * Button to add trials
     * @param savedInstanceState
     *          passed state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));

        meanView = findViewById(R.id.meanView);
        medianView  = findViewById(R.id.medianValue);
        stdDevView  = findViewById(R.id.stdDevVal);
        regionView  = findViewById(R.id.regionView);
        minTrialsView = findViewById(R.id.minTrialsView);

        TextView meanText = findViewById(R.id.meanText);
        experimentOptions = findViewById(R.id.experiment_options);

        regionView.setText(experiment.info.getRegion());
        String minTrialsString = "Min Trials - " + String.valueOf(experiment.info.getMinTrials());
        minTrialsView.setText(minTrialsString);

        if (experiment.getType().equals("Binomial")) {
            meanText.setText("Success Ratio - ");
        }
//        getMean(experiment, meanView);
        TextView experimentType = findViewById(R.id.experimentTypeTextView);
        experimentType.setText(experiment.getType());

        TextView experimentDescription = findViewById(R.id.descriptionTextView);
        experimentDescription.setText(experiment.info.getDescription());

        Button addTrial = findViewById(R.id.addTrialButton);
        if(experiment.getState().equals(Experiment.State.ENDED)){
            addTrial.setVisibility(View.GONE);
        }
        else {
            addTrial.setVisibility(View.VISIBLE);
        }

        addTrial.setOnClickListener(view -> {
            new TrialFragment(experiment.getType(), newTrial -> {
                DataManager.addTrial(newTrial, experiment, t -> {
                    Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show();
                }, e -> {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                });
            }).show(getSupportFragmentManager(), "ADD_TRIAL");
        });

        Button forumButton = findViewById(R.id.forumButton);
        forumButton.setOnClickListener(v->{
            Intent intent = new Intent(this, ExperimentQuestionsActivity.class);
            intent.putExtra("id", experiment.getId());
            startActivity(intent);
        });

        DataManager.listen(experiment, this::update);
    }

    /**
     * Update and display experiment statistics.
     * @param experiment
     *          Experiment of current view
     */
    void update(Experiment experiment) {
        // Could add all updates here
        meanView.setText(String.valueOf(experiment.getMean()));
        medianView.setText(String.valueOf(experiment.getMedian()));
        stdDevView.setText(String.valueOf(experiment.getStdDev()));


    }
}