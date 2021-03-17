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
        TextView meanText = findViewById(R.id.meanText);
        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));
        if(experiment.getType().equals("Binomial")){
            meanText.setText("Success Ratio - x");
        }
        getMean(experiment, meanView);
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

    private void getMean(Experiment experiment, TextView meanView) {
        DataManager.listen(experiment, updatedExperiment -> {
            if (updatedExperiment.getType().equals("Count")) {
                float mean = 0;
                for (int j = 0; j < updatedExperiment.getTrials().size(); j++) {
                    CountTrial t = (CountTrial) updatedExperiment.getTrials().get(j);
                    System.out.println(t.getCount());
                    mean = mean + t.getCount();
                }
                if (updatedExperiment.getTrials().size() > 0) {
                    meanView.setText(String.valueOf(mean / updatedExperiment.getTrials().size()));
                } else {
                    meanView.setText("0.0");
                }
            } else if (updatedExperiment.getType().equals("IntCount")) {
                float mean = 0;
                for (int j = 0; j < updatedExperiment.getTrials().size(); j++) {
                    IntCountTrial t = (IntCountTrial) updatedExperiment.getTrials().get(j);
                    mean = mean + t.getPCount();
                }
                if (updatedExperiment.getTrials().size() > 0) {
                    meanView.setText(String.valueOf(mean / updatedExperiment.getTrials().size()));
                } else {
                    meanView.setText("0.0");
                }
            }
            else if (updatedExperiment.getType().equals("Measurement")) {
                float mean = 0;
                for (int j = 0; j < updatedExperiment.getTrials().size(); j++) {
                    MeasurementTrial t = (MeasurementTrial) updatedExperiment.getTrials().get(j);
                    mean = mean + t.getMeasurement();
                }
                if (updatedExperiment.getTrials().size() > 0) {
                    meanView.setText(String.valueOf(mean / updatedExperiment.getTrials().size()));
                } else {
                    meanView.setText("0.0");
                }
            }
            else{
                int success = 0;
                int fail = 0;
                for(int j=0; j<updatedExperiment.getTrials().size(); j++){
                    BinomialTrial t = (BinomialTrial) updatedExperiment.getTrials().get(j);
                    if(t.isValue()){
                        success++;
                    }
                    else{
                        fail++;
                    }
                }
                if((success+fail)>0){
                    meanView.setText(String.valueOf(success/(success+fail)));
                }
                else{
                    meanView.setText("0");
                }
            }

        });
    }
}