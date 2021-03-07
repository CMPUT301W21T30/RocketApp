package com.example.rocketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ExperimentsListActivity extends AppCompatActivity {
    public Button profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_list);

        TextView textView = findViewById(R.id.textView);
        textView.setText(DataManager.getUser().getName());
        TextView textView2 = findViewById(R.id.textView2);
        textView2.setText(String.valueOf(DataManager.getIsOwner()));


        profileBtn = findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(v -> {

            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(userProfileIntent);

        });
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