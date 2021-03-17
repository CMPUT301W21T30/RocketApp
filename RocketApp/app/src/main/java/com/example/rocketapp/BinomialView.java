package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BinomialView extends AppCompatActivity {

    private ArrayList<Trial> trials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binomial_view);
        Intent i = getIntent();

        Experiment experiment = DataManager.getExperiment(i.getSerializableExtra("id"));
        TextView expType = findViewById(R.id.experimentTypeTextView);
        TextView expDescription = findViewById(R.id.descriptionTextView);
        expType.setText(experiment.getType());
        expDescription.setText(experiment.info.getDescription());
        Button addTrial = findViewById(R.id.addTrialButton);
        Button fail = findViewById(R.id.failBinomial);
        Button success = findViewById(R.id.successBinomial);
        addTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success.setVisibility(View.VISIBLE);
                addTrial.setVisibility(View.INVISIBLE);
                fail.setVisibility(View.VISIBLE);
            }
        });
        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean value = false;
                Trial newTrial = new BinomialTrial(false);
                DataManager.addTrial(newTrial, experiment, trial -> {
                    Log.d("TRIAL", "Trial exception");
                } , exception -> {
                    Log.d("EXCEPTION", "Exception");
                });
                success.setVisibility(View.INVISIBLE);
                addTrial.setVisibility(View.VISIBLE);
                fail.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Trial added", Toast.LENGTH_SHORT).show();
            }
        });
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean value = false;
                Trial newTrial = new BinomialTrial(true);
                DataManager.addTrial(newTrial, experiment, trial -> {
                    Log.d("TRIAL", "Trial exception");
                } , exception -> {
                    Log.d("EXCEPTION", "Exception");
                });
                success.setVisibility(View.INVISIBLE);
                addTrial.setVisibility(View.VISIBLE);
                fail.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Trial added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}