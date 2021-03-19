package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class OwnerActivity extends AppCompatActivity {
    private static final String TAG = "OwnerActivity";
    private Experiment experiment;
    private ArrayList<Trial> trialsArrayList = new ArrayList<>();
    private TrialListAdapter trialListAdapter;

    /**
     * Create The Activity by setting the textview, button and using the adapter to display trials
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));
        
        TextView ownerExperimentNameTextView = findViewById(R.id.OwnerExperimentNameTextView);
        ownerExperimentNameTextView.setText(experiment.info.getDescription());

        TextView experimentTypeTextView = findViewById(R.id.ExperimentTypeTextView);
        experimentTypeTextView.setText(experiment.getType());

        Button endExperimentBtn = findViewById(R.id.EndExperimentBtn);
        endExperimentBtn.setOnClickListener(v ->
            DataManager.endExperiment(experiment, experiment -> finish(), e -> Log.d(TAG, e.toString()))
        );

        DataManager.listen(experiment, this::update);

        initRecyclerView();
    }

    /**
     * Using the Recycler adapter to display each trials
     */
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView trialRecyclerView = findViewById(R.id.trialRecyclerView);

        trialListAdapter = new TrialListAdapter(this, trialsArrayList);
        trialRecyclerView.setAdapter(trialListAdapter);

        trialRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * Update the list using Data Manager
     * @param experiment which the trials of the experiment
     */
    void update(Experiment experiment) {
        trialListAdapter.updateList((ArrayList<Trial>) experiment.getTrials());
    }


}