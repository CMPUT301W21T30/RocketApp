package com.example.rocketapp.model.graphs;


import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.trials.MeasurementTrial;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Histogram extends AppCompatActivity {

    private BarChart barChart; //Source: https://www.youtube.com/watch?v=pi1tq-bp7uA
    private Experiment experiment;

    public BarChart getBarChart() {
        return barChart;
    }

    public void setBarChart(BarChart barChart) {
        this.barChart = barChart;
    }

    public Histogram(Experiment experiment){
        this.experiment = experiment;
    }

    public void createHistogramView(ArrayList<Trial> trials){
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        ArrayList<Trial> done = new ArrayList<Trial>();
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
                    if (!trials.get(i).getType().equals(BinomialTrial.TYPE)) {
                        float delta = Float.parseFloat(trials.get(i).getValueString()) - Float.parseFloat(trials.get(j).getValueString());
                        if (-0.5 < delta && delta < 0.5) {
                            freq = freq + 1;
                            done.add(trials.get(j));
                        }
                    }

                    //Handling Counts
                    if (trials.get(i).getType().equals(BinomialTrial.TYPE)) {
                        if (trials.get(i).getValueString().equals(trials.get(j).getValueString())) {
                            freq = freq + 1;
                            done.add(trials.get(j));
                        }
                    }
                }

                //Handling Binomial case:
                if (trials.get(i).getValueString().equals("True")) {
                    barEntries.add(new BarEntry(Float.parseFloat("1"), freq));
                } else if (trials.get(i).getValueString().equals("False")) {
                    barEntries.add(new BarEntry(Float.parseFloat("0"), freq));
                }

                //Adding the non-binomial cases to barEntries
                else {
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
