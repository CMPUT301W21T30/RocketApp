package com.example.rocketapp.view.activities;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RocketAppActivity extends AppCompatActivity {
    private InputMethodManager input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        input = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
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
}
