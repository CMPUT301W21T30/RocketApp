package com.example.rocketapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import com.example.rocketapp.model.trials.Geolocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.view.activities.ExperimentForumActivity;

/**
 * Display view for Experiment
 */
public class ExperimentActivity extends AppCompatActivity {
    private static final String TAG = "ExperimentActivity";
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
    private Button publishExperimentButton;

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

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));

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

        TextView ownerTextView = findViewById(R.id.ownerTextView);
        ownerTextView.setOnClickListener(this::onOwnerClicked);
        ownerTextView.setText(experiment.getOwner().getName());
        TextView experimentType = findViewById(R.id.experimentTypeTextView);
        experimentType.setText(experiment.getType());

        TextView experimentDescription = findViewById(R.id.descriptionTextView);
        experimentDescription.setText(experiment.info.getDescription());

        publishExperimentButton = findViewById(R.id.publishExperimentButton);
        endExperimentButton = findViewById(R.id.endExperimentButton);
        map = findViewById(R.id.mapbtn);
        addTrialButton = findViewById(R.id.addTrialButton);
        statusTextView = findViewById(R.id.endedTextView);

        if(UserManager.getUser().isOwner(experiment)){
            endExperimentButton.setOnClickListener(this::onEndClicked);
            publishExperimentButton.setOnClickListener(this::onPublishClicked);
        } else {
            endExperimentButton.setVisibility(View.GONE);
            publishExperimentButton.setVisibility(View.GONE);
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

        ExperimentManager.listen(experiment, this::update);
        TrialManager.listen(experiment, this::update);
        update(experiment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (experiment.getOwner().equals(UserManager.getUser()))
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

    void onPublishClicked(View view) {

        Log.d("ExperimentActivity", "Publish Clicked Published: " + experiment.isPublished());

        if (experiment.isPublished()){
            Log.e(TAG, "Calling un-publishExperiment");
            ExperimentManager.unpublishExperiment(experiment, this::update, e-> {});

        }
        else {
            Log.e(TAG, "Calling publishExperiment");
            ExperimentManager.publishExperiment(experiment, this::update, e-> {});
        }

    }

    void onEndClicked(View view) {
        ExperimentManager.endExperiment(experiment, exp->{}, e->{});
    }

    void onAddTrialClicked(View view) {
        if (!experiment.info.isGeoLocationEnabled()) {
            new TrialFragment(experiment.getType(), experiment.info.isGeoLocationEnabled(), newTrial -> {
                TrialManager.addTrial(newTrial, experiment, t -> {
                    Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show();
                }, e -> {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                });
            }).show(getSupportFragmentManager(), "ADD_TRIAL");
        }
        else{
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.getResult() == null) {
                            Log.d(TAG, "Result was null");
                            return;
                        }
                        Geolocation location = new Geolocation(task.getResult().getLongitude(), task.getResult().getLatitude());
                        new TrialFragment(experiment.getType(), experiment.info.isGeoLocationEnabled(), newTrial -> {
                            newTrial.setLocation(location);
                            TrialManager.addTrial(newTrial, experiment, t -> {
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

    void onOwnerClicked(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("id", experiment.getOwnerId());
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

        statusTextView.setVisibility(experiment.isActive() ? View.INVISIBLE : View.VISIBLE);
        addTrialButton.setVisibility(experiment.isActive() ? View.VISIBLE : View.INVISIBLE);

        if (UserManager.getUser().isOwner(experiment)) {
            Log.d("ExperimentActivity", "Update Published: " + experiment.isPublished() + " Active " + experiment.isActive());

            publishExperimentButton.setText(experiment.isPublished() ? "Unpublish" : "Publish");
            endExperimentButton.setVisibility(experiment.isActive() ? View.VISIBLE : View.GONE);
        } else {
            Log.d("ExperimentActivity", "Not Owner");
        }
    }
}