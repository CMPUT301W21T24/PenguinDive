package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

/*
developed with help from
used for camera access
Date Created: Nov 10, 2019
Created by: SmallAcademy: https://www.youtube.com/channel/UCR1t5eSmLxLUdBnK2XwZOuw
licence: Creative commons or Youtube standard licence
URL: https://www.youtube.com/watch?v=Iuj4CuWjYF8

used for basic setup of a QR scanner
Date created: Nov 14, 2019
Date last modified: Nov 14, 2019
Created by: bikashthapa01: https://github.com/bikashthapa01
Licence: unknown (could not find)
URL: https://github.com/bikashthapa01/QR-APP/tree/master/app

 */
public class QRScanner extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private TextView qrText;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);

        // initialize variables
        scannerView = findViewById(R.id.scanner_view);
        qrText = findViewById(R.id.QR_text);
        btn = findViewById(R.id.btn_id);

        // setup a scanner object to scan a QR code when it is centered on the camera on screen
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrText.setText(result.getText());
                    }
                });
            }
        });
        
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCam();
    }

    // function requests access to the phone camera
    protected void requestCam() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                mCodeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(QRScanner.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).check();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}