package com.example.rocketapp;

import android.content.Context;
import android.os.Build;
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
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

/**
 * A dialog box to create a new Experiment
 * User selects type of experiment and enters details of experiment
 */

public class CreateExperimentDialog extends DialogFragment {

    //Forces implementing class to create a function that handles experiment returned from fragment

    private static final String TAG = "ExperimentDialog";

    private EditText descriptionET, regionET, minTrialsET;      //Add experiment info
    private Button publishBtn, cancelButton;                      //Confirm or Cancel information
    private CheckBox geoBox;                                    //Handle boolean geoLocationEnabled
    private Boolean geolocationEnabled = false;                         //Set through geoBox
    private Spinner expType;                                    //Dropdown box to select experiment type

    /**
     *
     * @param inflater
     *          Instantiates a layout XML file into its corresponding View objects.     //https://developer.android.com/reference/android/view/LayoutInflater
     * @param container
     *          a view used to contain other views
     * @param savedInstanceState
     *          save the state of the application
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.create_experiment_dialog, container, false);
        initialSetup(view);

        geoBox.setOnClickListener(v -> {
            if (geoBox.isChecked()){            //if experiment requires geoLocation to be enabled
                geolocationEnabled = true;
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Geolocation Enabled", Toast.LENGTH_LONG).show();
            }
        });

        publishBtn.setOnClickListener(v -> {          //Confirm button is clicked
            if (checkInputsValid()) {
                Log.d(TAG, "onClick: capturing input");
                returnExperiment(getExperiment(expType.getSelectedItem().toString()));
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });         //Validates input and adds experiment

        cancelButton.setOnClickListener(v -> {      //Cancel button is clicked
            Log.d(TAG, "onClick: Closing Dialog");
            Objects.requireNonNull(getDialog()).dismiss();
        });         //Does not add the experiment

        return view;
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

        descriptionET = view.findViewById(R.id.description_input);      //description
        regionET = view.findViewById(R.id.region_input);                //region
        minTrialsET = view.findViewById(R.id.min_trial);                //minimum number of trials
        geoBox = view.findViewById(R.id.geolocation);                   //geoLocation enabled or disabled
        publishBtn = view.findViewById(R.id.add_exp);                     //Confirm experiment
        cancelButton = view.findViewById(R.id.cancel_exp);              //Do not create experiment
        expType = view.findViewById(R.id.select_exp);                   //Type of experiment ("Binomial", "Count", "IntCount", "Measurement")
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.experiments));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);       //dropdown for experiment types
        expType.setAdapter(myAdapter);                                  //set experiment type from selected item inside dropdown
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            /*dialogListener = (OnInputListener) getActivity();*/
        }catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    /**
     * Exception handler which checks if Experiment is valid and published or not
     * @param newExperiment Experiment to be published.
     */
    public void returnExperiment(Experiment newExperiment){ ;
        DataManager.publishExperiment(newExperiment, experiment -> {
            Log.d(TAG, "Experiment published");
        }, exception -> {
            Log.d(TAG, "Experiment not published");
        });
    }

    /**
     * given a string describing the experiment type, returns an experiment of said type based on user inputs
     * @param type
     *          experiment type
     * @return
     *          experiment of said type based on user input
     */
    public Experiment getExperiment(String type){
        Experiment exp;
        switch(type) {
            case ("Count Experiment"):
                exp = new CountExperiment(descriptionET.getText().toString(),
                        regionET.getText().toString(), Integer.parseInt(minTrialsET.getText().toString()), geolocationEnabled);
                return exp;
            case ("Integer Count Experiment"):
                exp = new IntCountExperiment(descriptionET.getText().toString(),
                        regionET.getText().toString(), Integer.parseInt(minTrialsET.getText().toString()), geolocationEnabled);
                return exp;
            case ("Measurement Experiment"):
                exp = new MeasurementExperiment(descriptionET.getText().toString(),
                        regionET.getText().toString(), Integer.parseInt(minTrialsET.getText().toString()), geolocationEnabled);
                return exp;
            default:
                exp = new BinomialExperiment(descriptionET.getText().toString(),
                        regionET.getText().toString(), Integer.parseInt(minTrialsET.getText().toString()), geolocationEnabled);
                return exp;

        }
    }

    /**
     * Validates user input
     * description must be within [1,40] characters
     * minimum amount of trials before an experiment can be ENDED must be described
     * region length must be within [1,40] characters
     * @return
     *      True if user input is valid
     *      False if user input is invalid
     */
    private boolean checkInputsValid() {
        return  Validate.lengthInRange(descriptionET, 1, 100, true) &&
                Validate.intInRange(minTrialsET, 0, Integer.MAX_VALUE, true) &&
                Validate.lengthInRange(regionET, 1, 40, true)
                ;
    }
}
