package com.example.rocketapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.Code;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.model.trials.CountTrial;
import com.example.rocketapp.model.trials.IntCountTrial;
import com.example.rocketapp.model.trials.MeasurementTrial;
import com.example.rocketapp.model.trials.Trial;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * ScannerActivity implements the scanner feature
 *
 */
public class RegisterBarcodeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ExperimentScannerAct";
    private Button scanBtn;
    private TextView code;
    private Experiment experiment;
    private TextView experimentType;
    private EditText trialsEditText;
    private CheckBox registerpass;
    private CheckBox registerfail;
    private Button registerBtn;
    private Code registerCode;
    private BinomialTrial binomialTrial;
    private CountTrial countTrial;
    private IntCountTrial intCountTrial;
    private MeasurementTrial measurementTrial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_scanner);
        experimentType = findViewById(R.id.experimentType);
        trialsEditText = findViewById(R.id.trialEditText);
        registerfail = findViewById(R.id.registerfail);
        registerpass = findViewById(R.id.registerpass);
        code = findViewById(R.id.scanned_code);
        registerBtn = findViewById(R.id.registerButton);
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));

        registerCode = new Code();


        experimentType.setText(experiment.getType());

        scanBtn = findViewById(R.id.scanButton);
        scanBtn.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //System.out.println(code.getText());

        if (experiment.getType().equals(BinomialExperiment.TYPE)) {
            trialsEditText.setVisibility(View.GONE);
        } else {
            registerpass.setVisibility(View.GONE);
            registerfail.setVisibility(View.GONE);
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (experiment.getType().equals(BinomialTrial.TYPE)) {
                    if (registerpass.isChecked()) {
                        registerCode.setCode(code.getText().toString());
                        registerCode.setExperiment(experiment.info.getDescription());
                        registerCode.setIfBinomial(true);

                    }
                    else if (registerfail.isChecked()) {
                        registerCode.setCode(code.getText().toString());
                        registerCode.setExperiment(experiment.info.getDescription());
                        registerCode.setIfBinomial(false);

                    }
                    else {
                        //CANNOT select both
                    }

                }
                else {

                }


            }
        });

    }


    @Override
    public void onClick(View v) {
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning");
        integrator.initiateScan();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                code = findViewById(R.id.scanned_code);
                code.setText(result.getContents());
            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


}