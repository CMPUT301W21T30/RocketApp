package com.example.rocketapp.view.activities;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.ScannerManager;
import com.example.rocketapp.helpers.Device;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.view.fragments.TrialFragment;

public class GenerateQRcodeActivity extends AppCompatActivity {
    private static final String TAG = "GenerateQRCodeActivity";
    private Experiment<?> experiment;
    private ImageView qrImageView;
    private Button saveButton;
    private TextView codeTextPreview;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        codeTextPreview = findViewById(R.id.generatedCodeTextView);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));

        ((TextView) findViewById(R.id.experimentType2)).setText(experiment.getType() + " Trial");

        qrImageView = findViewById(R.id.qrCodeImageView);

        saveButton = findViewById(R.id.saveQRcodeBtn);
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(v -> Device.saveToGallery(qrImageView.getDrawable(), this));

        findViewById(R.id.generateQRcodeBtn).setOnClickListener(v -> generateQRCode());

        ActivityCompat.requestPermissions(GenerateQRcodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(GenerateQRcodeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        generateQRCode();
    }

    private void generateQRCode() {
        new TrialFragment(
                "Create QR Code for " + experiment.getType() + " Trial",
                experiment,
                newTrial -> {
                    ScannerManager.createQRCodeBitmap(experiment, newTrial,
                            bitmap -> qrImageView.setImageBitmap(bitmap),
                            generatedString -> codeTextPreview.setText(generatedString),
                            exception -> Log.e(TAG, exception.toString()));

                    giveConfirmation();
                    saveButton.setVisibility(View.VISIBLE);
                }
        ).show(getSupportFragmentManager(), "ADD_TRIAL");
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

    public void giveConfirmation(){
        Toast.makeText(this, "Generated!", Toast.LENGTH_LONG).show();
    }

}
