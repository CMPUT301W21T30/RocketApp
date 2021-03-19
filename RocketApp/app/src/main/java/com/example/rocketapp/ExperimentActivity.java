package com.example.rocketapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Display view for Experiment
 */
public class ExperimentActivity extends AppCompatActivity {

    private Experiment experiment;
    private TextView meanTextView;
    private TextView medianTextView;
    private TextView stdDevTextView;
    private TextView regionView;
    private TextView minTrialsTextView;
    private TextView statusTextView;
    private Button addTrialButton;
    private Button endExperimentButton;
    private Button unpublishExperimentButton;

    /**
     * Setup the view for Experiment
     * Display type, description, region and Minimum Trials.
     * Button to add trials
     * @param savedInstanceState
     *          passed state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));

        meanTextView = findViewById(R.id.meanView);
        medianTextView = findViewById(R.id.medianValue);
        stdDevTextView = findViewById(R.id.stdDevVal);
        regionView  = findViewById(R.id.regionView);
        regionView.setText(experiment.info.getRegion());

        minTrialsTextView = findViewById(R.id.minTrialsView);
        minTrialsTextView.setText(String.valueOf(experiment.info.getMinTrials()));

        TextView meanText = findViewById(R.id.meanText);
        if (experiment.getType().equals(BinomialExperiment.TYPE)) {
            meanText.setText("Success Ratio");
        }

        TextView experimentType = findViewById(R.id.experimentTypeTextView);
        experimentType.setText(experiment.getType());

        TextView experimentDescription = findViewById(R.id.descriptionTextView);
        experimentDescription.setText(experiment.info.getDescription());

        unpublishExperimentButton = findViewById(R.id.unpublishExperimentButton);
        endExperimentButton = findViewById(R.id.endExperimentButton);
        addTrialButton = findViewById(R.id.addTrialButton);
        statusTextView = findViewById(R.id.endedTextView);

        if(!experiment.getOwner().equals(DataManager.getUser())){
            endExperimentButton.setVisibility(View.GONE);
            unpublishExperimentButton.setVisibility(View.GONE);
        } else {
            endExperimentButton.setOnClickListener(this::onToggleEndClicked);
            unpublishExperimentButton.setOnClickListener(this::onUnpublishClicked);
        }

        addTrialButton.setOnClickListener(this::onAddTrialClicked);

        findViewById(R.id.forumButton).setOnClickListener(this::onForumButtonClicked);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        DataManager.listen(experiment, this::update);
        update(experiment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (experiment.getOwner().equals(DataManager.getUser()))
        getMenuInflater().inflate(R.menu.experiment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuButton:
                Intent intent = new Intent(this, ExperimentEditActivity.class);
                intent.putExtra("id", experiment.getId());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void onUnpublishClicked(View view) {
        switch(experiment.getState()) {
            case PUBLISHED:
            case ENDED:
                DataManager.unpublishExperiment(experiment, experiment-> {}, e-> {});
                break;
            case UNPUBLISHED:
                break;
        }
    }

    void onToggleEndClicked(View view) {
        switch(experiment.getState()) {
            case PUBLISHED:
                DataManager.endExperiment(experiment, experiment-> {}, e-> {});
                break;
            case ENDED:
            case UNPUBLISHED:
                DataManager.publishExperiment(experiment, experiment-> {}, e-> {});
                break;
        }
    }

    void onAddTrialClicked(View view) {
        new TrialFragment(experiment.getType(), newTrial -> {
            DataManager.addTrial(newTrial, experiment, t -> {
                Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show();
            }, e -> {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            });
        }).show(getSupportFragmentManager(), "ADD_TRIAL");
    }

    void onForumButtonClicked(View view) {
        Intent intent = new Intent(this, ExperimentForumActivity.class);
        intent.putExtra("id", experiment.getId());
        startActivity(intent);
    }

    /**
     * Update and display experiment statistics.
     * @param experiment
     *          Experiment of current view
     */
    void update(Experiment experiment) {
        // Could add all updates here
        meanTextView.setText(String.valueOf(experiment.getMean()));
        medianTextView.setText(String.valueOf(experiment.getMedian()));
        stdDevTextView.setText(String.valueOf(experiment.getStdDev()));

        switch(experiment.getState()) {
            case PUBLISHED:
                statusTextView.setVisibility(View.GONE);
                addTrialButton.setVisibility(View.VISIBLE);
                break;
            case ENDED:
                addTrialButton.setVisibility(View.GONE);
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText("Experiment has ended.");
                break;
            case UNPUBLISHED:
                addTrialButton.setVisibility(View.GONE);
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText("Experiment is not published.");
                break;
        }

        if (experiment.getOwner().getName().equals(DataManager.getUser().getName())) {
            switch(experiment.getState()) {
                case PUBLISHED:
                    unpublishExperimentButton.setVisibility(View.GONE);
                    endExperimentButton.setText("End");
                    break;
                case ENDED:
                    endExperimentButton.setText("Publish");
                    unpublishExperimentButton.setVisibility(View.VISIBLE);
                    break;
                case UNPUBLISHED:
                    endExperimentButton.setText("Publish");
                    unpublishExperimentButton.setVisibility(View.GONE);
                    break;
            }
        }
    }
}