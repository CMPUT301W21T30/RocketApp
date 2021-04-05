package com.example.rocketapp.view.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.helpers.Validate;
import com.example.rocketapp.view.activities.ExperimentsListActivity;

/**
 * This is the landing screen of RocketApp
 * User logs in with a username
 */
public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "LogInActivity";
    EditText usernameEditText;
    /**
     * Displays a username entry field and a login button
     * Creates or Logs in to an existing user profile accordingly
     * @param savedInstanceState
     *          Save state of experiment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set up login Button
        findViewById(R.id.loginBtn).setOnClickListener(v -> {
            usernameEditText = findViewById(R.id.usernameEditText);
            if (Validate.lengthInRange(usernameEditText, 3, 50, true)) {
                createUser(usernameEditText.getText().toString());
            }
        });

        findViewById(R.id.inputGroup).setVisibility(View.GONE);

        if (!getPreferences(MODE_PRIVATE).getString("userId", "noId").equals("noId")) {
            Log.e(TAG, "userId found!");
            login(getPreferences(MODE_PRIVATE));
        } else {
            findViewById(R.id.inputGroup).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingGroup).setVisibility(View.GONE);
            Log.e(TAG, "userId not found");
        }
    }


    /**
     * Logs into existing user's profile if username matches a user inside database
     * @param preferences
     *          username entered during login in the box.
     */
    private void login(SharedPreferences preferences) {
        UserManager.login(preferences, user -> {
            Intent ExperimentsListActivityIntent = new Intent(this, ExperimentsListActivity.class);
            startActivity(ExperimentsListActivityIntent);
        }, e -> Log.d(TAG, e.toString()));
    }

    /**
     * Creates a new user and login
     * @param userName Name of user to create
     */
    private void createUser(String userName) {
        UserManager.createUser(userName, getPreferences(MODE_PRIVATE), user -> {
            Intent ExperimentsListActivityIntent = new Intent(this, ExperimentsListActivity.class);
            startActivity(ExperimentsListActivityIntent);
        }, e -> {
            usernameEditText.setError("Username not available.");
            usernameEditText.requestFocus();
        });
    }

}

