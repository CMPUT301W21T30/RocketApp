package com.example.rocketapp.view;


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
import java.util.Collections;
import java.util.Date;

public class Histogram extends AppCompatActivity {

    BarChart barChart; //Source: https://www.youtube.com/watch?v=pi1tq-bp7uA
    LineChart lineChart; //Source: https://www.youtube.com/watch?v=yrbgN2UvKGQ&list=PLFh8wpMiEi89LcBupeftmAcgDKCeC24bJ&ab_channel=SarthiTechnology
    Experiment experiment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);

        barChart = (BarChart) findViewById(R.id.histogram);
        lineChart = (LineChart) findViewById(R.id.time_plot);
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        ArrayList<Trial> done = new ArrayList<Trial>();
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        ArrayList<Trial> trials = (ArrayList<Trial>) experiment.getFilteredTrials();
        LineDataSet lineDataSet = new LineDataSet(dataValues(trials), "Data set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();


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

    private ArrayList<Entry> dataValues(ArrayList<Trial> trials) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Date date = new Date((long) value);
                DateFormat df = new SimpleDateFormat("dd/MM");
                return (df).format(date);// xVal is a string array
            }
        });
        ArrayList<Entry> dataValue = new ArrayList<Entry>();
        float mean = 0;
        ArrayList<String> done = new ArrayList<>();
        for (int i = 0; i < trials.size(); i++) {
            mean = experiment.getMean(trials.get(i).getTimestamp().toDate());
            System.out.println("the mean is " + mean);//testing getMean(Date), tests confirm it works.
            Entry entry = new Entry(trials.get(i).getTimestamp().toDate().getTime(), mean);
            DateFormat df = new SimpleDateFormat("dd/MM");
            String sdf = (df).format(trials.get(i).getTimestamp().toDate().getTime());
            int found = 0;
            for (int k = 0; k < done.size(); k++) {
                if(sdf.equals(done.get(k))){
                    found = found + 1;
                }
            }
            if(found==0){
                dataValue.add(entry);
                done.add(sdf);
            }
        }
        System.out.println(dataValue);
        System.out.println(dataValue);
        return dataValue;
    }
}
