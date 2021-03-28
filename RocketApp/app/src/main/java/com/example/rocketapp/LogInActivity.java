package com.example.rocketapp;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;

/**
 * This is the landing screen of RocketApp
 * User logs in with a username
 */
public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "LogInActivity";
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
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            EditText usernameEditText = findViewById(R.id.usernameEditText);
            if (Validate.lengthInRange(usernameEditText, 3, 50, true)) {
                loginOrCreateUser(usernameEditText.getText().toString());
            }
        });
    }


    /**
     * Creates user if name entered does not exist
     * Logs into existing user's profile if username matches a user inside database
     * @param userName
     *          username entered during login in the box.
     */
    private void loginOrCreateUser(String userName) {
        DataManager.login(userName, user -> {           //Username exists and match successful
            Intent ExperimentsListActivityIntent = new Intent(LogInActivity.this, ExperimentsListActivity.class);
            startActivity(ExperimentsListActivityIntent);
        }, e -> {
            Log.d(TAG, e.toString());
            createUser(userName);                       //Create user if user name does not exist
        });
    }

    /**
     * Creates a new user and login
     * @param userName Name of user to create
     */
    private void createUser(String userName) {
        DataManager.createUser(userName, user -> {
            Intent ExperimentsListActivityIntent = new Intent(LogInActivity.this, ExperimentsListActivity.class);
            startActivity(ExperimentsListActivityIntent);
        }, e -> {
            Log.e(TAG, e.toString());        //User is unable to be created with this username
        });
    }

}

