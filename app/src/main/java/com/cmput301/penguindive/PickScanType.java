package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
This class lets the user choose to register a barcode, scan a barcode for a trial result, or
scan a QR code (either for a trial result or to advertise an experiment
 */
public class PickScanType extends AppCompatActivity {
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_scan_type);

        // Assign drawer
        drawerLayout = findViewById(R.id.qrscantype);

        // buttons for choice
        final Button QR = findViewById(R.id.QR);
        final Button barcode = findViewById(R.id.bar);
        final Button barReg = findViewById(R.id.bar_reg);

        // if QR code choice
        QR.setOnClickListener(v -> {
            Intent intent = new Intent(PickScanType.this, QRScanner.class);
            intent.putExtra("type", "SQ");
            startActivity(intent);
        });

        // if wanting to scan a barcode
        barcode.setOnClickListener(v -> {
            Intent intent = new Intent(PickScanType.this, QRScanner.class);
            intent.putExtra("type", "SB");
            startActivity(intent);
        });

        // if wanting to register a barcode
        barReg.setOnClickListener(v -> {
            Intent intent = new Intent(PickScanType.this, QRScanner.class);
            intent.putExtra("type", "BR");
            startActivity(intent);
        });
    }

    // Refresh method
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, PickScanType.class);
    }
    public void ClickMenu(View view){ MainActivity.openDrawer(drawerLayout);}

    public void ClickLogo(View view){ MainActivity.closeDrawer(drawerLayout);}

    public void ClickHome(View view){MainActivity.redirectActivity(this,MainActivity.class); }

    public void ClickMyExperiments(View view){ MainActivity.redirectActivity(this,MyExperimentActivity.class); }

    public void ClickScanQrCode(View view){ MainActivity.closeDrawer(drawerLayout); }

    public void ClickGenerateQrCode(View view){ MainActivity.redirectActivity(this, PickQRType.class); }

    public void ClickMyProfile(View view){
        MainActivity.redirectActivity(this,Profile.class);
    }

    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

    public void ClickGitHub(View view){ MainActivity.openGitHub(this); }

}
