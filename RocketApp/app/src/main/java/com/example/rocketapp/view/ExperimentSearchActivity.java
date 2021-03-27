package com.example.rocketapp.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.experiments.Experiment;

import java.util.ArrayList;

/**
 * Display unsubscribed experiments on a new activity.
 */
public class ExperimentSearchActivity extends RocketAppActivity {
    private static final String TAG = "AllExperimentsActivity";
    private ArrayList<Experiment> experimentList;
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
        initRecyclerView();

        EditText searchExperiments = findViewById(R.id.search_for_experiments);
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        view.setOnTouchListener((v, motionEvent) -> {
            v.performClick();
            toggleKeyboard(false);
            return false;
        });
        view.setOnClickListener(v->{
            toggleKeyboard(false);
        });
    }

    /**
     * initialize/ update recycler view with the list of given experiments
     * or the updated list depending on the searched keyword
     */
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        experimentList = ExperimentManager.getExperimentArrayList("", false, true);

        adapter = new ExperimentListAdapter(experimentList, experiment -> {
            UserManager.subscribe(experiment, () -> {
                Log.d(TAG, "Subscribed");
                toggleKeyboard(false);
                finish();
            } ,exception -> {
                Log.d(TAG, exception.toString());
            });
        });

        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerViewNonOwner);
        experimentRecyclerView.setAdapter(adapter);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * filter the list using the keywords
     * typed into the searchbar
     * @param text
     */
    public void filter(String text){
        adapter.updateList(ExperimentManager.getExperimentArrayList(text, false, true));
    }
    
}