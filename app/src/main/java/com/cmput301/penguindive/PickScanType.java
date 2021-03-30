package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class PickScanType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_scan_type);

        final Button QR = findViewById(R.id.QR);
        final Button barcode = findViewById(R.id.bar);

        QR.setOnClickListener(v -> {
            Intent intent = new Intent(PickScanType.this, QRScanner.class);
            intent.putExtra("type", "Q");
            startActivity(intent);
        });

        barcode.setOnClickListener(v -> {
            Intent intent = new Intent(PickScanType.this, QRScanner.class);
            intent.putExtra("type", "B");
            startActivity(intent);
        });
    }
}