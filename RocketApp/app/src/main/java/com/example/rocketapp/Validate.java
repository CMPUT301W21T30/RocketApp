package com.example.rocketapp;
import android.widget.EditText;

/**
 * Used to validate user input
 * Validates if integer entered is in a certain range
 * Validates if length is within two given length sizes
 */
public class Validate {
    /**
     * Returns true if integer entered is within the two parametric integers passed
     * @param editText
     *          text box with integer value
     * @param min
     *          minimum value the integer should have
     * @param max
     *          maximum value the integer could have
     * @param showError
     *          if showError is true, the error is displayed.
     * @return
     */
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

    /**
     * Returns true if integer entered is within the two parametric integers passed
     * @param editText
     *          text box with integer value
     * @param min
     *          minimum value the integer should have
     * @param max
     *          maximum value the integer could have
     * @param showError
     *          if showError is true, the error is displayed.
     * @return
     */
    static boolean floatInRange(EditText editText, float min, float max, boolean showError) {
        boolean isValid = false;
        try {
            float value = Float.parseFloat(editText.getText().toString());
            isValid = value >= min && value <= max;
        } catch (Exception e) {
        }

        if (!isValid && showError) {
            editText.setError("Must be a non-negative float.");
            editText.requestFocus();
        }
        return isValid;
    }

    /**
     * Returns true if text entered has length within the two parametric integers passed
     * @param editText
     *          text box with some text
     * @param min
     *          minimum length the text should have
     * @param max
     *          maximum length the text could have
     * @param showError
     *          if showError is true, the error is displayed.
     * @return
     */
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
