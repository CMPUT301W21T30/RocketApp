package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ExperimentsListActivity extends AppCompatActivity implements ExperimentDialog.OnInputListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_list);

        TextView textView = findViewById(R.id.textView);
        textView.setText(DataManager.getUser().getName());
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText(String.valueOf(DataManager.getIsOwner()));
    }


    @Override
    public void returnExperiment(Experiment exp) {
        Log.d("ExperimentsListActivity", "sendExperiment: got the experiment" + exp.info.getDescription());
        //TODO: handle received experiment
    }
}