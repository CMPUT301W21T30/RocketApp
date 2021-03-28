package com.example.rocketapp.view.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.example.rocketapp.view.TrialListAdapter;

import java.util.ArrayList;

public class ExperimentEditActivity extends AppCompatActivity {
    private static final String TAG = "ExperimentEditAct";
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

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        
        TextView ownerExperimentNameTextView = findViewById(R.id.OwnerExperimentNameTextView);
        ownerExperimentNameTextView.setText(experiment.info.getDescription());

        TextView experimentTypeTextView = findViewById(R.id.ExperimentTypeTextView);
        experimentTypeTextView.setText(experiment.getType());

        Button endExperimentBtn = findViewById(R.id.EndExperimentBtn);
        endExperimentBtn.setOnClickListener(v ->
            ExperimentManager.endExperiment(experiment, experiment -> finish(), e -> Log.d(TAG, e.toString()))
        );

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initRecyclerView();

        ExperimentManager.listen(experiment, this::update);
        TrialManager.listen(experiment, this::update);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Using the Recycler adapter to display each trials
     */
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView trialRecyclerView = findViewById(R.id.trialRecyclerView);

        trialListAdapter = new TrialListAdapter(this, trialsArrayList, (holder, trial) -> {
            // Toggle ignored
            trial.setIgnored(!trial.getIgnored());
            TrialManager.update(trial, experiment, t -> Toast.makeText(this, (trial.getIgnored() ? "Ignore" : "Include") + " Trial: " + trial.getValueString(), Toast.LENGTH_SHORT).show(), e ->{});
        });
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