package com.example.rocketapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.experiments.Experiment;

import java.util.ArrayList;

/**
 * Home page for a user. Displays owned experiments, subscribed experiments, and provides interface to edit profile,
 * create new experiments, subscribe to experiments, and open experiments.
 */
public class ExperimentsListActivity extends AppCompatActivity{
    private static final String TAG = "ExperimentsListActivity";
    private ArrayList<Experiment> experimentsOwned, experimentsSubscribed;
    private ExperimentListAdapter adapterOwned, adapterSubscribed;

    /**
     * - Contains Navigation to the profile
     * - inits for the subscribed and owned experiment recycler views are called within
     * - callback to update list of experiments
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_list);

        initRecyclerViewOwned();

        initRecyclerViewSubscribed();

        ImageButton profileBtn = findViewById(R.id.experiment_options);
        profileBtn.setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            userProfileIntent.putExtra("id", UserManager.getUser().getId());
            startActivity(userProfileIntent);
        });

        Button showAllExperiment = findViewById(R.id.showAllExp);
        showAllExperiment.setOnClickListener(v -> {
            Intent allExpIntent = new Intent(getApplicationContext(), ExperimentSearchActivity.class);
            startActivity(allExpIntent);
        });

        Button addNewExperiment = findViewById(R.id.createExpBtn);
        addNewExperiment.setOnClickListener(v -> new CreateExperimentDialog().show(getSupportFragmentManager(), "Add_experiment"));

        ExperimentManager.setUpdateCallback(()-> {
            adapterOwned.updateList(ExperimentManager.getOwnedExperimentsArrayList());
            Log.d(TAG, "Updated Owned");
        });
        UserManager.setUpdateCallback(()-> {
            adapterSubscribed.updateList(ExperimentManager.getSubscribedExperimentArrayList());
            Log.e(TAG, "subscribed updated: " + ExperimentManager.getSubscribedExperimentArrayList().toString());
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Log.e(TAG,UserManager.getUser().toString());
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
     * Update the the Experiments List every time it back from other fragment
     */
    @Override
    protected void onResume() {
        super.onResume();
//        initRecyclerViewOwned();
//        initRecyclerViewSubscribed();
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
            intent.putExtra("id", experiment.getId());
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
            intent.putExtra("id", experiment.getId());
            startActivity(intent);
        });
        experimentRecyclerView.setAdapter(adapterSubscribed);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    // Adds swiping behaviour
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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



}