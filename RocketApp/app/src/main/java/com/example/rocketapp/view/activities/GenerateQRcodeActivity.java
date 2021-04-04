package com.example.rocketapp.view.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.BinomialExperiment;
import com.example.rocketapp.model.experiments.Experiment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import io.grpc.Context;

public class GenerateQRcodeActivity extends AppCompatActivity {
    private ImageView qrcode;
    private Button generateBtn;
    private EditText trialsEditText;
    private Experiment experiment;
    private CheckBox registerpass;
    private CheckBox registerfail;
    private TextView experimentType;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.generate_qrcode_activity);
        registerfail = findViewById(R.id.registerpass2);
        registerpass = findViewById(R.id.registerfail2);
        experimentType = findViewById(R.id.experimentType2);
        trialsEditText = findViewById(R.id.trialEditText2);

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
                //Get input value from edit text
                String sText = experiment.info.getDescription();
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

}
