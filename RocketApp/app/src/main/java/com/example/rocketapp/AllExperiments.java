package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class AllExperiments extends AppCompatActivity {
    private static final String TAG = "AllExperimentsActivity";
    private ArrayList<Experiment> experimentList;
    private EditText searchExperiments;
    private ExperimentListAdapter adapter;


    /**
     * initialize recyclerview
     * call a addTextChangedListener to searchExperiments editText
      * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_experiments);
        initRecyclerViewNotOwned();

        searchExperiments = findViewById(R.id.search_for_experiments);

        searchExperiments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    /**
     * initialize/ update recycler view with the list of given experiments
     * or the updated list depending on the searched keyword
     */
    private void initRecyclerViewNotOwned() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewNonOwner);

        experimentList = DataManager.getExperimentArrayList("", false, false);

        adapter = new ExperimentListAdapter(experimentList, experiment -> {
            DataManager.subscribe(experiment,() -> {
                Log.d(TAG, "Subscribed");
                finish();
            } ,exception -> {
                Log.d(TAG, "Could not be subscribed.");
            });
        });

        experimentRecyclerView.setAdapter(adapter);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * filter the list using the keywords
     * typed into the searchbar
     * @param text
     */
    public void filter(String text){
        experimentList.clear();
        experimentList.addAll(DataManager.getExperimentArrayList(text, false, false));
        adapter.notifyDataSetChanged();
    }



}