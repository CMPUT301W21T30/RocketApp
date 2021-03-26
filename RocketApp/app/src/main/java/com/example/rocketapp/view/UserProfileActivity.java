package com.example.rocketapp.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rocketapp.R;
import com.example.rocketapp.controller.UserManager;

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
        userName.setText('@'+ UserManager.getUser().getName());

        EditText userEmail = findViewById(R.id.userEmail);      //field to enter email
        EditText userPhoneNumber = findViewById(R.id.userPhoneNumber);      //field to enter phone number

        userEmail.setText(UserManager.getUser().getEmail());
        userPhoneNumber.setText(UserManager.getUser().getPhoneNumber());

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
                    finish();
                }, e -> {});
            }
        });

    }

}
