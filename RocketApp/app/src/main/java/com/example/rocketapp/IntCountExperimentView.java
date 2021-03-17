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

public class IntCountExperimentView extends AppCompatActivity {

    private ArrayList<Trial> trials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.int_count_experiment_view);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        Experiment experiment = DataManager.getExperiment(id);
        TextView expType = findViewById(R.id.icexp_type);
        TextView expDescription = findViewById(R.id.icexp_desc);
        expType.setText(experiment.getType());
        expDescription.setText(experiment.info.getDescription());
        Button addTrial = findViewById(R.id.addIntCountTrial);
        Button submit = findViewById(R.id.submitTrial);
        TextView intCount = findViewById(R.id.editIntCountTrial);
        addTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intCount.setVisibility(View.VISIBLE);
                addTrial.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validate.lengthInRange((EditText)intCount, 1, Integer.MAX_VALUE, true)){
                    Integer value = Integer.parseInt(intCount.getText().toString());
                    Trial newTrial = new IntCountTrial(value);
                    DataManager.addTrial(newTrial, experiment, trial -> {
                        Log.d("TRIAL", "Trial exception");
                    } , exception -> {
                        Log.d("EXCEPTION", "Exception");
                    });
                    intCount.setVisibility(View.INVISIBLE);
                    addTrial.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(getApplicationContext(), "Trial added", 100);
                }
            }
        });
    }}
