package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MeasurementExperimentView extends AppCompatActivity {

    private ArrayList<Trial> trials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measurement_experiment_view);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        Experiment experiment = DataManager.getExperiment(i.getSerializableExtra("id"));
        TextView expType = findViewById(R.id.mexp_type);
        TextView expDescription = findViewById(R.id.mexp_desc);
        expType.setText(experiment.getType());
        expDescription.setText(experiment.info.getDescription());
        Button addTrial = findViewById(R.id.addMeasuermentTrial);
        Button submit = findViewById(R.id.submitTrial);
        TextView measurement = findViewById(R.id.editMeasurementTrial);
        addTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurement.setVisibility(View.VISIBLE);
                addTrial.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validate.lengthInRange((EditText)measurement, 1, Integer.MAX_VALUE, true)) {
                    float value = Float.parseFloat(measurement.getText().toString());
                    Trial newTrial = new MeasurementTrial(value);
                    DataManager.addTrial(newTrial, experiment, trial -> {
                        Log.d("TRIAL", "Trial exception");
                    }, exception -> {
                        Log.d("EXCEPTION", "Exception");
                    });
                    measurement.setVisibility(View.INVISIBLE);
                    addTrial.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Trial added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }}