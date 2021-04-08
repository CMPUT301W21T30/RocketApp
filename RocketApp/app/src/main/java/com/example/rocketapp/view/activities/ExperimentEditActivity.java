package com.example.rocketapp.view.activities;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.helpers.Validate;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.example.rocketapp.view.adapters.TrialListAdapter;

import java.util.ArrayList;

public class ExperimentEditActivity extends RocketAppActivity {
    private static final String TAG = "ExperimentEditAct";
    private Experiment<?> experiment;
    private final ArrayList<Trial> trialsArrayList = new ArrayList<>();
    private TrialListAdapter trialListAdapter;
    private EditText descriptionEditText, regionEditText, minTrialsEditText;

    /**
     * Create The Activity by setting the textview, button and using the adapter to display trials
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_edit);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));
        
        descriptionEditText = findViewById(R.id.OwnerExperimentNameTextView);
        regionEditText = findViewById(R.id.editTextRegion);
        minTrialsEditText = findViewById(R.id.editTextMinTrials);

        findViewById(R.id.textViewUpdate).setOnClickListener(v -> {
            if (Validate.lengthInRange(descriptionEditText, 3, 100, true) &&
                Validate.lengthInRange(regionEditText, 2, 40, true) &&
                Validate.intInRange(minTrialsEditText, 1, Integer.MAX_VALUE, true)) {

                experiment.info.setDescription(descriptionEditText.getText().toString());
                experiment.info.setRegion(regionEditText.getText().toString());
                experiment.info.setMinTrials(Integer.parseInt(minTrialsEditText.getText().toString()));

                ExperimentManager.update(experiment,
                        experiment -> Toast.makeText(this, "Experiment updated.", Toast.LENGTH_SHORT).show(),
                        exception-> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show());
            }
            toggleKeyboard(false);
        });

        TextView experimentTypeTextView = findViewById(R.id.ExperimentTypeTextView);
        experimentTypeTextView.setText(experiment.getType());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initRecyclerView();

        ExperimentManager.listen(experiment, this::update);
        TrialManager.listen(experiment, this::update);
        update(experiment);
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

        trialRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * Update the list according to the current experiment state
     * @param experiment experiment object
     */
    void update(Experiment<?> experiment) {
        trialListAdapter.updateList(experiment.getTrials());
        minTrialsEditText.setText(String.valueOf(experiment.info.getMinTrials()));
        descriptionEditText.setText(experiment.info.getDescription());
        regionEditText.setText(experiment.info.getRegion());
    }


}