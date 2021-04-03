package com.cmput301.penguindive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class represents an activity that will show the user's profile
 */

public class Profile extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private TextView id;
    Button saveButton;
    Button searchButton;
    String uid;
    DrawerLayout drawerLayout;


    //private ArrayList<Experiment> ownedExperiments;
    //private ArrayList<Experiment> subscribedExperiments;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference profileCollectionReference = db.collection("Experimenter");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set view
        setContentView(R.layout.show_profile);
        //initialize elements
        id = findViewById(R.id.UserID);
        name = findViewById(R.id.userName);
        email = findViewById(R.id.Email);
        saveButton = findViewById(R.id.saveButton);
        searchButton = findViewById(R.id.searchButton);
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        uid = sharedPref.getString("UID", "");

        // Assign drawer
        drawerLayout = findViewById(R.id.showprofile);

        //make a query on the given profile to display the id, name, contact information of the instance's user
        DocumentReference docRef = profileCollectionReference.document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    id.setText("User ID: " + document.getId());
                    name.setText(document.getString("name"));
                    email.setText(document.getString("email"));

                }
            }
        });

        // On press update the profile collection of the Firestore Database
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.update("name",name.getText().toString());
                docRef.update("email",email.getText().toString());
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, SearchProfile.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
    // return to the main activity on back button press
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void ClickMenu(View view){ MainActivity.openDrawer(drawerLayout);}

    public void ClickLogo(View view){ MainActivity.closeDrawer(drawerLayout);}

    public void ClickHome(View view){MainActivity.redirectActivity(this,MainActivity.class); }

    public void ClickMyExperiments(View view){ MainActivity.redirectActivity(this,MyExperimentActivity.class); }

    public void ClickScanQrCode(View view){ MainActivity.redirectActivity(this,PickScanType.class);  }

    public void ClickGenerateQrCode(View view){ MainActivity.redirectActivity(this,PickQRType.class);}

    public void ClickMyProfile(View view){ MainActivity.closeDrawer(drawerLayout); }

    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

}