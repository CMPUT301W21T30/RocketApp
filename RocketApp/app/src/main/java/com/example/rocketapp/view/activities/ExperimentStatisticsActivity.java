package com.example.rocketapp.view.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.graphs.Histogram;
import com.example.rocketapp.model.graphs.TimePlot;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;

public class ExperimentStatisticsActivity extends AppCompatActivity {
    Experiment experiment;
    Histogram histogram;
    TimePlot timePlot;
    BarChart histo; //To disable view if Count
    TextView ht;    //To disable title if Count

    /**
     * Creates Histogram and Timeplot
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_statistics);
        System.out.println("The experiment ID is: " +getIntent().getSerializableExtra(Experiment.ID_KEY));
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));
        histogram = new Histogram(experiment);
        timePlot = new TimePlot(experiment);
        ArrayList<Trial> trials = (ArrayList<Trial>) experiment.getTrials(false);
        histo = findViewById(R.id.histogram);
        ht = findViewById(R.id.textView12);
        histogram.setBarChart(findViewById(R.id.histogram));
        histogram.createHistogramView(trials);
        timePlot.setLineChart(findViewById(R.id.time_plot));
        timePlot.createTimePlotView(trials);
        if(experiment.getType().equals("Count")){
            histo.setVisibility(View.GONE);
            ht.setVisibility(View.GONE);
        }

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
