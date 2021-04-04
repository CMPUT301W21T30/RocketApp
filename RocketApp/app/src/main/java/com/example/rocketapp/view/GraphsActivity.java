package com.example.rocketapp.view;

import android.os.Bundle;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class GraphsActivity extends AppCompatActivity {
    Experiment experiment;
    Histogram histogram;
    TimePlot timePlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);
        System.out.println("The experiment ID is: " +getIntent().getSerializableExtra("id"));
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        histogram = new Histogram(experiment);
        timePlot = new TimePlot(experiment);
        ArrayList<Trial> trials = (ArrayList<Trial>) experiment.getFilteredTrials();
        histogram.setBarChart((BarChart) findViewById(R.id.histogram));
        histogram.createHistogramView(trials);
        timePlot.setLineChart((LineChart) findViewById(R.id.time_plot));
        timePlot.createTimePlotView(trials);
    }
}
