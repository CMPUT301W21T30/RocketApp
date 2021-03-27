package com.example.rocketapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.users.User;

/**
 * User has the ability to update their email or phone number through this page
 */
public class UserProfileActivity extends RocketAppActivity {
    public ImageButton saveProfileData;
    public User user;
    /**
     * User enters the new information they want their profile to be updated with
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        user = UserManager.getUser(getIntent().getSerializableExtra("id"));

        TextView userName = findViewById(R.id.userNameOnProfile);
        userName.setText('@'+ user.getName());

        EditText userEmail = findViewById(R.id.userEmail);      //field to enter email
        EditText userPhoneNumber = findViewById(R.id.userPhoneNumber);      //field to enter phone number

        userEmail.setText(user.getEmail());
        userPhoneNumber.setText(user.getPhoneNumber());

        if (!UserManager.getUser().equals(user)) {
            userEmail.setEnabled(false);
            userPhoneNumber.setEnabled(false);
        }

        saveProfileData = findViewById(R.id.saveUserProfileData);
        saveProfileData.setOnClickListener(new View.OnClickListener() {

            /**
             * Upon clicking the update button
             * The user email and phone number gets updated
             * The firestore database also gets updated
             * @param v
             */
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String phone = userPhoneNumber.getText().toString();

                UserManager.getUser().setPhoneNumber(phone);
                UserManager.getUser().setEmail(email);
                UserManager.updateUser(user -> {
                    Toast.makeText(UserProfileActivity.this, "User Profile Updated", Toast.LENGTH_SHORT).show();
                    toggleKeyboard(false);
                    finish();
                }, e -> {});
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
}
