package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ExperimentView extends AppCompatActivity {

    private ArrayList<Trial> trials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_view);
        Intent i = getIntent();
        String type = i.getStringExtra("type");
        String description = i.getStringExtra("description");
        TextView expType = findViewById(R.id.exp_type);
        TextView expDescription = findViewById(R.id.exp_desc);
        expType.setText(type);
        expDescription.setText(description);
    }
}