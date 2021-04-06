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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    CollectionReference experimentCollectionReference = db.collection("Experiments");

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
                Integer newNameLength = name.getText().toString().length();
                // If no entry username to UID
                if (newNameLength.equals(0)){
                    docRef.update("name",uid);
                    updateUserName(uid); // Make changes to firestore
                }
                else {
                    docRef.update("name",name.getText().toString());
                    updateUserName(name.getText().toString()); // Make changes to firestore
                }
                docRef.update("email",email.getText().toString());
                Toast.makeText(Profile.this, "Username and Email successfully updated!", Toast.LENGTH_SHORT ).show();

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

    /**
     * This method will gather all experiment documents that he user has created
     * It will then update the "ownerName" and "Keywords" fields to reflect the new user name
     * @param newName
     * A string that represents the new desired user name
     */
    public void updateUserName(String newName){
        experimentCollectionReference.addSnapshotListener((value, error) -> {
            for(QueryDocumentSnapshot doc: Objects.requireNonNull(value)) {
                String ownerId = (String) doc.getData().get("ownerId");
                if (ownerId.equals(uid)) {
                    String experimentId = doc.getId();
                    experimentCollectionReference.document(experimentId).update("ownerName", newName);
                    Experiment currentExperiment = makeExperiment(doc); // Make the experiment for easier reference
                    List<String> newKeywords = getExperimentKeywords(currentExperiment); // Get the keywords
                    experimentCollectionReference.document(experimentId).update("Keywords", newKeywords); // Update database
                }
            }
        });
    }

    /**
     * This method makes an Experiment object
     * @param doc
     * A QueryDocumentSnapshot that can be used to get all fields for the experiment class
     * @return
     * A Experiment item with all values filled in
     */
    public Experiment makeExperiment(QueryDocumentSnapshot doc){
        String expID = doc.getId();
        String description = (String) doc.getData().get("Description");
        String region = (String) doc.getData().get("Region");
        String title = (String) doc.getData().get("Title");
        String status = (String) doc.getData().get("Status");

        // For casting long to int, https://stackoverflow.com/a/32869210
        Integer minTrials = Math.toIntExact((Long) doc.getData().get("MinimumTrials"));
        String ownerId = (String) doc.getData().get("ownerId");
        String ownerName = (String) doc.getData().get("ownerName");
        List<String> experimenters = (List<String>) doc.getData().get("experimentIDs");
        String trialType = (String) doc.getData().get("TrialType");
        Boolean locationStatus = (Boolean) doc.getData().get("LocationStatus");

        return new Experiment(expID, title, description, region, minTrials, ownerId, ownerName, status, experimenters, locationStatus, trialType);
    }

    /**
     * This method gets all searchable keywords from an experiment.
     * This includes it's title, description, ownerId, ownerUserName, status and region
     * @param newExperiment
     * An experiment object to reference when required values
     * @return
     * A list of strings containing all keywords gathered within the experiment
     */
    public List<String> getExperimentKeywords(Experiment newExperiment){
        List<String> keywords;
        keywords = new ArrayList<>();
        // For getting it to add multiple elements properly
        // https://stackoverflow.com/a/36560577
        // For whitespace and punctuation
        // https://stackoverflow.com/a/28257108
        keywords.add(newExperiment.getOwnerId().trim().toLowerCase()); // Full UserId will need to be searched
        keywords.add(newExperiment.getOwnerUserName().trim().toLowerCase()); // Full Username will need to be searched
        keywords.addAll(Arrays.asList(newExperiment.getTitle().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getDescription().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getRegion().toLowerCase().trim().split("\\W+")));
        keywords.add(newExperiment.getStatus().trim().toLowerCase());

        return keywords;
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