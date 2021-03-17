package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class AllExperiments extends AppCompatActivity {

    private static final String TAG = "AllExperimentsActivity";
    ArrayList<Experiment> experimentsNotOwned;
    ExperimentRecyclerViewNotOwnedAdapter adapterNotOwned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_experiments);
        initRecyclerViewNotOwned();
    }

    private void initRecyclerViewNotOwned() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewNonOwner);

        experimentsNotOwned = DataManager.getNotOwnedExperimentsArrayList();
        adapterNotOwned = new ExperimentRecyclerViewNotOwnedAdapter(this, experimentsNotOwned);
        experimentRecyclerView.setAdapter(adapterNotOwned);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}