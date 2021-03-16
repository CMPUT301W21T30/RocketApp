package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExperimentsListActivity extends AppCompatActivity implements ExperimentDialog.OnInputListener {

    //use this button to navigate to the profile page of the user
    public ImageButton profileBtn;
<<<<<<< Updated upstream
=======
    public Button addNewExperiment;
    public Button subscribeToExperiment;
    ArrayList<Experiment> experimentsOwned;
    ArrayList<Experiment> experimentsSubscribed;
    ExperimentRecyclerViewOwnedAdapter adapterOwned;
    ExperimentRecylerViewSubscribedAdapter adapterSubscribed;

>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_list);

        //TextView textView = findViewById(R.id.textView);
        //textView.setText(DataManager.getUser().getName());
        //TextView textView2 = findViewById(R.id.textView2);
        //textView2.setText(String.valueOf(DataManager.getIsOwner()));


        profileBtn = findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(v -> {

            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(userProfileIntent);
<<<<<<< Updated upstream
=======
        });

        subscribeToExperiment = findViewById(R.id.subscribe_button);
        //subscribeToExperiment.setOnClickListener();

        addNewExperiment = findViewById(R.id.createExpBtn);
        addNewExperiment.setOnClickListener(v -> new CreateExperimentDialog().show(getSupportFragmentManager(), "Add_experiment"));
>>>>>>> Stashed changes

        });
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