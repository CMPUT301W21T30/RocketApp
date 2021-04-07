package com.example.rocketapp.view.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.view.TrialFragment;

import java.io.File;
import java.io.FileOutputStream;

public class GenerateQRcodeActivity extends AppCompatActivity {
    private static final String TAG = "GenerateQRCodeActivity";
    private Experiment<?> experiment;
    private ImageView qrImageView;
    private Button saveButton;
    private TextView codeTextPreview;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.generate_qrcode_activity);

        codeTextPreview = findViewById(R.id.generatedCodeTextView);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));

        ((TextView) findViewById(R.id.experimentType2)).setText(experiment.getType());

        qrImageView = findViewById(R.id.qrCodeImageView);

        saveButton = findViewById(R.id.saveQRcodeBtn);
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(v -> saveToGallery());

        findViewById(R.id.generateQRcodeBtn).setOnClickListener(v ->
                new TrialFragment(
                        experiment,
                        newTrial -> {
                            TrialManager.createQRCodeBitmap(experiment, newTrial,
                                    bitmap -> qrImageView.setImageBitmap(bitmap),
                                    generatedString -> codeTextPreview.setText(generatedString),
                                    exception -> Log.e(TAG, exception.toString()));

                            giveConfirmation();
                            saveButton.setVisibility(View.VISIBLE);
                        }
                ).show(getSupportFragmentManager(), "ADD_TRIAL"));

        ActivityCompat.requestPermissions(GenerateQRcodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(GenerateQRcodeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

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

    public void giveConfirmation(){
        Toast.makeText(this, "Generated!", Toast.LENGTH_LONG).show();
    }


    /*
     * https://www.youtube.com/watch?v=FcCtT1C7NGI
     * Author: Ketul Patel
     *
     * https://stackoverflow.com/questions/26718374/save-image-from-imageview-to-device-gallery
     * Author: Shravan DG  https://stackoverflow.com/users/6646750/shravan-dg
     */
    private void saveToGallery(){
        //TODO move this functionality to TrialManager class
        BitmapDrawable bitmapDrawable = (BitmapDrawable) qrImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/QRcodes");

        dir.mkdirs();
        String filename = String.format("%d.png", System.currentTimeMillis());
        File outFile = new File(dir, filename);
        try {
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        try {
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);

    }


}
