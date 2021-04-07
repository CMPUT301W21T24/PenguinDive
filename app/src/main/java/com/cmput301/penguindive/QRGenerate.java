package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.BarcodeMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/*
Developed with help from:
Date created: Nov 10, 2019
Create by: SmallAcademy: https://www.youtube.com/channel/UCR1t5eSmLxLUdBnK2XwZOuw
licence: creative commons or standard YouTube licence
URL: https://www.youtube.com/watch?v=NpVRUhEpRI8

Date created: Nov 14, 2012
Created by: user1820528, edited by: A-Sharabiani
answered by: Nicolas Tyler: https://stackoverflow.com/users/2027232/nicolas-tyler
using this answer: https://stackoverflow.com/a/17650125
Licence: CC BY-SA 3.0
URL: https://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list

Created by: Saiful Alam
Date created: Sept 29, 2015
licence: unknown (bottom of page says copyright 2016)
URL: https://android--code.blogspot.com/2015/09/android-how-to-save-image-to-gallery.html

This function allows a user to generate a QR code and save it to their photo gallery
 */
public class QRGenerate extends AppCompatActivity {

    String QRString;
    Spinner experName;
    Spinner passfail;
    ImageView QRCode;
    Button generate;
    Button save;
    Button back;
    Bitmap bMap;
    TextView imSaved;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generate);

        // Assign drawer
        drawerLayout = findViewById(R.id.qrgenerate);

        // TODO: add a trial name spinner for each experiment to allow choosing for which trial?
        // initialize variables
        experName = findViewById(R.id.experiment_name);
        QRCode = findViewById(R.id.QR_code);
        generate = findViewById(R.id.generate);
        save = findViewById(R.id.saveButton);
        passfail = findViewById(R.id.pass_fail);
        back = findViewById(R.id.goBack);
        imSaved = findViewById(R.id.image_saved);

        String choice = getIntent().getSerializableExtra("type").toString();

        ArrayList<String> experimentNames = new ArrayList<>();
        // get experiment names from database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Experiments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                ArrayAdapter<String> namesAdapt = new ArrayAdapter<>(QRGenerate.this, R.layout.support_simple_spinner_dropdown_item, eNames);
                experName.setAdapter(namesAdapt);
            }
        });

        // set the dropdown menu entries
        String[] passOrFail = {"SUCCESS", "FAILURE"};
        ArrayAdapter<String> passFailAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, passOrFail);
        passfail.setAdapter(passFailAdapt);

        // hide buttons until QR code is generated
        save.setVisibility(Button.GONE);
        back.setVisibility(Button.GONE);

        if (choice.equals("ad")) {
            passfail.setVisibility(Spinner.GONE);
        }

        // upon button click, take information from dropdowns and text box
        // and combine into data for the QR code
        generate.setOnClickListener(v -> {

            String name = experName.getSelectedItem().toString();
            String passFail = passfail.getSelectedItem().toString();
            if (choice.equals("ad")) {
                QRString = name;
            } else {
                QRString = "QR-" + name + "-" + passFail;
            }

            // generate QR code and display to user
            QRGEncoder QRcode = new QRGEncoder(QRString, QRGContents.Type.TEXT, 500);
            QRcode.setColorBlack(Color.BLACK);
            QRcode.setColorWhite(Color.WHITE);
            try {
                bMap = QRcode.getBitmap();
                QRCode.setImageBitmap(bMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // hide input areas and allow user to either save the QR Code to local storage or go back
            generate.setVisibility(Button.GONE);
            experName.setVisibility(EditText.GONE);
            passfail.setVisibility(Spinner.GONE);

            save.setVisibility(Button.VISIBLE);
            back.setVisibility(Button.VISIBLE);
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(QRGenerate.this, MainActivity.class);
            startActivity(intent);
        });

        // save the code to local photo gallery
        save.setOnClickListener(v -> {
            String filename = "QRCode-" + QRString;

            String saved = MediaStore.Images.Media.insertImage(getContentResolver(), bMap, filename, "Trial QR code");
            imSaved.setText("Image " + filename + " saved to photos.  Click back to return to main");
            save.setVisibility(Button.GONE);
        });
   }

    // Refresh method
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, QRGenerate.class);
    }

    public void ClickMenu(View view){ MainActivity.openDrawer(drawerLayout);}

    public void ClickLogo(View view){ MainActivity.closeDrawer(drawerLayout);}

    public void ClickHome(View view){MainActivity.redirectActivity(this,MainActivity.class); }

    public void ClickMyExperiments(View view){ MainActivity.redirectActivity(this,MyExperimentActivity.class); }

    public void ClickScanQrCode(View view){ MainActivity.redirectActivity(this,PickScanType.class);  }

    public void ClickGenerateQrCode(View view){ MainActivity.closeDrawer(drawerLayout); }

    public void ClickMyProfile(View view){
        MainActivity.redirectActivity(this,Profile.class);
    }

    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

    public void ClickGitHub(View view){ MainActivity.openGitHub(this);}

}
