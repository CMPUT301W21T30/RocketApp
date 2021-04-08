package com.example.rocketapp.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.helpers.Validate;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.CountExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.experiments.IntCountExperiment;
import com.example.rocketapp.model.experiments.MeasurementExperiment;

import java.util.Objects;

/**
 * A dialog box to create a new Experiment
 * User selects type of experiment and enters details of experiment
 */

public class CreateExperimentFragment extends DialogFragment {
    private static final String TAG = "ExperimentDialog";
    private EditText descriptionEditText, regionEditText, minTrialsEditText;      //Add experiment info
    private Button publishButton, cancelButton;                                   //Confirm or Cancel information
    private CheckBox geoCheckBox;                                                 //Handle boolean geoLocationEnabled
    private Spinner experimentTypeSpinner;                                        //Dropdown box to select experiment type


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_experiment, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).setTitle("Create Experiment").create();

        initialSetup(view);

        publishButton.setOnClickListener(v -> {
            if (checkInputsValid()) {
                publishExperiment(createExperiment(experimentTypeSpinner.getSelectedItem().toString()));
                dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return alertDialog;
    }

    /**
     * Connects with UI of Experiment Dialog box
     * @param view
     */
    public void initialSetup(View view){

        //Initializing dialog attributes
        //Sources:
        //How to add a checkbox: https://developer.android.com/guide/topics/ui/controls/checkbox
        //how to add a dropdown list: https://developer.android.com/guide/topics/ui/controls/spinner

        descriptionEditText = view.findViewById(R.id.description_input);                            //description
        regionEditText = view.findViewById(R.id.region_input);                                      //region
        minTrialsEditText = view.findViewById(R.id.min_trial);                                      //minimum number of trials
        geoCheckBox = view.findViewById(R.id.geolocation);                                          //geoLocation enabled or disabled
        publishButton = view.findViewById(R.id.add_exp);                                            //Confirm experiment
        cancelButton = view.findViewById(R.id.cancel_exp);                                          //Do not create experiment
        experimentTypeSpinner = view.findViewById(R.id.select_exp);                                 //Type of experiment ("Binomial", "Count", "IntCount", "Measurement")
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.experiments));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);           //dropdown for experiment types
        experimentTypeSpinner.setAdapter(myAdapter);                                                //set experiment type from selected item inside dropdown
    }

    /**
     * Publishes a new experiment
     * @param newExperiment experiment to be published.
     */
    public void publishExperiment(Experiment<?> newExperiment){
        ExperimentManager.createExperiment(newExperiment, experiment -> Log.d(TAG, "Experiment published"), exception -> Log.d(TAG, exception.getMessage()));
    }

    /**
     * given a string describing the experiment type, returns an experiment of said type based on user inputs
     * @param type
     *          experiment type
     * @return
     *          experiment of said type based on user input
     */
    public Experiment<?> createExperiment(String type){
        switch(type) {
            case ("Count Experiment"):
                return new CountExperiment(descriptionEditText.getText().toString(),
                        regionEditText.getText().toString(), Integer.parseInt(minTrialsEditText.getText().toString()), geoCheckBox.isChecked());
            case ("Integer Count Experiment"):
                return new IntCountExperiment(descriptionEditText.getText().toString(),
                        regionEditText.getText().toString(), Integer.parseInt(minTrialsEditText.getText().toString()), geoCheckBox.isChecked());
            case ("Measurement Experiment"):
                return new MeasurementExperiment(descriptionEditText.getText().toString(),
                        regionEditText.getText().toString(), Integer.parseInt(minTrialsEditText.getText().toString()), geoCheckBox.isChecked());
            default:
                return new BinomialExperiment(descriptionEditText.getText().toString(),
                        regionEditText.getText().toString(), Integer.parseInt(minTrialsEditText.getText().toString()), geoCheckBox.isChecked());
        }
    }

    /**
     * Validates user input
     * description must be within [5,100] characters
     * minimum amount of trials before an experiment can be ENDED must be described
     * region length must be within [1,40] characters
     * @return
     *      True if user input is valid
     *      False if user input is invalid
     */
    private boolean checkInputsValid() {
        return  Validate.lengthInRange(descriptionEditText, 5, 100, true) &&
                Validate.intInRange(minTrialsEditText, 0, Integer.MAX_VALUE, true) &&
                Validate.lengthInRange(regionEditText, 1, 40, true);
    }
}
