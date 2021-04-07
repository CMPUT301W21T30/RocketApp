package com.example.rocketapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.model.experiments.Experiment;
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
    private Experiment<?> experiment;
    private Button registerButton;
    private TextView codePreviewTextView;
    private TextView registeredStatusTextView;
    private String scannedCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_scanner);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));

        codePreviewTextView = findViewById(R.id.scanned_code);
        registeredStatusTextView = findViewById(R.id.registeredStatusTextView);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setVisibility(View.INVISIBLE);
        registerButton.setOnClickListener(v ->
            new TrialFragment(experiment,
                    newTrial -> TrialManager.registerBarcode(scannedCode, experiment, newTrial,
                            barcode-> {
                                Toast.makeText(this, "Barcode registered.", Toast.LENGTH_LONG).show();
                                registeredStatusTextView.setText(getRegistrationStatusString(barcode));
                            },
                            exception-> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show())
            ).show(getSupportFragmentManager(), "ADD_TRIAL"));

        findViewById(R.id.scanButton).setOnClickListener(this::scanCode);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

        if (result != null && result.getContents() != null) {
            codePreviewTextView = findViewById(R.id.scanned_code);
            codePreviewTextView.setText(result.getContents());
            scannedCode = result.getContents();
            registerButton.setVisibility(View.VISIBLE);

            TrialManager.readBarcode(scannedCode,
                    barcode -> registeredStatusTextView.setText(getRegistrationStatusString(barcode)),
                    e -> {
                        registeredStatusTextView.setText(R.string.barcode_not_registered);
                        Log.d(TAG, "Barcode not registered");
            });
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    String getRegistrationStatusString(TrialManager.Barcode barcode) {
        Experiment<?> experiment = ExperimentManager.getExperiment(barcode.getExperimentId());
        String status = "Barcode registered as:\n" +
                barcode.getTrial().getType() + " Trial: " + barcode.getTrial().getValueString() +
                "\nExperiment: " + experiment.info.getDescription();
        return status;
    }
}