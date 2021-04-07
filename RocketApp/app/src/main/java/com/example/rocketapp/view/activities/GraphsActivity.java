package com.example.rocketapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.graphs.Histogram;
import com.example.rocketapp.model.graphs.TimePlot;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class GraphsActivity extends AppCompatActivity {
    Experiment<?> experiment;
    Histogram histogram;
    TimePlot timePlot;

    /**
     * Creates GraphActivity view based on input
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);
        System.out.println("The experiment ID is: " +getIntent().getSerializableExtra(Experiment.ID_KEY));
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));
        histogram = new Histogram(experiment);
        timePlot = new TimePlot(experiment);
        ArrayList<Trial> trials = (ArrayList<Trial>) experiment.getFilteredTrials();
        histogram.setBarChart(findViewById(R.id.histogram));
        histogram.createHistogramView(trials);
        timePlot.setLineChart(findViewById(R.id.time_plot));
        timePlot.createTimePlotView(trials);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Access GraphsActivity from options menu
     * @param item
     * @return boolean indicating activity status
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
