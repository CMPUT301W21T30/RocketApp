package com.example.rocketapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;

/**
 * Fragment for creating or editing experiments.
 * OnOkCallback interface executes when OK is pressed for calling code from the instantiating class.
 * Validates user input and notifies user if inputs are invalid.
 * Used with MainActivity and ExperimentActivity.
 *
 * Short static class to simplify creating the fragment without the inputs to edit success count and fail count.
 *
 * If an experiment is passed in, will modify that experiment and return a reference to it in OnOkCallback when OK is pressed.
 * If no experiment is passed in, will create a new experiment and return a reference to it in OnOkCallback when OK is pressed.
 */
public class TrialFragment extends DialogFragment {

    private String type;
    private EditText inputEditText;
    private DataManager.TrialCallback callback;

    public TrialFragment(String type, DataManager.TrialCallback callback) {
        this.type = type;
        this.callback = callback;
    }


    // Setup and alert dialog and button callbacks
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.trial_fragment_layout, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).setTitle(type).create();

        inputEditText = view.findViewById(R.id.inputEditText);
        view.findViewById(R.id.button_cancel).setOnClickListener(i -> alertDialog.dismiss());

        if (type.equals(BinomialTrial.TYPE)) {
            view.findViewById(R.id.inputEditText).setVisibility(View.GONE);
            view.findViewById(R.id.button_confirm).setVisibility(View.GONE);

            view.findViewById(R.id.addSuccess).setOnClickListener(view1 -> {
                callback.callBack(new BinomialTrial(true));
                alertDialog.dismiss();
            });
            view.findViewById(R.id.addFailure).setOnClickListener(view1 -> {
                callback.callBack(new BinomialTrial(false));
                alertDialog.dismiss();
            });
        }
        else {
            view.findViewById(R.id.addSuccess).setVisibility(View.GONE);
            view.findViewById(R.id.addFailure).setVisibility(View.GONE);
            view.findViewById(R.id.button_confirm).setOnClickListener(i -> {
                    switch(type) {
                        case IntCountExperiment.TYPE:
                            if (Validate.intInRange(inputEditText, 0, Integer.MAX_VALUE, true)) {
                                callback.callBack(new IntCountTrial(Integer.parseInt(inputEditText.getText().toString())));
                                alertDialog.dismiss();
                            }
                            break;
                        case CountExperiment.TYPE:
                            if (Validate.intInRange(inputEditText, 0, Integer.MAX_VALUE, true)) {
                                callback.callBack(new CountTrial(Integer.parseInt(inputEditText.getText().toString())));
                                alertDialog.dismiss();
                            }
                            break;
                        case MeasurementTrial.TYPE:
                            if (Validate.floatInRange(inputEditText, 0.0f, Float.MAX_VALUE, true)) {
                                callback.callBack(new MeasurementTrial(Float.parseFloat(inputEditText.getText().toString())));
                                alertDialog.dismiss();
                            }
                            break;
                    }
            });
        }

        return alertDialog;
    }
}
