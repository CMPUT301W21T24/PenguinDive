package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class PickQRType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_q_r_type);

        final Button exper = findViewById(R.id.exper);
        final Button trial = findViewById(R.id.trial);

        exper.setOnClickListener(v -> {
            Intent intent = new Intent(PickQRType.this, QRGenerate.class);
            intent.putExtra("type", "ad");
            startActivity(intent);
        });

        trial.setOnClickListener(v -> {
            Intent intent = new Intent(PickQRType.this, QRGenerate.class);
            intent.putExtra("type", "tr");
            startActivity(intent);
        });
    }
}