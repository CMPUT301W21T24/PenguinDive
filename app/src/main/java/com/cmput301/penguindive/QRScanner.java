package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Objects;

/*
License for the QR scanning repository used

MIT License

Copyright (c) 2018 Yuriy Budiyev [yuriy.budiyev@yandex.ru]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

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

This class allows users to scan a printed QR code and will read out the text of the QR code back to the user
 */
public class QRScanner extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private TextView qrText;
    private Button btn;
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);

        // Assign drawer
        drawerLayout = findViewById(R.id.qrscanner);

        // initialize variables
        scannerView = findViewById(R.id.scanner_view);
        qrText = findViewById(R.id.QR_text);
        btn = findViewById(R.id.btn_id);
        final Spinner experName = findViewById(R.id.exper);
        final Spinner trial = findViewById(R.id.trial);
        final EditText information = findViewById(R.id.info);
        final Button update = findViewById(R.id.update);
        String choice = getIntent().getSerializableExtra("type").toString();

        ArrayList<String> experimentNames = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (document.get("Status").equals("publish")) {
                            experimentNames.add(document.get("Title").toString());
                            Log.d("experiment names", document.get("Title").toString());
                        }
                    }
                } else {
                    Log.d("Could not get data", "no Data to get");
                }
                String[] eNames = experimentNames.toArray(new String[0]);
                ArrayAdapter<String> namesAdapt = new ArrayAdapter<>(QRScanner.this, R.layout.support_simple_spinner_dropdown_item, eNames);
                experName.setAdapter(namesAdapt);
            }
        });

        //TODO: add trials and trial descriptions instead of trial types

        String[] trialTypes = {"Binomial", "Count", "Measurement", "Non-Negative"};
        ArrayAdapter<String> typeAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, trialTypes);
        trial.setAdapter(typeAdapt);

        trial.setVisibility(Spinner.GONE);
        experName.setVisibility(Spinner.GONE);
        information.setVisibility(EditText.GONE);
        update.setVisibility(Button.GONE);

        // setup a scanner object to scan a QR code when it is centered on the camera on screen
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrText.setText(result.getText());
                        if (choice.equals("B")) {
                            trial.setVisibility(Spinner.VISIBLE);
                            experName.setVisibility(Spinner.VISIBLE);
                            information.setVisibility(EditText.VISIBLE);
                        } else {
                            update.setVisibility(Button.VISIBLE);
                        }
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

        update.setOnClickListener(v -> {
            //TODO: go to experiment trial given by QR code and update trial
            String[] experToUpdate = qrText.getText().toString().split("-");
            qrText.setText("trial updated per QR Code");
            update.setVisibility(Button.GONE);
        });

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(QRScanner.this, MainActivity.class);
            startActivity(intent);
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
    public void ClickMenu(View view){ MainActivity.openDrawer(drawerLayout);}

    public void ClickLogo(View view){ MainActivity.closeDrawer(drawerLayout);}

    public void ClickHome(View view){MainActivity.redirectActivity(this,MainActivity.class); }

    public void ClickMyExperiments(View view){ MainActivity.redirectActivity(this,MyExperimentActivity.class); }

    public void ClickScanQrCode(View view){ MainActivity.closeDrawer(drawerLayout); }

    public void ClickGenerateQrCode(View view){ MainActivity.redirectActivity(this,PickQRType.class);}

    public void ClickMyProfile(View view){
        MainActivity.redirectActivity(this,Profile.class);
    }

    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

    public void ClickGitHub(View view){ MainActivity.openGitHub(this); }
}
