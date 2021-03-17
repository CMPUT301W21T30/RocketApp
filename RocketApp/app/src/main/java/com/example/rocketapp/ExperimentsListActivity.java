package com.example.rocketapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Home page for a user. Displays owned experiments, subscribed experiments, and provides interface to edit profile,
 * create new experiments, subscribe to experiments, and open experiments.
 */
public class ExperimentsListActivity extends AppCompatActivity{
    private static final String TAG = "ExperimentsListActivity";
    private ArrayList<Experiment> experimentsOwned;
    private ArrayList<Experiment> experimentsSubscribed;
    private ExperimentListAdapter adapterOwned;
    private ExperimentListAdapter adapterSubscribed;

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
            startActivity(userProfileIntent);
        });
        Button showAllExperiment = findViewById(R.id.showAllExp);
        showAllExperiment.setOnClickListener(v -> {
            Intent allExpIntent = new Intent(getApplicationContext(), AllExperiments.class);
            startActivity(allExpIntent);
        });

        Button addNewExperiment = findViewById(R.id.createExpBtn);
        addNewExperiment.setOnClickListener(v -> new CreateExperimentDialog().show(getSupportFragmentManager(), "Add_experiment"));

        DataManager.setUpdateCallback(()->{
            adapterOwned.updateList(DataManager.getOwnedExperimentsArrayList());
            adapterSubscribed.updateList(DataManager.getSubscribedExperimentArrayList());
        });
    }

    /**
     * Update the the Experiments List every time it back from other fragment
     */
    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerViewOwned();
        initRecyclerViewSubscribed();
    }

    /**
     * set adapter to the recyclerView and populate it with
     * the list of Owned experiments
     */
    private void initRecyclerViewOwned() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewOwner);

        experimentsOwned = DataManager.getOwnedExperimentsArrayList();
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
        experimentsSubscribed = DataManager.getSubscribedExperimentArrayList();
        adapterSubscribed = new ExperimentListAdapter(experimentsSubscribed, experiment -> {
            Intent intent = new Intent(this, ExperimentActivity.class);
            intent.putExtra("id", experiment.getId());
            startActivity(intent);
        });
        experimentRecyclerView.setAdapter(adapterSubscribed);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



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
                    DataManager.publishExperiment(experimentsOwned.get(position), (experiment) -> {
                        Toast.makeText(getApplicationContext(), "Published " + experimentsOwned.get(position).info.getDescription(), Toast.LENGTH_SHORT).show();
                    }, (e) -> {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    });
                    adapterSubscribed.notifyDataSetChanged();
                    adapterOwned.notifyDataSetChanged();
                    break;
                case ItemTouchHelper.RIGHT:
                    DataManager.unpublishExperiment(experimentsOwned.get(position), (experiment) -> {
                        Toast.makeText(getApplicationContext(), "UnPublished " + experiment.info.getDescription(), Toast.LENGTH_SHORT).show();
                    }, (e) -> {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    });
                    adapterSubscribed.notifyDataSetChanged();
                    adapterOwned.notifyDataSetChanged();
                    break;
            }

        }
    };

}