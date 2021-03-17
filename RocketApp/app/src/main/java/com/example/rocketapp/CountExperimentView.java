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

public class CountExperimentView extends AppCompatActivity {

    private ArrayList<Trial> trials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_experiment_view);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
//        Experiment experiment = DataManager.getExperiment(id);
        Experiment experiment = DataManager.getExperiment(i.getSerializableExtra("id"));
        TextView expType = findViewById(R.id.cexp_type);
        TextView expDescription = findViewById(R.id.cexp_desc);
        expType.setText(experiment.getType());
        expDescription.setText(experiment.info.getDescription());
        Button addTrial = findViewById(R.id.addCountTrial);
        Button submit = findViewById(R.id.submitTrial);
        TextView count = findViewById(R.id.editCountTrial);
        addTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count.setVisibility(View.VISIBLE);
                addTrial.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validate.lengthInRange((EditText)count, 1, Integer.MAX_VALUE, true)) {
                    Integer value = Integer.parseInt(count.getText().toString());
                    Trial newTrial = new CountTrial(value);
                    DataManager.addTrial(newTrial, experiment, trial -> {
                        Log.d("TRIAL", "Trial exception");
                    }, exception -> {
                        Log.d("EXCEPTION", "Exception");
                    });
                    count.setVisibility(View.INVISIBLE);
                    addTrial.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Trial added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }}