package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.BarcodeMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

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

 */
public class QRGenerate extends AppCompatActivity {

    String QRString;
    EditText experName;
    Spinner trialType;
    Spinner passfail;
    ImageView QRCode;
    Button generate;
    Button save;
    Button back;
    Bitmap bMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generate);

        // initialize variables
        experName = findViewById(R.id.experiment_name);
        trialType = findViewById(R.id.trial_type);
        QRCode = findViewById(R.id.QR_code);
        generate = findViewById(R.id.generate);
        save = findViewById(R.id.saveButton);
        passfail = findViewById(R.id.pass_fail);
        back = findViewById(R.id.goBack);

        // set the dropdown menu entries
        String[] trialTypes = {"Binomial", "Count", "Measurement", "Non-Negative"};
        String[] passOrFail = {"Pass", "Fail"};
        ArrayAdapter<String> typeAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, trialTypes);
        ArrayAdapter<String> passFailAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, passOrFail);
        trialType.setAdapter(typeAdapt);
        passfail.setAdapter(passFailAdapt);

        // hide buttons until QR code is generated
        save.setVisibility(Button.GONE);
        back.setVisibility(Button.GONE);

        // upon button click, take information from dropdowns and text box
        // and combine into data for the QR code
        generate.setOnClickListener(v -> {

            String name = experName.getText().toString();
            String type = trialType.getSelectedItem().toString();
            String passFail = passfail.getSelectedItem().toString();
            QRString = name + "-" + type + "-" + passFail;

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
            trialType.setVisibility(Spinner.GONE);

            //save.setVisibility(Button.VISIBLE);
            back.setVisibility(Button.VISIBLE);
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(QRGenerate.this, MainActivity.class);
            startActivity(intent);
        });

        // TODO: save files to photo gallery
//        save.setOnClickListener(v -> {
//            String location = Context.getExternalFilesDir(null);
//            String filename = "QRCode-" + QRString;
//            File file = new File(location, filename + ".png");
//
//            try {
//                FileOutputStream fileOut = new FileOutputStream(file);
//                bMap.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
//                fileOut.close();
//
//                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                intent.setData
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
   }
}