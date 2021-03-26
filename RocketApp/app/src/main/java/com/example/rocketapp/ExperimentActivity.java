package com.example.rocketapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

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
    private Button map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean granted = false;

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
        map = findViewById(R.id.mapbtn);
        addTrialButton = findViewById(R.id.addTrialButton);
        statusTextView = findViewById(R.id.endedTextView);

        if(!experiment.getOwner().equals(DataManager.getUser())){
            endExperimentButton.setVisibility(View.GONE);
            unpublishExperimentButton.setVisibility(View.GONE);
        } else {
            endExperimentButton.setOnClickListener(this::onToggleEndClicked);
            unpublishExperimentButton.setOnClickListener(this::onUnpublishClicked);
        }

        if(experiment.info.isGeoLocationEnabled()) {
            map.setOnClickListener(this::mapClicked);
        }
        else{
            map.setVisibility(View.GONE);
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

    void mapClicked(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("id", experiment.getId());
        startActivity(intent);
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
        if (!experiment.info.isGeoLocationEnabled()) {
            new TrialFragment(experiment.getType(), newTrial -> {
                DataManager.addTrial(newTrial, experiment, t -> {
                    Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show();
                }, e -> {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                });
            }).show(getSupportFragmentManager(), "ADD_TRIAL");
        }
        else{
            Log.d("Tag", "Starting geo");
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                Log.d("TAG", "Permission granted");
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        System.out.println(task.getResult().getLatitude());
                        System.out.println(task.getResult().getLongitude());
                        new TrialFragment(experiment.getType(), newTrial -> {
                            newTrial.setLatitude(task.getResult().getLatitude());
                            newTrial.setLongitude(task.getResult().getLongitude());
                            DataManager.addTrial(newTrial, experiment, t -> {
                                Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show();
                            }, e -> {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            });
                        }).show(getSupportFragmentManager(), "ADD_TRIAL");
                    }
                });
            }
            else {
                Toast toast = Toast.makeText(this.getApplicationContext(), "Permission needed for this experiment", Toast.LENGTH_SHORT);
                toast.show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                if(granted){
                    System.out.println("HERE");
                }
                else{
                    System.out.println("NOT HERE");
                }
            }
        }
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