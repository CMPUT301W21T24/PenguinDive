package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
This class lets the user choose whether to create a QR code for a trial result or to advertise an experiment
 */
public class PickQRType extends AppCompatActivity {
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_q_r_type);

        // buttons
        final Button exper = findViewById(R.id.exper);
        final Button trial = findViewById(R.id.trial);

        // Assign drawer
        drawerLayout = findViewById(R.id.pickqrtype);

        // if creating for advertisement
        exper.setOnClickListener(v -> {
            Intent intent = new Intent(PickQRType.this, QRGenerate.class);
            intent.putExtra("type", "ad");
            startActivity(intent);
        });

        // if creating for trial result
        trial.setOnClickListener(v -> {
            Intent intent = new Intent(PickQRType.this, QRGenerate.class);
            intent.putExtra("type", "tr");
            startActivity(intent);
        });
    }

    // Refresh method
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, PickQRType.class);
    }

    public void ClickMenu(View view){ MainActivity.openDrawer(drawerLayout);}

    public void ClickLogo(View view){ MainActivity.closeDrawer(drawerLayout);}

    public void ClickHome(View view){MainActivity.redirectActivity(this,MainActivity.class); }

    public void ClickMyExperiments(View view){ MainActivity.redirectActivity(this,MyExperimentActivity.class); }

    public void ClickScanQrCode(View view){ MainActivity.redirectActivity(this, PickScanType.class); }

    public void ClickGenerateQrCode(View view){ MainActivity.closeDrawer(drawerLayout);  }

    public void ClickMyProfile(View view){
        MainActivity.redirectActivity(this,Profile.class);
    }

    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

    public void ClickGitHub(View view){ MainActivity.openGitHub(this); }

}
