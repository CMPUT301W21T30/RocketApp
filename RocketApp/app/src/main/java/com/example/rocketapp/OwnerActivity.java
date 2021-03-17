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

    TextView OwnerExperimentNameTextView;
    TextView ExperimentTypeTextView;
    Button EndExperimentBtn;
    Experiment experiment;
    ArrayList<? extends Trial> trialsArrayList = new ArrayList<>();
    TrialListAdapter trialListAdapter;

    private static final String TAG = "TrialsListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        OwnerExperimentNameTextView = findViewById(R.id.OwnerExperimentNameTextView);
        ExperimentTypeTextView = findViewById(R.id.ExperimentTypeTextView);
        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));

        OwnerExperimentNameTextView.setText(experiment.info.getDescription());
        ExperimentTypeTextView.setText(experiment.getType());

        // TODO: End Experiment give error for not the Owner
        EndExperimentBtn = findViewById(R.id.EndExperimentBtn);
        EndExperimentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.endExperiment(experiment, (s) -> {}, (e) -> {});
                finish();
            }
        });

        DataManager.listen(experiment, this::update);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView trialRecyclerView = findViewById(R.id.trialRecyclerView);

        trialListAdapter = new TrialListAdapter(this, trialsArrayList);
        trialRecyclerView.setAdapter(trialListAdapter);

        trialRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//         Set up Swipe Gesture for Published and UnPublished

    }


    void update(Experiment experiment) {
        // Could add all updates here
        trialsArrayList = experiment.getTrials();
        initRecyclerView();
    }


}