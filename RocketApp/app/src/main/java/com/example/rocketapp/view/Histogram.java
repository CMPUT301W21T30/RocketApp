package com.example.rocketapp.view;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class Histogram extends AppCompatActivity {

    BarChart barChart; //Source: https://www.youtube.com/watch?v=pi1tq-bp7uA
    Experiment experiment;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView(R.layout.graph_activity);

        barChart = (BarChart)findViewById(R.id.histogram);
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        ArrayList<Trial> trials = (ArrayList<Trial>) experiment.getTrials();
    }
}
