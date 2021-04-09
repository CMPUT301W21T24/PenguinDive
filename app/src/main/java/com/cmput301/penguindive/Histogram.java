package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/*
Developped with help from:
PhilJay's MPAndroidChart on GitHub
Created By: Philipp Jahoda
Last Updated on October 29, 2020
License: Copyright 2020 Philipp Jahoda
Licensed under the Apache License, Version 2.0
URL: https://github.com/PhilJay/MPAndroidChart
 */

//This class allows the user to display a bar-graph of the frequency a trial's associated data is repeated.
public class Histogram extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    BarChart barChart;

    ArrayList<String> count = new ArrayList<>();
    HashMap<String,String> hm = new HashMap<>();

    String type;
    String name;
    String expID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set view
        setContentView(R.layout.histogram);
        barChart = findViewById(R.id.bar_chart);

        //determine what Trial type belongs to the experiment
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
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    count.add(document.getString("Count Type"));
                                }
                            }
                            int TRUE = Collections.frequency(count,"TRUE");
                            int FALSE = Collections.frequency(count,"FALSE");
                            int total = TRUE - FALSE;

                            ArrayList<BarEntry> entries = new ArrayList<>();

                            BarEntry barEntry = new BarEntry(0, total);
                            entries.add(barEntry);

                            BarDataSet barDataSet = new BarDataSet(entries, type);

                            BarData data = new BarData(barDataSet);
                            barChart.setData(data);
                            barChart.invalidate();
                        }

                    });

                    //If the experiment is of type Binomial Trial display the frequency of pass and fail
                }else if (type.equals("Binomial Trial")){
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    count.add(document.getString("Binomial Type"));
                                }
                            }
                            int TRUE = Collections.frequency(count,"Pass");
                            int FALSE = Collections.frequency(count,"Fail");
                            ArrayList<BarEntry> entries = new ArrayList<>();

                            BarEntry barEntry = new BarEntry(0, TRUE);
                            entries.add(barEntry);
                            barEntry = new BarEntry(1,FALSE);
                            entries.add(barEntry);
                            BarDataSet barDataSet = new BarDataSet(entries, type);

                            BarData data = new BarData(barDataSet);
                            barChart.setData(data);
                            barChart.invalidate();
                        }

                    });

                //If the experiment is of type Measurement display the frequency each measurement shows up in a trial
                }else if (type.equals("Measurement Trial")){
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i = 0;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String value = document.getString("Measurement");
                                    count.add(value);
                                    hm.put(value,String.valueOf(i));
                                    i++;
                                }
                            }
                            ArrayList<BarEntry> entries = new ArrayList<>();
                            int x = 1;
                            for (String i : hm.keySet()){
                                int a = Collections.frequency(count,i);
                                BarEntry barEntry = new BarEntry(x,a);
                                entries.add(barEntry);
                                x++;
                            }

                            BarDataSet barDataSet = new BarDataSet(entries, type);
                            BarData data = new BarData(barDataSet);
                            barChart.setData(data);
                            barChart.invalidate();
                        }

                    });

                    //If the experiment is of type Non-negative Integer display the frequency each integer shows up in a trial
                }else if (type.equals("Non-Negative Integer Count Trial")) {
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i = 0;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String value = document.getString("Non-Negative Integer");
                                    count.add(value);
                                    hm.put(value,String.valueOf(i));
                                    i++;
                                }
                            }
                            ArrayList<BarEntry> entries = new ArrayList<>();
                            int x = 1;
                            for (String i : hm.keySet()){
                                int a = Collections.frequency(count,i.toString());
                                BarEntry barEntry = new BarEntry(x,a);
                                entries.add(barEntry);
                                x++;
                            }

                            BarDataSet barDataSet = new BarDataSet(entries, type);
                            BarData data = new BarData(barDataSet);
                            barChart.setData(data);
                            barChart.invalidate();
                        }

                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Histogram.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
