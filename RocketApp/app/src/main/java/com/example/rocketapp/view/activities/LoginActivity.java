package com.example.rocketapp.view.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.controller.callbacks.Callback;
import com.example.rocketapp.helpers.Validate;

/**
 * This is the landing screen of RocketApp
 * User logs in with a username
 */
public class LoginActivity extends AppCompatActivity {
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
            usernameEditText = findViewById(R.id.userNameEditText);
            if (Validate.lengthInRange(usernameEditText, 3, 50, true)) {
                createUser(usernameEditText.getText().toString());
            }
        });

        findViewById(R.id.createUserCardView).setVisibility(View.INVISIBLE);

        login(()-> {
            findViewById(R.id.createUserCardView).setVisibility(View.VISIBLE);
            findViewById(R.id.inputGroup).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingMessageTextView).setVisibility(View.INVISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        });
    }


    /**
     * Logs into existing user's profile if username matches a user inside database
     */
    private void login(Callback onFailure) {
        UserManager.login(this, user -> {
            Intent ExperimentsListActivityIntent = new Intent(this, MainActivity.class);
            startActivity(ExperimentsListActivityIntent);
            finish();
        }, e -> onFailure.callBack());
    }


    /**
     * Creates a new user and login
     * @param userName Name of user to create
     */
    private void createUser(String userName) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        UserManager.createUser(userName, this, user -> {
            Intent ExperimentsListActivityIntent = new Intent(this, MainActivity.class);
            startActivity(ExperimentsListActivityIntent);
            finish();
        }, e -> {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            usernameEditText.setError("Username not available.");
            usernameEditText.requestFocus();
        });
    }

}

