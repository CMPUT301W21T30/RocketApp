package com.example.rocketapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class ExperimentsListActivity extends AppCompatActivity{

    //use this button to navigate to the profile page of the user
    private static final String TAG = "ExperimentsListActivity";
    public ImageButton profileBtn;

    public Button addNewExperiment;
    public Button subscribeToExperiment;

    public Button addNewExperiment;
    ArrayList<Experiment> experimentsOwned;
    ArrayList<Experiment> experimentsSubscribed;
    ExperimentRecyclerViewOwnedAdapter adapterOwned;
    ExperimentRecylerViewSubscribedAdapter adapterSubscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_list);

        initRecyclerViewOwned();

        initRecyclerViewSubscribed();


        profileBtn = findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(userProfileIntent);

        });

        subscribeToExperiment = findViewById(R.id.subscribe_button);
        //subscribeToExperiment.setOnClickListener();

        addNewExperiment = findViewById(R.id.createExpBtn);
        addNewExperiment.setOnClickListener(v -> new CreateExperimentDialog().show(getSupportFragmentManager(), "Add_experiment"));
        });

        addNewExperiment = findViewById(R.id.createExpBtn);
        addNewExperiment.setOnClickListener(v -> new CreateExperimentDialog().show(getSupportFragmentManager(), "Add_experiment"));

        DataManager.setUpdateCallback(()->{
            experimentsOwned.clear();
            experimentsOwned.addAll(DataManager.getOwnedExperimentsArrayList());
            experimentsSubscribed.clear();
            experimentsSubscribed.addAll(DataManager.getSubscribedExperimentArrayList());
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

    private void initRecyclerViewOwned() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewOwner);

        experimentsOwned = DataManager.getOwnedExperimentsArrayList();
        adapterOwned = new ExperimentRecyclerViewOwnedAdapter(this, experimentsOwned);
        experimentRecyclerView.setAdapter(adapterOwned);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Swipe Gesture for Published and UnPublished
        ItemTouchHelper itemTouchHelperOwned = new ItemTouchHelper(simpleCallback);
        itemTouchHelperOwned.attachToRecyclerView(experimentRecyclerView);
    }


    private void initRecyclerViewSubscribed(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewSubscribed);

        experimentsSubscribed = DataManager.getSubscribedExperimentArrayList();
        adapterSubscribed = new ExperimentRecylerViewSubscribedAdapter(this, experimentsSubscribed);
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

                    adapterOwned.notifyDataSetChanged();
                    break;
                case ItemTouchHelper.RIGHT:
                    DataManager.unpublishExperiment(experimentsOwned.get(position), (experiment) -> {
                        Toast.makeText(getApplicationContext(), "UnPublished " + experiment.info.getDescription(), Toast.LENGTH_SHORT).show();
                    }, (e) -> {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    });

                    adapterOwned.notifyDataSetChanged();
                    break;
            }

        }
    };

}