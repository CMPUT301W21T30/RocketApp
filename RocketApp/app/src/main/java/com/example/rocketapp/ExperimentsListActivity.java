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

//            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
//            startActivity(userProfileIntent);
//            DataManager.publishExperiment(new BinomialExperiment("Second Experiment description", "Canada", 10, true), null, null);
            DataManager.subscribe(DataManager.getExperimentArrayList().get(0), null);


//            System.out.println("Owned experiments.");
//            for (Experiment experiment : DataManager.getOwnedExperimentsArrayList()) {
//                System.out.println(experiment.toString());
//            }
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