package com.example.rocketapp.view.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.helpers.Validate;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.users.User;

/**
 * User has the ability to update their email or phone number through this page
 */
public class UserProfileActivity extends RocketAppActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        User user = UserManager.getUser(getIntent().getSerializableExtra(Experiment.ID_KEY));

        EditText userName = findViewById(R.id.userNameEditText);
        userName.setText(user.getName());

        EditText userEmail = findViewById(R.id.userEmailEditText);      //field to enter email
        userEmail.setText(user.getEmail());

        EditText userPhoneNumber = findViewById(R.id.userPhoneNumberEditText);      //field to enter phone number
        userPhoneNumber.setText(user.getPhoneNumber());

        Button saveProfileButton = findViewById(R.id.updateProfileButton);
        if (!UserManager.getUser().equals(user)) {
            userEmail.setEnabled(false);
            userPhoneNumber.setEnabled(false);
            userName.setEnabled(false);
            saveProfileButton.setVisibility(View.GONE);
        } else {

            saveProfileButton.setOnClickListener(v -> {

                // Check inputs and update profile
                if (!Validate.lengthInRange(userName, 3, 50, true)) return;
                if (!Validate.emailInRange(userEmail, 3, 50, true)) return;
                if (!Validate.lengthInRange(userPhoneNumber, 7, 11, true)) return;

                UserManager.getUser().setName(userName.getText().toString());
                UserManager.getUser().setPhoneNumber(userPhoneNumber.getText().toString());
                UserManager.getUser().setEmail(userEmail.getText().toString());

                UserManager.updateUser(u -> {
                    toggleKeyboard(false);
                    Toast.makeText(this, "User Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }, e -> {
                    userName.setError(e.getMessage());
                    userName.requestFocus();
                });
            });
        }

        // Create back button
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
}
