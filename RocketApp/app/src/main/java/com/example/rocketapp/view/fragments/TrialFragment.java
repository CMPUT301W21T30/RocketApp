package com.example.rocketapp.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.helpers.Validate;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.experiments.CountExperiment;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.experiments.IntCountExperiment;
import com.example.rocketapp.model.trials.IntCountTrial;
import com.example.rocketapp.model.trials.MeasurementTrial;
import com.example.rocketapp.model.trials.Trial;

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
    private final Experiment experiment;
    private final ObjectCallback<Trial> callback;
    private String title;
    private EditText inputEditText;

    public TrialFragment(Experiment experiment, ObjectCallback<Trial> callback) {
        this.title = "";
        this.experiment = experiment;
        this.callback = callback;
    }

    public TrialFragment(String title, Experiment experiment, ObjectCallback<Trial> callback) {
        this.title = title;
        this.experiment = experiment;
        this.callback = callback;
    }


    /**
     * Setup and alert dialog and button callbacks
     **/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_trial, null);

        if (title.isEmpty()) title = experiment.getType();
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).setTitle(title).create();

        inputEditText = view.findViewById(R.id.inputEditText);
        inputEditText.setHint(experiment.getType().toLowerCase() + "...");
        view.findViewById(R.id.button_cancel).setOnClickListener(i -> alertDialog.dismiss());

        if(!experiment.info.isGeoLocationEnabled()){
            view.findViewById(R.id.warning).setVisibility(View.GONE);
        }

        if (experiment.getType().equals(BinomialTrial.TYPE)) {
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
                    switch(experiment.getType()) {
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
