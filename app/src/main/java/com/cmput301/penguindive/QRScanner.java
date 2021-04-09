package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    // global variables
    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private TextView qrText;
    DrawerLayout drawerLayout;
    Spinner experName;
    Spinner trueFalse;
    Spinner barcodeExper;
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
        barcodeExper = findViewById(R.id.barcode_exper);
        choice = getIntent().getSerializableExtra("type").toString();

        // get experiment names for registering barcodes
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

        // set true false for registering barcodes
        String[] trialTypes = {"TRUE", "FALSE"};
        ArrayAdapter<String> typeAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, trialTypes);
        trueFalse.setAdapter(typeAdapt);

        // set the visibility of the UI elements
        trueFalse.setVisibility(Spinner.GONE);
        experName.setVisibility(Spinner.GONE);
        update.setVisibility(Button.GONE);
        barcodeExper.setVisibility(Button.GONE);

        // setup the scanner and read the code
        scanSetup();
        
        scannerView.setOnClickListener(view -> codeScanner.startPreview());

        // When update/register is pressed do something based on user choice
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

        // return to MainActivity
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(QRScanner.this, MainActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Function populates a spinner with experiments that have a registered barcode
     */
    private void populateBarcodeExper() {
        // set up variables
        String barcodeText = qrText.getText().toString();
        ArrayList<String> expers = new ArrayList<>();
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("UID", "");
        // check the database for any published experiments that hold the scanned barcode
        db.collection("Experiments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Map<String, Object> m = document.getData();
                    if (m.containsKey("Barcodes") && document.get("Status").equals("Published")) {
                        ArrayList<String> s = (ArrayList<String>) m.get("Barcodes");
                        for (int i = 0; i < s.size(); i++) {
                            if (s.get(i).contains(barcodeText) && s.get(i).contains(uid)) {
                                Log.d("experiment barcodes", (String) document.get("Title"));
                                expers.add((String) document.get("Title"));
                            }
                        }
                    }
                }
            } else {
                Log.d("Could not get data", "no Data to get");
            }
            // update dropdown menu
            String[] enames = new String[expers.size()];
            for (int i = 0; i < enames.length; i++) {
                enames[i] = expers.get(i);
            }
            // if there are no items in the dropdown then there are no registered barcodes, otherwise
            // show the update button and dropdown
            if (enames.length == 0) {
                qrText.setText("No registered barcodes match this scan");
            } else {
                ArrayAdapter<String> eAdapt = new ArrayAdapter(QRScanner.this, R.layout.support_simple_spinner_dropdown_item, enames);
                barcodeExper.setAdapter(eAdapt);
                update.setVisibility(Button.VISIBLE);
                barcodeExper.setVisibility(Button.VISIBLE);
            }

        });
    }

    /**
     * update trial result based on scanned barcode
     */
    private void barcodeScanned() {
        // if barcode is registered then update trial, else ask if they want to register the barcode
        String barCodeText = qrText.getText().toString();
        String ExperTitle = (String) barcodeExper.getSelectedItem();
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("UID", "");
        // get barcodes from the database, no need to check for published experiments as this is done before
        // this function can be called
        db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = null;
                    boolean barRegistered = false;
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Map<String, Object> m = document.getData();
                        if (m.containsKey("Barcodes") && document.get("Title").equals(ExperTitle)) {
                            ArrayList<String> s = (ArrayList<String>) m.get("Barcodes");
                            // get the scanned barcode in the experiment
                            for (int i = 0; i < s.size(); i++) {
                                if (s.get(i).contains(barCodeText) && s.get(i).contains(uid)) {
                                    doc = document;
                                    barRegistered = true;
                                    break;
                                }
                            }
                        }
                    }
                    // if a barcode is registered then update as per the registration
                    if (barRegistered) {
                        Map<String, Object> existingData = doc.getData();
                        String[] s = new String[4];
                        for (Map.Entry<String, Object> e : existingData.entrySet()) {
                            if (e.getKey().equals("Barcodes")) {
                                Object[] barcodes = e.getValue().toString().split(",");
                                for (int i = 0; i < barcodes.length; i++) {
                                    s = barcodes[i].toString().split("\\.");
                                    if (s[0].contains(barCodeText) && s[2].equals(uid)) {
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        // positive update
                        if (s[4].contains("TRUE")) {
                            qrText.setText("adding success to trial result for experiment");
                            db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        String tType = "";
                                        for (DocumentSnapshot doc12 : task.getResult()) {
                                            if (ExperTitle.equals(doc12.get("Title"))) {
                                                tType = (String) doc12.get("TrialType");
                                                break;
                                            }
                                        }
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("Experiment Name", ExperTitle);
                                        data.put("Trial Type", tType);
                                        switch (tType) {
                                            case "Measurement Trial":
                                                data.put("Measurement Name", "BarSuccess");
                                                data.put("Measurement", 1.0);
                                                break;
                                            case "Non-Negative Integer Count Trial":
                                                data.put("NNIC Name", "BarSuccess");
                                                data.put("Non-Negative Integer", 1);
                                                break;
                                            case "Binomial Trial":
                                                data.put("Binomial Type", "Pass");
                                                break;
                                            case "Count Trial":
                                                data.put("Increment Count", "TRUE");
                                                break;
                                        }
                                        db.collection("Trials").document(UUID.randomUUID().toString()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    qrText.setText("Trial successfully updated (success)!");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            // negative update
                        } else if (s[4].contains("FALSE")) {
                            qrText.setText("adding failure to trial result for experiment");
                            db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        String tType = "";
                                        for (DocumentSnapshot document : task.getResult()) {
                                            if (ExperTitle.equals(document.get("Title"))) {
                                                tType = (String) document.get("TrialType");
                                                break;
                                            }
                                        }
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("Experiment Name", ExperTitle);
                                        data.put("Trial Type", tType);
                                        switch (tType) {
                                            case "Measurement Trial":
                                                data.put("Measurement Name", "BarFailure");
                                                data.put("Measurement", 0.0);
                                                break;
                                            case "Non-Negative Integer Count Trial":
                                                data.put("NNIC Name", "BarFailure");
                                                data.put("Non-Negative Integer", -1);
                                                break;
                                            case "Binomial Trial":
                                                data.put("Binomial Type", "Fail");
                                                break;
                                            case "Count Trial":
                                                data.put("Increment Count", "FALSE");
                                                break;
                                        }
                                        db.collection("Trials").document(UUID.randomUUID().toString()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    qrText.setText("Trial successfully updated (failure)!");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            // no registered barcode matching the scanned barcode
                        } else {
                            qrText.setText("Could not determine result of barcode");
                        }
                        update.setVisibility(Button.GONE);
                        // could not find any barcodes
                    } else {
                        qrText.setText("Barcode does not seem to be registered.\nPlease register the barcode first");
                        update.setVisibility(Button.GONE);
                    }
                }
            }
        });
    }

    /**
     * function called on QR code scanned, very similar to barcodeScanned
     */
    private void QRScanned() {
        // initial check to ensure a proper QRcode is scanned
        if (qrText.getText().toString().contains("QR-")) {
            SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
            String uid = sharedPref.getString("UID", "");
            String[] experToUpdate = qrText.getText().toString().split("-");
            // if the QR code is for an advertisement then advertise the experiment
            if (experToUpdate.length == 2) {
                qrText.setText("Check out this experiment: " + experToUpdate[1]);
                // otherwise update the trial based on QR code formatting
            } else if (experToUpdate.length == 3) {
                // positive update
                if (experToUpdate[2].equals("SUCCESS")) {
                    // update trial result
                    qrText.setText("Adding success to: " + experToUpdate[1]);
                    db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String tType = "";
                                boolean pub = false;
                                for (DocumentSnapshot doc : task.getResult()) {
                                    if (experToUpdate[1].equals(doc.get("Title")) && doc.get("Status").equals("Published")) {
                                        tType = (String) doc.get("TrialType");
                                        pub = true;
                                        break;
                                    }
                                }
                                if (pub) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("Experiment Name", experToUpdate[1]);
                                    data.put("Trial Type", tType);
                                    switch (tType) {
                                        case "Measurement Trial":
                                            data.put("Measurement Name", "QRSuccess");
                                            data.put("Measurement", 1.0);
                                            break;
                                        case "Non-Negative Integer Count Trial":
                                            data.put("NNIC Name", "QRSuccess");
                                            data.put("Non-Negative Integer", 1);
                                            break;
                                        case "Binomial Trial":
                                            data.put("Binomial Type", "Pass");
                                            break;
                                        case "Count Trial":
                                            data.put("Increment Count", "TRUE");
                                            break;
                                    }
                                    db.collection("Trials").document(UUID.randomUUID().toString()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                qrText.setText("Trial successfully updated!");
                                            }
                                        }
                                    });
                                } else {
                                    qrText.setText("No experiment with that name exists");
                                }
                            }
                        }
                    });
                    // negative update
                } else if (experToUpdate[2].equals("FAILURE")) {
                    qrText.setText("Adding failure to: " + experToUpdate[1]);
                    db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String tType = "";
                                boolean pub = false;
                                for (DocumentSnapshot doc : task.getResult()) {
                                    if (experToUpdate[1].equals(doc.get("Title")) && doc.get("Status").equals("Published")) {
                                        tType = (String) doc.get("TrialType");
                                        pub = true;
                                        break;
                                    }
                                }
                                if (pub) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("Experiment Name", experToUpdate[1]);
                                    data.put("Trial Type", tType);
                                    switch (tType) {
                                        case "Measurement Trial":
                                            data.put("Measurement Name", "QRFailure");
                                            data.put("Measurement", 0.0);
                                            break;
                                        case "Non-Negative Integer Count Trial":
                                            data.put("NNIC Name", "QRFailure");
                                            data.put("Non-Negative Integer", -1);
                                            break;
                                        case "Binomial Trial":
                                            data.put("Binomial Type", "Fail");
                                            break;
                                        case "Count Trial":
                                            data.put("Increment Count", "FALSE");
                                            break;
                                    }
                                    db.collection("Trials").document(UUID.randomUUID().toString()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                qrText.setText("Trial successfully updated!");
                                            }
                                        }
                                    });
                                } else {
                                    qrText.setText("No experiment with that name exists");
                                }
                            }
                        }
                    });
                    // not properly formatted
                } else {
                    qrText.setText("QR code not applicable");
                }
                // not properly formatted
            } else {
                qrText.setText("Invalid QR code");
            }
            // not properly formatted
        } else {
            qrText.setText("Invalid QR code");
        }
        update.setVisibility(Button.GONE);
    }

    /**
     * register a new barcode to a published experiment
     */
    private void registerBar() {
        // get barcode and user id
        String barcode = qrText.getText().toString();
        HashMap<String, String> choiceMap = new HashMap<>();
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        String uid = sharedPref.getString("UID", "");
        String experTrial = qrText.getText().toString() + ".exp:." + uid + ".add trial result:." + trueFalse.getSelectedItem();
        // update specific experiment to contain the new barcode
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
                    // update only if the user has not registered the barcode to this experiment yet.
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
                    boolean alreadyReg = false;
                    // only update if the user has not registered this barcode
                    for (int i = 0; i < barcodes.size(); i++) {
                        if (barcodes.get(i).equals(experTrial) && barcodes.get(i).contains(uid)) {
                            qrText.setText("barcode already registered");
                            alreadyReg = true;
                            break;
                        }
                    }
                    if (!alreadyReg) {
                        barcodes.add(experTrial);
                        existingData.put("Barcodes", barcodes);
                        db.collection("Experiments").document(doc.getId()).set(existingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    qrText.setText("Barcode registered Successfully");
                                    update.setVisibility(Button.GONE);
                                    Log.d("data", "data successfully updated");
                                } else {
                                    Log.d("data", "could not update experiment");
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * scans the QR or barcode and determines the next steps
     */
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
                    populateBarcodeExper();
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

    /**
     * This method refreshes the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, PickScanType.class);
    }

    /**
     * This method gives the current drawer layout to the openDrawer method in MainActivity
     * It is called when the hamburger icon is clicked on the toolbar
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMenu(View view){ MainActivity.openDrawer(drawerLayout);}

    /**
     * This method gives the current drawer layout to the closeDrawer method in MainActivity
     * It is called when the PenguinDive logo is clicked in the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickLogo(View view){ MainActivity.closeDrawer(drawerLayout);}

    /**
     * This method redirects the user to MainActivity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickHome(View view){MainActivity.redirectActivity(this,MainActivity.class); }

    /**
     * This method redirects the user to the MyExperimentActivity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMyExperiments(View view){ MainActivity.redirectActivity(this,MyExperimentActivity.class); }

    /**
     * This method redirects the user to the PickScanType Activity
     * Since the user is already scanning a QR code, this simply closes the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickScanQrCode(View view){ MainActivity.closeDrawer(drawerLayout); }

    /**
     * This method redirects the user to the PickQRType Activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGenerateQrCode(View view){ MainActivity.redirectActivity(this, PickQRType.class); }

    /**
     * This method redirects the user to the their profile page (Profile Activity)
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMyProfile(View view){
        MainActivity.redirectActivity(this,Profile.class);
    }

    /**
     * This method redirects the user to the search users page (SearchProfile Activity)
     * @param view
     * Takes a view representing the current view
     */
    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

    /**
     * This method calls openGitHub in MainActivity and provides it the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGitHub(View view){ MainActivity.openGitHub(this); }

}
