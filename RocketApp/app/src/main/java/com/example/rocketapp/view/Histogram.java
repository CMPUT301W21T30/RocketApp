package com.example.rocketapp.view;


import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;

public class Histogram extends AppCompatActivity {

    BarChart barChart; //Source: https://www.youtube.com/watch?v=pi1tq-bp7uA
    Experiment experiment;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView(R.layout.graph_activity);

        barChart = (BarChart)findViewById(R.id.histogram);
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        ArrayList<Trial> done = new ArrayList<Trial>();
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        ArrayList<Trial> trials = (ArrayList<Trial>) experiment.getFilteredTrials();

        for (int i = 0; i < trials.size(); i++) {
            int freq = 0;
            int found = 0;
            for (int k = 0; k < done.size(); k++) {
                if (trials.get(i).getId() == (done.get(k).getId())) {
                    found = found + 1;
                }
            }

            if (found == 0) {
                //Handling Non binomial Experiments
                for (int j = i; j < trials.size(); j++) {

                    //Used for handling measurements (grouping experiments less than 1 unit away from each other to avoid bar overlap)
                    float delta = Float.parseFloat(trials.get(i).getValueString()) - Float.parseFloat(trials.get(j).getValueString());

                    //Handling Counts
                    if (trials.get(i).getValueString().equals(trials.get(j).getValueString())
                    || (-1 < delta && delta < 1) /*handling measurements of previously stated case*/) {
                        freq = freq + 1;
                        done.add(trials.get(j));
                    }
                }

                //Handling Binomial case:
                if (trials.get(i).getValueString().equals("True")) {
                    barEntries.add(new BarEntry(Float.parseFloat("1"), freq));
                }
                else if(trials.get(i).getValueString().equals("False")){
                    barEntries.add(new BarEntry(Float.parseFloat("0"), freq));
                }

                //Adding the non-binomial cases to barEntries
                else{
                    barEntries.add(new BarEntry(Float.parseFloat(trials.get(i).getValueString()), freq));
                }
            }
        }

        //Creating the graph
        BarDataSet barDataSet = new BarDataSet(barEntries, "Trials");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColors(Collections.singletonList(Color.BLACK));
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Experiment Trials");
        barChart.animateY(2000);
    }
}
