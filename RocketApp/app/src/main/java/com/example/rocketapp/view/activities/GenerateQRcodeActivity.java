package com.example.rocketapp.view.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rocketapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import io.grpc.Context;

public class GenerateQRcodeActivity extends AppCompatActivity {
    private ImageView qrcode;
    private Button generateBtn;
    private EditText testString;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.generate_qrcode_activity);

        generateBtn = findViewById(R.id.generateQRcodeBtn);
        qrcode = findViewById(R.id.QRcodeimageview);
        testString = findViewById(R.id.QRcodetestString);

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //Get input value from edit text
                String sText = testString.getText().toString().trim();
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





}
