package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class represents an activity where the user can choose what kind of statistics to view
 * Redirects the user to the appropriate activity
 */
public class StatsActivity extends AppCompatActivity {
    private String expID;
    Button statButton;
    Button histogramButton;
    Button plotButton;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set view
        setContentView(R.layout.activity_stats);

        Intent intent = getIntent();
        expID = intent.getStringExtra("EXPID");


        statButton = findViewById(R.id.statsButton);
        histogramButton = findViewById(R.id.histogramButton);
        plotButton = findViewById(R.id.plotButton);

        histogramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatsActivity.this, Histogram.class);
                intent.putExtra("EXPID",expID);
                startActivity(intent);
                finishAffinity();
            }
        });
        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatsActivity.this, Statistics.class);
                intent.putExtra("EXPID",expID);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(StatsActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
