package com.example.rocketapp.view.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.ScannerManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.view.fragments.CreateExperimentFragment;
import com.example.rocketapp.view.adapters.ExperimentListAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;

/**
 * Home page for a user. Displays owned experiments, subscribed experiments, and provides interface to edit profile,
 * create new experiments, subscribe to experiments, and open experiments.
 */
public class MainActivity extends RocketAppActivity {
    private static final String TAG = "ExperimentsListActivity";
    private ArrayList<Experiment<?>> experimentsOwned, experimentsSubscribed;
    private ExperimentListAdapter adapterOwned, adapterSubscribed;
    private int cameraPermissionRequestCode = 100;
    /**
     * - Contains Navigation to the profile
     * - inits for the subscribed and owned experiment recycler views are called within
     * - callback to update list of experiments
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerViewOwned();
        initRecyclerViewSubscribed();

        findViewById(R.id.experiment_options).setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            userProfileIntent.putExtra(Experiment.ID_KEY, UserManager.getUser().getId());
            startActivity(userProfileIntent);
        });

        findViewById(R.id.showAllExp).setOnClickListener(v -> {
            Intent allExpIntent = new Intent(getApplicationContext(), ExperimentSearchActivity.class);
            startActivity(allExpIntent);
        });

        findViewById(R.id.createExpBtn).setOnClickListener(v -> new CreateExperimentFragment().show(getSupportFragmentManager(), "Add_experiment"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scanCode:
                scanCode();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the the Experiments List every time it back from other fragment
     */
    @Override
    protected void onResume() {
        super.onResume();

        ExperimentManager.setUpdateCallback(()-> {
            adapterOwned.updateList(ExperimentManager.getOwnedExperimentsArrayList());
            adapterSubscribed.updateList(ExperimentManager.getSubscribedExperimentArrayList());
        });
        UserManager.setUpdateCallback(()-> adapterSubscribed.updateList(ExperimentManager.getSubscribedExperimentArrayList()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            ScannerManager.readCode(
                    result.getContents(),
                    trial -> Toast.makeText(this, "Trial added.", Toast.LENGTH_LONG).show(),
                    exception -> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * set adapter to the recyclerView and populate it with
     * the list of Owned experiments
     */
    private void initRecyclerViewOwned() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewOwner);

        experimentsOwned = ExperimentManager.getOwnedExperimentsArrayList();
        adapterOwned = new ExperimentListAdapter(experimentsOwned, experiment -> {
            Intent intent = new Intent(this, ExperimentActivity.class);
            intent.putExtra(Experiment.ID_KEY, experiment.getId());
            startActivity(intent);
        });
        experimentRecyclerView.setAdapter(adapterOwned);

        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Swipe Gesture for Published and UnPublished
        ItemTouchHelper itemTouchHelperOwned = new ItemTouchHelper(simpleCallback);
        itemTouchHelperOwned.attachToRecyclerView(experimentRecyclerView);
    }

    /**
     * set adapter to the recyclerView and populate it with
     * the list of Owned experiments
     */
    private void initRecyclerViewSubscribed(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewSubscribed);
        experimentsSubscribed = ExperimentManager.getSubscribedExperimentArrayList();
        adapterSubscribed = new ExperimentListAdapter(experimentsSubscribed, experiment -> {
            Intent intent = new Intent(this, ExperimentActivity.class);
            intent.putExtra(Experiment.ID_KEY, experiment.getId());
            startActivity(intent);
        });
        experimentRecyclerView.setAdapter(adapterSubscribed);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    // Adds swiping behaviour
    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    ExperimentManager.publishExperiment(experimentsOwned.get(position),
                            experiment -> Toast.makeText(getApplicationContext(), "Published " + experimentsOwned.get(position).info.getDescription(), Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
                    adapterSubscribed.notifyDataSetChanged();
                    adapterOwned.notifyDataSetChanged();
                    break;
                case ItemTouchHelper.RIGHT:
                    ExperimentManager.unpublishExperiment(experimentsOwned.get(position),
                            experiment -> Toast.makeText(getApplicationContext(), "UnPublished " + experiment.info.getDescription(), Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
                    adapterSubscribed.notifyDataSetChanged();
                    adapterOwned.notifyDataSetChanged();
                    break;
            }

        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == cameraPermissionRequestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "Must allow app access to Camera in device settings.", Toast.LENGTH_LONG).show();
            } else {
                scanCode();
            }
        }
    }

    private void scanCode() {
        if (!hasPermission(Manifest.permission.CAMERA, cameraPermissionRequestCode)) return;

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning");
        integrator.initiateScan();
    }

}