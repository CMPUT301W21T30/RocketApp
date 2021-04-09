package com.example.rocketapp.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.ScannerManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.view.fragments.TrialFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

/**
 * ScannerActivity implements the scanner feature
 *
 * https://youtu.be/wfucGSKngq4
 *
 */
public class RegisterBarcodeActivity extends RocketAppActivity {
    private static final String TAG = "ExperimentScannerAct";
    private final int cameraPermissionRequestCode = 100;
    private Experiment experiment;
    private TextView codePreviewTextView, registeredStatusTextView;
    private Button registerButton;
    private String scannedCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_registration);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));

        codePreviewTextView = findViewById(R.id.scanned_code);
        registeredStatusTextView = findViewById(R.id.registeredStatusTextView);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setVisibility(View.INVISIBLE);
        registerButton.setOnClickListener(v ->
            new TrialFragment(experiment,
                    newTrial -> ScannerManager.registerBarcode(scannedCode, experiment, newTrial,
                            barcode-> registeredStatusTextView.setText(getRegistrationStatusString(barcode)),
                            exception-> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show())
            ).show(getSupportFragmentManager(), "ADD_TRIAL"));

        findViewById(R.id.scanButton).setOnClickListener(v -> scanCode());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

            ScannerManager.readBarcode(scannedCode,
                    barcode -> registeredStatusTextView.setText(getRegistrationStatusString(barcode)),
                    e -> {
                        registeredStatusTextView.setText(R.string.barcode_not_registered);
                        Log.d(TAG, "Barcode not registered");
            });
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == cameraPermissionRequestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "Must allow app access to Camera in device settings.", Toast.LENGTH_LONG).show();
            } else {
                scanCode();
            }
        }
    }


    private void scanCode() {
        if (!hasPermission(Manifest.permission.CAMERA, cameraPermissionRequestCode)) return;

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning");
        integrator.initiateScan();
    }


    private String getRegistrationStatusString(ScannerManager.Barcode barcode) {
        Experiment experiment = ExperimentManager.getExperiment(barcode.getExperimentId());
        return String.format("Barcode registered as:\n%s Trial: %s\nExperiment: %s", barcode.getTrial().getType(), barcode.getTrial().getValueString(), experiment.info.getDescription());
    }
}