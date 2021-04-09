package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This class lets the user choose whether to create a QR code for a trial result or to advertise an experiment
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

    /**
     * This method refreshes the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, PickQRType.class);
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
     * @param view
     * Takes a view representing the current view
     */
    public void ClickScanQrCode(View view){ MainActivity.redirectActivity(this, PickScanType.class); }

    /**
     * This method redirects the user to the PickQRType Activity
     * Since the user is already on the PickQRType Activity, this simply closes the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGenerateQrCode(View view){ MainActivity.closeDrawer(drawerLayout);  }

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
