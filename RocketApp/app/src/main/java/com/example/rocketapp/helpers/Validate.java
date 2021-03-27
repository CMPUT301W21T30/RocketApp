package com.example.rocketapp.helpers;

import android.widget.EditText;


/**
 * Used to validate user input
 * Validates if integer entered is in a certain range
 * Validates if length is within two given length sizes
 */
public class Validate {

    /**
     * Private constructor, should not be instantiated.
     */
    private Validate() { }

    /**
     * Checks if input is an int in range and displays warning if it fails.
     * @param editText
     *          text box to check value for
     * @param min
     *          minimum value the integer can have
     * @param max
     *          maximum value the integer can have
     * @param showError
     *          if showError is true, the error is displayed.
     * @return
     *          true if string entered is int within min and max
     */
    public static boolean intInRange(EditText editText, int min, int max, boolean showError) {
        boolean isValid = intInRange(editText.getText().toString(), min, max);

        if (!isValid && showError) {
            editText.setError("Must be a non-negative number.");
            editText.requestFocus();
        }
        return isValid;
    }

    /**
     * @param str
     *      string to check
     * @param
     *      min minimum value the integer can have
     * @param
     *      max maximum value the integer can have
     * @return
     *      true if str entered is integer within min and max
     */
    public static boolean intInRange(String str, int min, int max) {
        boolean isValid = false;
        try {
            int value = Integer.parseInt(str);
            isValid = value >= min && value <= max;
        } catch (Exception ignored) {}
        return isValid;
    }

    /**
     * Checks if input is a float in range and displays warning if it fails.
     * @param editText
     *          text box to check value for
     * @param min
     *          minimum value the float can have
     * @param max
     *          maximum value the float can have
     * @param showError
     *          if showError is true, the error is displayed under the edittext.
     * @return
     *          true if string entered is float within min and max
     */
    public static boolean floatInRange(EditText editText, float min, float max, boolean showError) {
        boolean isValid = floatInRange(editText.getText().toString(), min, max);

        if (!isValid && showError) {
            editText.setError("Must be a non-negative float.");
            editText.requestFocus();
        }
        return isValid;
    }

    /**
     * @param str
     *      string to check
     * @param
     *      min minimum value the float can have
     * @param
     *      max maximum value the integer could have
     * @return
     *      true if str entered is float within min and max
     */
    public static boolean floatInRange(String str, float min, float max) {
        boolean isValid = false;

        try {
            float value = Float.parseFloat(str);
            isValid = value >= min && value <= max;
        } catch (Exception ignored) {}

        return isValid;
    }

    /**
     * Checks if input has a length in range and displays warning if it fails.
     * @param editText
     *      text box with some text
     * @param min
     *      minimum length the text can have
     * @param max
     *      maximum length the text can have
     * @param showError
     *      if showError is true, the error is displayed.
     * @return
     *      true if text entered has length within min and max.
     */
    public static boolean lengthInRange(EditText editText, int min, int max, boolean showError) {
        boolean isValid = lengthInRange(editText.getText().toString(), min, max);

        if (!isValid && showError) {
            editText.setError("Must be between " + min + " and " + max + " characters.");
            editText.requestFocus();
        }
        return isValid;
    }

    /**
     * @param str
     *      string to check
     * @param
     *      min minimum length the string can have
     * @param
     *      max maximum length the string can have
     * @return
     *      true if str entered is has length within min and max
     */
    public static boolean lengthInRange(String str, float min, float max) {
        boolean isValid = false;
        try {
            int length = str.length();
            isValid = length >= min && length <= max;
        }
        catch (Exception ignored) {}
        return isValid;
    }
}
