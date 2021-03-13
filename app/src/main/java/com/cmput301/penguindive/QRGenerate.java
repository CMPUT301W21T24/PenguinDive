package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.Encoder;

public class QRGenerate extends AppCompatActivity {

    String QRString;
    EditText experName;
    Spinner trialType;
    Spinner passfail;
    ImageView QRCode;
    Button generate;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generate);

        experName = findViewById(R.id.experiment_name);
        trialType = findViewById(R.id.trial_type);
        QRCode = findViewById(R.id.QR_code);
        generate = findViewById(R.id.generate);
        save = findViewById(R.id.saveButton);
        passfail = findViewById(R.id.pass_fail);

        save.setVisibility(Button.GONE);

        generate.setOnClickListener(v -> {

            String name = experName.getText().toString();
            String type = trialType.getSelectedItem().toString();
            String passFail = passfail.getSelectedItem().toString();
            QRString = name + "-" + type + "-" + passFail;

            MultiFormatWriter mfw = new MultiFormatWriter();

            try {
                BitMatrix bMat = mfw.encode(QRString, BarcodeFormat.QR_CODE, 500, 500);
                // TODO: show and save QRCode
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
   }
}