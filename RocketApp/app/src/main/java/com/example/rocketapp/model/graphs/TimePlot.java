package com.example.rocketapp.model.graphs;

import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
//Citation - https://www.youtube.com/watch?v=yrbgN2UvKGQ
//Youtube
//Sarthi Technology - https://www.youtube.com/channel/UCWOpKx--cc-48bze5T4vYUg
//Sep 9, 2018

/**
 * Creates and displays mean of experiment over time at an interval of 1 day.
 */
public class TimePlot {
    private LineChart lineChart; //Source: https://www.youtube.com/watch?v=yrbgN2UvKGQ&list=PLFh8wpMiEi89LcBupeftmAcgDKCeC24bJ&ab_channel=SarthiTechnology
    private final Experiment experiment;

    /**
     * Initialize a time plot for a given experiment
     * @param experiment
     */
    public TimePlot(Experiment experiment){
        this.experiment = experiment;
    }

    /**
     *
     * @return time plot line chart
     */
    public LineChart getLineChart() {
        return lineChart;
    }

    /**
     * Sets time plot line chart
     * @param lineChart
     */
    public void setLineChart(LineChart lineChart) {
        this.lineChart = lineChart;
    }

    /**
     * Create time plot view for a given experiment
     * @param trials
     */
    //Citation - https://stackoverflow.com/a/54861945
    //stackoverflow
    //Gayathri - https://stackoverflow.com/users/9890588/gayathri
    //Feb 25 '19
    //License - cc by-sa
    public void createTimePlotView(ArrayList<Trial> trials) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Date date = new Date((long) value);     //Citation - https://stackoverflow.com/a/31624874
                DateFormat df = new SimpleDateFormat("dd/MM");  //TheStoneFox - https://stackoverflow.com/users/1219336/thestonefox
                return (df).format(date);   //License - cc by-sa
            }
        });
        ArrayList<Entry> dataValue = new ArrayList<Entry>();
        ArrayList<Entry> mockData = new ArrayList<Entry>();
        float mean = 0;
        ArrayList<String> done = new ArrayList<>();
        for (int i = 0; i < trials.size(); i++) {
            mean = experiment.getMean(trials.get(i).getTimestamp().toDate());
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
            else{
                for(int j = 0; j<dataValue.size(); j++){
                    if((sdf.equals((df).format(dataValue.get(j).getX())))&&(entry.getX()>dataValue.get(j).getX())){
                        dataValue.set(j, entry);
                    }
                }
            }
        }

        Collections.sort(dataValue, new Comparator<Entry>() {
            @Override
            public int compare(Entry entry, Entry t1) {
                return (int) (entry.getX()-t1.getX());
            }
        });
        LineDataSet lineDataSet = new LineDataSet(dataValue, "Data set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
    }
}
