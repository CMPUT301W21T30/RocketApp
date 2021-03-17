package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class AllExperiments extends AppCompatActivity {
    private static final String TAG = "AllExperimentsActivity";
    private ArrayList<Experiment> experimentsNotOwned;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_experiments);
        initRecyclerViewNotOwned();
    }

    private void initRecyclerViewNotOwned() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewNonOwner);

        experimentsNotOwned = DataManager.getNotSubscribedExperimentsArrayList();

        experimentRecyclerView.setAdapter(new ExperimentListAdapter(experimentsNotOwned, experiment -> {
            DataManager.subscribe(experiment,() -> {
                Log.d(TAG, "Subscribed");
                finish();
            } ,exception -> {
                Log.d(TAG, "Could not be subscribed.");
            });
        }));
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}