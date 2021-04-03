package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private TextView qrText;
    DrawerLayout drawerLayout;
    Spinner experName;
    Spinner trueFalse;
    Button update;
    String choice;
    ArrayList<String> experimentNames;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);

        // Assign drawer
        drawerLayout = findViewById(R.id.qrscanner);

        // initialize variables
        scannerView = findViewById(R.id.scanner_view);
        qrText = findViewById(R.id.QR_text);
        Button btn = findViewById(R.id.btn_id);
        experName = findViewById(R.id.exper);
        trueFalse = findViewById(R.id.true_false);
        update = findViewById(R.id.update);
        choice = getIntent().getSerializableExtra("type").toString();

        experimentNames = new ArrayList<>();
        db.collection("Experiments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    if (document.get("Status").equals("Published")) {
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
        });

        //TODO: add trials and trial descriptions instead of trial types
        String[] trialTypes = {"TRUE", "FALSE"};
        ArrayAdapter<String> typeAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, trialTypes);
        trueFalse.setAdapter(typeAdapt);

        trueFalse.setVisibility(Spinner.GONE);
        experName.setVisibility(Spinner.GONE);
        update.setVisibility(Button.GONE);

        // setup the scanner and read the code
        scanSetup();
        
        scannerView.setOnClickListener(view -> codeScanner.startPreview());

        update.setOnClickListener(v -> {
            switch (choice) {
                case "BR":
                    registerBar();
                    break;
                case "SQ":
                    QRScanned();
                    break;
                case "SB":
                    barcodeScanned();
                    break;
            }
        });

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(QRScanner.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void barcodeScanned() {
        // TODO: check database to see if barcode is already registered
        // if barcode is registered then update trial, else ask if they want to register the barcode
        String barCodeText = qrText.getText().toString();
        if (true/*barcode does not exist*/) {
            qrText.setText("Barcode does not seem to be registered.\nPlease register the barcode first");
        }
    }

    private void QRScanned() {
        //TODO: go to experiment trial given by QR code and update trial
        if (qrText.getText().toString().contains("QR-")) {
            String[] experToUpdate = qrText.getText().toString().split("-");
            // TODO: update trial result if QR code contains PASS
            if (experToUpdate[3].equals("PASS")) {
                // update trial result
                qrText.setText("trial updated per QR Code");
            } else {
                qrText.setText("QR code not applicable for updating trials");
            }
        } else {
            qrText.setText("Invalid QR code");
        }


        update.setVisibility(Button.GONE);
    }

    private void registerBar() {
        String barcode = qrText.getText().toString();
        HashMap<String, String> choiceMap = new HashMap<>();
        String experTrial = qrText.getText().toString() + "-exp:-" + experName.getSelectedItem().toString() + "-add trial result:-" + trueFalse.getSelectedItem();
        //TODO: add new field to experimenters or experiments document with registered barcodes
        db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = null;
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (document.get("Title").toString().equals(experName.getSelectedItem())) {
                            doc = document;
                        }
                    }
                    Log.d("experiment data", String.valueOf(doc.getData()));
                    Map<String, Object> existingData = doc.getData();
                    boolean barExist = false;
                    ArrayList<String> barcodes = new ArrayList<>();
                    String removed = "";
                    for (Map.Entry<String, Object> e : existingData.entrySet()) {
                        if (e.getKey().equals("Barcodes")) {
                            barExist = true;
                            barcodes = (ArrayList<String>) e.getValue();
                            removed = e.getKey();
                        }
                    }
                    if (barExist) {
                        existingData.remove(removed);
                    }
                    barcodes.add(experTrial);
                    existingData.put("Barcodes", barcodes);
                    db.collection("Experiments").document(doc.getId()).set(existingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("data", "data successfully updated");
                            } else {
                                Log.d("data", "could not update experiment");
                            }
                        }
                    });
                }
            }
        });
        update.setVisibility(Button.GONE);
    }

    private void scanSetup() {
        // setup a scanner object to scan a QR code when it is centered on the camera on screen
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            qrText.setText(result.getText());
            qrText.setTextColor(Color.WHITE);
            Log.d("qrText", qrText.getText().toString());

            // show only relevant information based on scan choice
            switch (choice) {
                case "SQ":
                    update.setText("Update");
                    update.setVisibility(Button.VISIBLE);
                    break;
                case "SB":
                    update.setText("Update");
                    update.setVisibility(Button.VISIBLE);
                    break;
                case "BR":
                    update.setText("Register");
                    update.setVisibility(Button.VISIBLE);
                    trueFalse.setVisibility(Spinner.VISIBLE);
                    experName.setVisibility(Spinner.VISIBLE);
                    break;
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCam();
        trueFalse.setVisibility(Spinner.GONE);
        experName.setVisibility(Spinner.GONE);
        update.setVisibility(Button.GONE);
        qrText.setText("");
    }

    // function requests access to the phone camera
    protected void requestCam() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
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
        codeScanner.releaseResources();
        trueFalse.setVisibility(Spinner.GONE);
        experName.setVisibility(Spinner.GONE);
        update.setVisibility(Button.GONE);
        qrText.setText("");
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
