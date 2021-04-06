package com.example.rocketapp.view.activities;

import android.content.Context;
import android.content.DialogInterface;
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
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.model.Code;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.example.rocketapp.view.TrialFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * ScannerActivity implements the scanner feature
 *
 * https://youtu.be/wfucGSKngq4
 *
 */
public class RegisterBarcodeActivity extends AppCompatActivity {
    private static final String TAG = "ExperimentScannerAct";
    private Button scanBtn;
    private TextView code;
    private Experiment<?> experiment;
    private TextView experimentType;
    private EditText trialsEditText;
    private CheckBox registerpass;
    private CheckBox registerfail;
    private Button registerBtn;
    private Code registerCode;
    private TextView checkRegister;
    private String scannedCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_scanner);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));

        experimentType = findViewById(R.id.experimentType);
        trialsEditText = findViewById(R.id.trialEditText);
        registerfail = findViewById(R.id.registerfail);
        registerpass = findViewById(R.id.registerpass);
        code = findViewById(R.id.scanned_code);
        registerBtn = findViewById(R.id.registerButton);
        checkRegister = findViewById(R.id.checkRegister);

        registerCode = new Code();


        experimentType.setText(experiment.getType());

        scanBtn = findViewById(R.id.scanButton);
        scanBtn.setOnClickListener(this::scanCode);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (experiment.getType().equals(BinomialExperiment.TYPE)) {
            trialsEditText.setVisibility(View.GONE);
        } else {
            registerpass.setVisibility(View.GONE);
            registerfail.setVisibility(View.GONE);
        }

        registerBtn.setOnClickListener(v ->
            new TrialFragment(experiment,
                    newTrial -> TrialManager.registerBarcode(scannedCode, experiment, newTrial,
                            barcode-> Toast.makeText(this, "Barcode registered.", Toast.LENGTH_LONG).show(),
                            exception-> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show())
            ).show(getSupportFragmentManager(), "ADD_TRIAL"));
    }


    private void scanCode(View v) {
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
                scannedCode = result.getContents();
            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void giveError(){
        Toast.makeText(this, "Cannot do this", Toast.LENGTH_LONG).show();
    }

    public void giveConfirmation(){
        Toast.makeText(this, "registered!", Toast.LENGTH_LONG).show();
    }

}