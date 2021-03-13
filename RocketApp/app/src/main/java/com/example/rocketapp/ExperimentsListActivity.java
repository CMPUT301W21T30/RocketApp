package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ExperimentsListActivity extends AppCompatActivity implements ExperimentDialog.OnInputListener {

    //use this button to navigate to the profile page of the user
    private static final String TAG = "ExperimentsListActivity";
    public ImageButton profileBtn;

    // TODO: Getting no Subscribed Experiment even for users who subsribed to few experiment
    private ArrayList<Experiment> experiments = DataManager.getSubscribedExperimentArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_list);
        initRecyclerView();


        //TextView textView = findViewById(R.id.textView);
        //textView.setText(DataManager.getUser().getName());
        //TextView textView2 = findViewById(R.id.textView2);
        //textView2.setText(String.valueOf(DataManager.getIsOwner()));


        profileBtn = findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(userProfileIntent);

        });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        for (int i = 0; i < experiments.size(); i++) {
            Log.d("experiment", experiments.get(i).info.getDescription());
        }
        RecyclerView experimentRecyclerView = findViewById(R.id.experimentRecyclerView);
        ExperimentRecylerViewAdapter adapter = new ExperimentRecylerViewAdapter(this, experiments);
        experimentRecyclerView.setAdapter(adapter);
        experimentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void returnExperiment(Experiment exp) {
        Log.d("ExperimentsListActivity", "sendExperiment: got the experiment" + exp.info.getDescription());
        //TODO: handle received experiment
    }
            /*
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        String username = usernameEditText.getText().toString();
        loginOrCreateUser(username, isOwner.get());
    });
    */
}