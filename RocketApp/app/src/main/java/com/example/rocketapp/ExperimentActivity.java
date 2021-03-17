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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);
        TextView meanView = findViewById(R.id.meanView);
        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));
        DataManager.listen(experiment, updatedExperiment -> {
            if(updatedExperiment.getType()=="Count"){
                float mean = 0;
                for(int j=0; j<updatedExperiment.getTrials().size(); j++){
                    CountTrial t = (CountTrial) updatedExperiment.getTrials().get(j);
                    System.out.println(t.getCount());
                }
                meanView.setText(String.valueOf(mean/updatedExperiment.getTrials().size()));
        }});
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
    }

}