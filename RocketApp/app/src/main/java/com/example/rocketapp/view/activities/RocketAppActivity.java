package com.example.rocketapp.view.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.example.rocketapp.controller.callbacks.Callback;

public class RocketAppActivity extends AppCompatActivity {
    private InputMethodManager input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    protected void toggleKeyboard(boolean open){
        if (open){
            input.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else {
            View view = getCurrentFocus();
            if (view != null) {
                getCurrentFocus().clearFocus();
                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }


    public boolean hasPermission(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {permission}, requestCode);
            return false;
        }
        return true;
    }
}
