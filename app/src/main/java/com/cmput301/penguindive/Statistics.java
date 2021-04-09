package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class shows the user all current statistics for a given trial
 */
public class Statistics extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String type;
    String name;
    String expID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set view
        setContentView(R.layout.statistics);

        Intent intent = getIntent();
        expID = intent.getStringExtra("EXPID");

        DocumentReference docRef = db.collection("Experiments").document(expID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //Find the Name and the Trial Type of the Experiment
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        type = document.getString("TrialType");
                        name = document.getString("Title");
                    }}
                //Prepare and display bar plot.
                if (type.equals("Count Trial")){

                }else if (type.equals("Binomial Trial")){
                }else if (type.equals("Measurement Trial")){
                }else if (type.equals("Non-Negative Integer Count Trial")) {
                }
            }
        });
    }
}
