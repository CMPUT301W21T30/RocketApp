package com.example.rocketapp.view;


import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.FirestoreDocument;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.Collections;

public class Histogram extends AppCompatActivity {

    BarChart barChart; //Source: https://www.youtube.com/watch?v=pi1tq-bp7uA
    Experiment experiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);

        barChart = (BarChart) findViewById(R.id.histogram);
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        ArrayList<Trial> done = new ArrayList<Trial>();

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        System.out.println(experiment);
        ArrayList<Trial> trials = (ArrayList<Trial>) experiment.getFilteredTrials();
        for (int i = 0; i < experiment.getFilteredTrials().size(); i++) {
            System.out.println(experiment.getFilteredTrials().get(i));
            int freq = 0;
            int found = 0;
            for (int k = 0; k < done.size(); k++) {
                if (experiment.getFilteredTrials().get(i).getId() == (done.get(k).getId())) {
                    found = found + 1;
                }
            }
            System.out.println("FOUND");
            System.out.println(found);
            if (found == 0) {
                for (int j = i; j < experiment.getFilteredTrials().size(); j++) {
                    System.out.println(experiment.getFilteredTrials().get(j));
                    System.out.println("YOYOYO");
                    System.out.println(experiment.getFilteredTrials().get(i).getValueString());
                    System.out.println(experiment.getFilteredTrials().get(j).getValueString());
                    if (experiment.getFilteredTrials().get(i).getValueString().equals(experiment.getFilteredTrials().get(j).getValueString())) {
                        System.out.println("ABCD");
                        freq = freq + 1;
                        done.add(experiment.getFilteredTrials().get(j));
                    }
                }
                if (experiment.getFilteredTrials().get(i).getValueString().equals("True")) {
                    barEntries.add(new BarEntry(Float.parseFloat("1"), freq));
                }
                else if(experiment.getFilteredTrials().get(i).getValueString().equals("False")){
                    barEntries.add(new BarEntry(Float.parseFloat("0"), freq));
                }
                else{
                    barEntries.add(new BarEntry(Float.parseFloat(experiment.getFilteredTrials().get(i).getValueString()), freq));
                }
            }
        }
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
