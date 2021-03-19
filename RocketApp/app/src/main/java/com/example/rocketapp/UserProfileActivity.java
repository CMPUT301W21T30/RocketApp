package com.example.rocketapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.security.auth.callback.Callback;

/**
 * User has the ability to update their email or phone number through this page
 */
public class UserProfileActivity extends AppCompatActivity {
    public ImageButton saveProfileData;

    /**
     * User enters the new information they want their profile to be updated with
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        TextView userName = findViewById(R.id.userNameOnProfile);
        userName.setText('@'+DataManager.getUser().getName());

        EditText userEmail = findViewById(R.id.userEmail);      //field to enter email
        EditText userPhoneNumber = findViewById(R.id.userPhoneNumber);      //field to enter phone number

        userEmail.setText(DataManager.getUser().getEmail());
        userPhoneNumber.setText(DataManager.getUser().getPhone_number());

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

                DataManager.getUser().setPhone_number(phone);
                DataManager.getUser().setEmail(email);
                DataManager.updateUser(user -> {
                    Toast.makeText(UserProfileActivity.this, "User Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }, e -> {});
            }
        });

    }

}
