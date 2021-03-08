package com.example.rocketapp;

import android.widget.EditText;

public class Validate {
    static boolean intInRange(EditText editText, int min, int max, boolean showError) {
        boolean isValid = false;
        try {
            int value = Integer.parseInt(editText.getText().toString());
            isValid = value >= min && value <= max;
        } catch (Exception e) {
        }

        if (!isValid && showError) {
            editText.setError("Must be a non-negative number.");
            editText.requestFocus();
        }
        return isValid;
    }

    static boolean lengthInRange(EditText editText, int min, int max, boolean showError) {
        boolean isValid = false;
        try {
            int length = editText.getText().toString().length();
            isValid = length >= min && length <= max;
        } catch (Exception e) {
        }

        if (!isValid && showError) {
            editText.setError("Must be between " + min + " and " + max + " characters.");
            editText.requestFocus();
        }
        return isValid;
    }
}
