package com.example.rocketapp.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.trials.Geolocation;
import com.example.rocketapp.view.fragments.TrialFragment;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.Experiment;

/**
 * Display view for Experiment
 */
public class ExperimentActivity extends RocketAppActivity {
    private static final String TAG = "ExperimentActivity";
    private final int locationPermissionRequestCode = 100;
    private Experiment<?> experiment;
    private TextView meanTextView;
    private TextView medianTextView;
    private TextView stdDevTextView;
    private TextView regionTextView;
    private TextView minTrialsTextView;
    private TextView statusTextView;
    private TextView descriptionTextView;
    private TextView ownerTextView;
    private TextView publishedTextView;
    private TextView Q1TextView;
    private TextView Q3TextView;
    private TextView experimentTypeTextView;
    private TextView trialCountTextView;
    private Button addTrialButton;
    private TextView totalText;

    private MenuItem publishExperimentMenuItem;
    private MenuItem endExperimentMenuItem;

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

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));

        meanTextView = findViewById(R.id.meanView);
        medianTextView = findViewById(R.id.medianValue);
        stdDevTextView = findViewById(R.id.stdDevVal);
        Q1TextView = findViewById(R.id.Q1View);
        Q3TextView = findViewById(R.id.Q3View);
        regionTextView = findViewById(R.id.regionView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        minTrialsTextView = findViewById(R.id.minTrialsView);
        statusTextView = findViewById(R.id.statusTextView);
        publishedTextView = findViewById(R.id.publishedTextView);
        experimentTypeTextView = findViewById(R.id.experimentTypeTextView);
        trialCountTextView = findViewById(R.id.trialCountTextView);

        findViewById(R.id.loginBtn).setOnClickListener(v -> openExperimentIntent(ExperimentStatisticsActivity.class));

        if (!UserManager.getUser().isOwner(experiment))
            publishedTextView.setVisibility(View.GONE);

        if (experiment.getType().equals(BinomialExperiment.TYPE))
            ((TextView) findViewById(R.id.meanText)).setText(R.string.mean_label_binomial_trial);


        ownerTextView = findViewById(R.id.ownerTextView);
        ownerTextView.setOnClickListener(this::onOwnerClicked);

        addTrialButton = findViewById(R.id.addTrialButton);
        addTrialButton.setOnClickListener(v -> onAddTrialClicked());

        Button mapButton = findViewById(R.id.mapbtn);
        if (experiment.info.isGeoLocationEnabled()) {
            mapButton.setOnClickListener(this::mapClicked);
        } else {
            mapButton.setVisibility(View.GONE);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ExperimentManager.listen(experiment, this::update);
        TrialManager.listen(experiment, this::update);
        update(experiment);
    }

    private void isItCount() {
        medianTextView.setVisibility(View.GONE);
        stdDevTextView.setVisibility(View.GONE);
        Q1TextView.setVisibility(View.GONE);
        Q3TextView.setVisibility(View.GONE);
        findViewById(R.id.medianText).setVisibility(View.GONE);
        findViewById(R.id.stdDevText).setVisibility(View.GONE);
        findViewById(R.id.Q3Text).setVisibility(View.GONE);
        findViewById(R.id.Q1Text).setVisibility(View.GONE);
        totalText.setText("Total");
    }


    @Override
    protected void onResume() {
        super.onResume();
        update(experiment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (UserManager.getUser().isOwner(experiment)) {
            getMenuInflater().inflate(R.menu.menu_experiment, menu);
            endExperimentMenuItem = menu.findItem(R.id.endExperimentMenuItem);
            publishExperimentMenuItem = menu.findItem(R.id.publishExperimentMenuItem);
            publishExperimentMenuItem.setTitle(experiment.isPublished() ? "Un-publish Experiment" : "Publish Experiment");
        } else {
            getMenuInflater().inflate(R.menu.menu_experimenter, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.editExperimentMenuItem:
                openExperimentIntent(ExperimentEditActivity.class);
                return true;
            case R.id.registerBarcodeMenuItem:
                openExperimentIntent(RegisterBarcodeActivity.class);
                return true;
            case R.id.experimentForumMenuItem:
                openExperimentIntent(ExperimentForumActivity.class);
                return true;
            case R.id.generateQRcodeMenuItem:
                openExperimentIntent(GenerateQRcodeActivity.class);
                return true;
            case R.id.publishExperimentMenuItem:
                onPublishExperimentClicked();
                return true;
            case R.id.endExperimentMenuItem:
                onEndExperimentClicked();
                return true;
            case R.id.experimentStatisticsMenuItem:
                openExperimentIntent(ExperimentStatisticsActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void openExperimentIntent(Class<?> experimentClass) {
        Intent experimentIntent = new Intent(this, experimentClass);
        experimentIntent.putExtra(Experiment.ID_KEY, experiment.getId());
        startActivity(experimentIntent);
    }


    void mapClicked(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Experiment.ID_KEY, experiment.getId());
        startActivity(intent);
    }


    void onPublishExperimentClicked() {
        if (experiment.isPublished()) {
            ExperimentManager.unpublishExperiment(experiment, this::update, e -> Log.e(TAG, e.getMessage()));
        } else {
            ExperimentManager.publishExperiment(experiment, this::update, e -> Log.e(TAG, e.getMessage()));
        }
    }


    void onEndExperimentClicked() {
        ExperimentManager.endExperiment(experiment, this::update, e -> Log.e(TAG, e.getMessage()));
    }


    void onAddTrialClicked() {
        if (!experiment.info.isGeoLocationEnabled()) {
            new TrialFragment("Add " + experiment.getType() + " Trial", experiment, newTrial ->
                TrialManager.addTrial(newTrial, experiment,
                        t -> Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show(),
                        e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show())
                ).show(getSupportFragmentManager(), "ADD_TRIAL");
        } else {
            getLocation(location -> {
                if (location == null) return;
                new TrialFragment(experiment, newTrial -> {
                    newTrial.setLocation(new Geolocation(location));
                    TrialManager.addTrial(newTrial, experiment,
                            t -> Toast.makeText(getApplicationContext(), newTrial.getType() + " added", Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
                }).show(getSupportFragmentManager(), "ADD_TRIAL");
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == locationPermissionRequestCode) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                onAddTrialClicked();
            } else {
                Toast.makeText(getApplicationContext(), "Must enable location permission in android settings.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void getLocation(ObjectCallback<Location> onSuccess) {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, locationPermissionRequestCode)) return;

        // Get last location
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(location -> {

            if (location != null) {
                onSuccess.callBack(location);

            } else {
                // Request location update
                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
                        LocationRequest.create()
                            .setInterval(60000)
                            .setFastestInterval(5000)
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setNumUpdates(1),
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location lastLocation = null;
                                if (locationResult == null) {
                                    return;
                                }
                                for (Location location : locationResult.getLocations()) {
                                    if (location != null) {
                                        lastLocation = location;
                                    }
                                }
                                if (lastLocation != null) onSuccess.callBack(lastLocation);
                            }
                        },
                        null);
            }
        });
    }

    void onOwnerClicked(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(Experiment.ID_KEY, experiment.getOwnerId());
        startActivity(intent);
    }

    /**
     * Update and display experiment statistics.
     * @param experiment
     *          Experiment of current view
     */
    void update(Experiment<?> experiment) {
        descriptionTextView.setText(experiment.info.getDescription());
        ownerTextView.setText(experiment.getOwner().getName());
        meanTextView.setText(String.valueOf(experiment.getMean()));
        Q1TextView.setText(String.valueOf(experiment.getBottomQuartile()));
        Q3TextView.setText(String.valueOf(experiment.getTopQuartile()));
        medianTextView.setText(String.valueOf(experiment.getMedian()));
        stdDevTextView.setText(String.valueOf(experiment.getStdDev()));
        minTrialsTextView.setText(String.valueOf(experiment.info.getMinTrials()));
        regionTextView.setText(experiment.info.getRegion());
        experimentTypeTextView.setText(experiment.getType() + " Experiment");
        trialCountTextView.setText(String.valueOf(experiment.getTrials().size()));

        statusTextView.setText(experiment.isActive() ? "Active" : "Ended");
        statusTextView.setTextColor(experiment.isActive() ? Color.GREEN : Color.RED);

        addTrialButton.setVisibility(experiment.isActive() ? View.VISIBLE : View.INVISIBLE);

        if (UserManager.getUser().isOwner(experiment)) {
            if (publishExperimentMenuItem != null) publishExperimentMenuItem.setTitle(experiment.isPublished() ? "Un-publish Experiment" : "Publish Experiment");
            if (endExperimentMenuItem != null && !experiment.isActive()) endExperimentMenuItem.setVisible(false);
            publishedTextView.setText(experiment.isPublished() ? "Published" : "Not Published");
            publishedTextView.setTextColor(experiment.isPublished() ? Color.GREEN : Color.RED);
        }
    }
}