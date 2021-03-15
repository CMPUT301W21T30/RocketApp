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

public class CreateExperimentDialog extends DialogFragment {

    private static final String TAG = "ExperimentDialog";

    private EditText descriptionET, regionET, minTrialsET;
    private Button publishButton, cancelButton;
    private CheckBox geoBox;
    private Boolean geolocationEnabled = false;
    private Spinner expType;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.create_experiment_dialog, container, false);
        initialSetup(view);

        geoBox.setOnClickListener(v -> {
            if (geoBox.isChecked()){
                geolocationEnabled = true;
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Geolocation Enabled", Toast.LENGTH_LONG).show();
            }
        });

        publishButton.setOnClickListener(v -> {
            if (checkInputsValid()) {
                Log.d(TAG, "onClick: capturing input");
                returnExperiment(getExperiment(expType.getSelectedItem().toString()));
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Closing Dialog");
            Objects.requireNonNull(getDialog()).dismiss();
        });

        return view;
    }

    public void initialSetup(View view){

        //Initializing dialog attributes
        //Sources:
        //How to add a checkbox: https://developer.android.com/guide/topics/ui/controls/checkbox
        //how to add a dropdown list: https://developer.android.com/guide/topics/ui/controls/spinner

        descriptionET = view.findViewById(R.id.description_input);
        regionET = view.findViewById(R.id.region_input);
        minTrialsET = view.findViewById(R.id.min_trial);
        geoBox = view.findViewById(R.id.geolocation);
        publishButton = view.findViewById(R.id.add_exp);
        cancelButton = view.findViewById(R.id.cancel_exp);
        expType = view.findViewById(R.id.select_exp);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.experiments));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expType.setAdapter(myAdapter);
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

    public Experiment getExperiment(String type){

        //given a string describing the experiment type, returns an experiment of said type based on user inputs
        Experiment exp;
        switch(type) {
            case ("Count Experiment"):
                exp = new CountExperiment(descriptionET.getText().toString(),
                        regionET.getText().toString(), Integer.parseInt(minTrialsET.getText().toString()),geolocationEnabled);
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

    private boolean checkInputsValid() {
        return  Validate.lengthInRange(descriptionET, 1, 40, true) &&
                Validate.intInRange(minTrialsET, 0, Integer.MAX_VALUE, true) &&
                Validate.lengthInRange(regionET, 1, 40, true)
                ;
    }

    private void returnExperiment(Experiment newExperiment){
        DataManager.publishExperiment(newExperiment, experiment -> {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Experiment published", Toast.LENGTH_LONG).show();
        }, exception -> {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Experiment could not be added", Toast.LENGTH_LONG).show();
        });
    }
}
