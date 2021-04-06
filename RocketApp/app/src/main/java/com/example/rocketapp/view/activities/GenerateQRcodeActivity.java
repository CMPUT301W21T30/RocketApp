package com.example.rocketapp.view.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.BinomialTrial;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

import io.grpc.Context;

public class GenerateQRcodeActivity extends AppCompatActivity {
    private ImageView qrcode;
    private Button generateBtn;
    private EditText trialsEditText;
    private Experiment experiment;
    private CheckBox registerpass;
    private CheckBox registerfail;
    private TextView experimentType;
    private TextView checkGenerate;
    private Button saveQRcodeBtn;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.generate_qrcode_activity);
        registerpass = findViewById(R.id.registerpass2);
        registerfail = findViewById(R.id.registerfail2);
        experimentType = findViewById(R.id.experimentType2);
        trialsEditText = findViewById(R.id.trialEditText2);
        saveQRcodeBtn = findViewById(R.id.saveQRcodeBtn);

        checkGenerate = findViewById(R.id.checkGenerate);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));


        experimentType.setText(experiment.getType());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);


        generateBtn = findViewById(R.id.generateQRcodeBtn);
        qrcode = findViewById(R.id.QRcodeimageview);

        if (experiment.getType().equals(BinomialExperiment.TYPE)) {
            trialsEditText.setVisibility(View.GONE);
        } else {
            registerpass.setVisibility(View.GONE);
            registerfail.setVisibility(View.GONE);
        }


        generateBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (experiment.getType().equals(BinomialTrial.TYPE)){
                    if(registerpass.isChecked() && !(registerfail.isChecked())){

                        String sText = experiment.getId() + "%" +  "pass";
                        //Initialize multi format writer
                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            //Initialize bit matrix
                            BitMatrix matrix = writer.encode(sText, BarcodeFormat.QR_CODE, 800,800);
                            //Initialize barcode encoder
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            //Initialize bitmap
                            Bitmap bitmap = encoder.createBitmap(matrix);
                            //Set bitmap on image view
                            qrcode.setImageBitmap(bitmap);



                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        checkGenerate.setText(sText);
                        giveConfirmation();

                    }
                    else if (registerfail.isChecked() && !(registerpass.isChecked())){
                        String sText = experiment.getId() + "%" + "fail";
                        //Initialize multi format writer
                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            //Initialize bit matrix
                            BitMatrix matrix = writer.encode(sText, BarcodeFormat.QR_CODE, 800,800);
                            //Initialize barcode encoder
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            //Initialize bitmap
                            Bitmap bitmap = encoder.createBitmap(matrix);
                            //Set bitmap on image view
                            qrcode.setImageBitmap(bitmap);



                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        checkGenerate.setText(sText);
                        giveConfirmation();
                    }
                    else{
                        //cannot select both
                        giveError();
                    }
                }
                else {
                    String sText = experiment.getId() + "%" + trialsEditText.getText().toString();
                    //Initialize multi format writer
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        //Initialize bit matrix
                        BitMatrix matrix = writer.encode(sText, BarcodeFormat.QR_CODE, 800,800);
                        //Initialize barcode encoder
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        //Initialize bitmap
                        Bitmap bitmap = encoder.createBitmap(matrix);
                        //Set bitmap on image view
                        qrcode.setImageBitmap(bitmap);



                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    checkGenerate.setText(sText);
                    giveConfirmation();

                }

                //Get input value from edit text

            }
        });

        ActivityCompat.requestPermissions(GenerateQRcodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(GenerateQRcodeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        saveQRcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();
            }
        });


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

    public void giveError(){
        Toast.makeText(this, "Cannot do this", Toast.LENGTH_LONG).show();
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
        BitmapDrawable bitmapDrawable = (BitmapDrawable) qrcode.getDrawable();
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
