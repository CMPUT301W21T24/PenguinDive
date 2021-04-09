package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

// This class is for the binomial activity, pretty much entirely refactored bc of database
public class BinomialActivity extends AppCompatActivity {

    // declare buttons
    private Button addPassButton;
    private Button addFailButton;

    // declare textviews
    private TextView numPasses;
    private TextView numFails;

    // experiment name stuff
    private TextView experimentNameView;
    private String experimentName;

    // used to initialize existing values in onCreate
    Integer intPasses;
    Integer intFails;

    // initialize db
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Trials");
    // add tag
    final String TAG = "Binomial Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_binomial_activity);  // set view to be binomial activity view

        // get experiment name from intent
        Intent intent = getIntent();
        experimentName = intent.getStringExtra("Experiment Name");

        // set buttons
        addPassButton = findViewById(R.id.binomial_pass_button);
        addFailButton = findViewById(R.id.binomial_fail_button);

        // set textviews
        numPasses = findViewById(R.id.binomial_numpasses);
        numFails = findViewById(R.id.binomial_numfails);

        // get the initial values for count with a new oncompletelistener
        // Made with help from Ryan Brooks
        db.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    // initialize values
                    intPasses = 0;
                    intFails = 0;

                    // for each document
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        // if the document is for the experiment
                        if (document.get("Experiment Name").equals(experimentName)) {
                            // get values for object through document
                            String binomialType = (String) document.get("Binomial Type");

                            // put the trials in the list depending on what type they are
                            if (binomialType.equals("Pass")) {
                                intPasses++;
                            } else if (binomialType.equals("Fail")) {
                                intFails++;
                            } else {
                                System.out.println("Binomial Trial is not pass or fail!");
                            }
                        }
                    }
                } else {
                    Log.d("Could not get data", "no data to get");
                }

                // set the text of textviews
                numPasses.setText(String.valueOf(intPasses));
                numFails.setText(String.valueOf(intFails));
            }
        });


        // set experiment name
        experimentNameView = findViewById(R.id.binomial_experiment_name);
        experimentNameView.setText(experimentName);

        // set onClickListeners
        // when this is clicked, run the class's add pass method
        addPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get number of passes
                Integer currentPasses = Integer.valueOf(numPasses.getText().toString());
                Integer newPasses = currentPasses + 1;
                numPasses.setText(String.valueOf(newPasses));

                // firestore stuff
                // make hashmap
                HashMap<String, String> data = new HashMap<>();

                // add one pass to the hashmap, along with the mandatory trial type and experiment name
                data.put("Trial Type", "Binomial Trial");
                data.put("Experiment Name", experimentName);

                data.put("Binomial Type", "Pass");

                // add to firestore
                collectionReference
                        // add and set id
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Data has been successfully added!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was an error adding data: " + e.toString());
                            }
                        });
            }
        });


        addFailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get number of fails
                Integer currentFails = Integer.valueOf(numFails.getText().toString());
                Integer newFails = currentFails + 1;
                numFails.setText(String.valueOf(newFails));


                // firestore stuff
                // make hashmap
                HashMap<String, String> data = new HashMap<>();

                // add one fail to the hashmap, along with the mandatory trial type and experiment name
                data.put("Trial Type", "Binomial Trial");
                data.put("Experiment Name", experimentName);

                data.put("Binomial Type", "Fail");

                // add to firestore
                collectionReference
                        // add and set id
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Data has been successfully added!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was an error adding data: " + e.toString());
                            }
                        });
            }
        });
    }
}
