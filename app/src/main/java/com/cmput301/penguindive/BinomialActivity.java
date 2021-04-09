package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// This class is for the binomial activity, pretty much entirely refactored bc of database
public class BinomialActivity extends AppCompatActivity {

    // declare buttons
    private Button addPassButton;
    private Button addFailButton;
    private Button ignoreResultsButton;

    // declare textviews
    private TextView numPasses;
    private TextView numFails;

    // experiment name stuff
    private TextView experimentNameView;
    private String experimentName;
    private String experimentID;

    // used to initialize existing values in onCreate
    Integer intPasses;
    Integer intFails;

    // initialize db
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference trialsCollectionReference = db.collection("Trials");
    CollectionReference experimentCollectionReference = db.collection("Experiments");

    // add tag
    final String TAG = "Binomial Activity";

    // date
    Calendar curDate = Calendar.getInstance();
    String date = curDate.get(Calendar.DAY_OF_MONTH) + "-" + (curDate.get(Calendar.MONTH) + 1) + "-" +
            curDate.get(Calendar.YEAR) + " " + curDate.get(Calendar.HOUR) + ":" +
            curDate.get(Calendar.MINUTE) + ":" + curDate.get(Calendar.SECOND);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_binomial_activity);  // set view to be binomial activity view

        // get experiment name from intent
        Intent intent = getIntent();
        experimentName = intent.getStringExtra("Experiment Name");
        experimentID = intent.getStringExtra("Experiment ID");
        String uid = intent.getStringExtra("UID");
        String ownerId = intent.getStringExtra("Owner ID");



        // set buttons
        addPassButton = findViewById(R.id.binomial_pass_button);
        addFailButton = findViewById(R.id.binomial_fail_button);
        ignoreResultsButton = findViewById(R.id.binomial_ignore_results_button);

        // Hide ignore results button if not owner
        if(uid.equals(ownerId)){
            ignoreResultsButton.setVisibility(View.VISIBLE);
        }
        else{
            ignoreResultsButton.setVisibility(View.GONE);
        }

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
                    numPasses.setText(String.valueOf(intPasses));
                    numFails.setText(String.valueOf(intFails));

                    // for each document
                    for (QueryDocumentSnapshot trialDoc : Objects.requireNonNull(task.getResult())) {
                        // if the document is for the experiment
                        if (trialDoc.get("Experiment ID").equals(experimentID)) {
                            experimentCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot experimentDoc : Objects.requireNonNull(task.getResult())) {
                                        if (experimentDoc.getId().equals(experimentID)) {

                                            List<String> ignored = (List<String>) experimentDoc.getData().get("ignoredExperimenters");
                                            if (ignored == null) {
                                                ignored = new ArrayList<String>();
                                            }
                                            String uid = (String) trialDoc.getData().get("UID");

                                            // if the user is not ignored
                                            if(!ignored.contains(uid)){

                                                // get values for object through document
                                                String binomialType = (String) trialDoc.get("Binomial Type");

                                                // put the trials in the list depending on what type they are
                                                if (binomialType.equals("Pass")) {
                                                    intPasses++;
                                                } else if (binomialType.equals("Fail")) {
                                                    intFails++;
                                                } else {
                                                    System.out.println("Binomial Trial is not pass or fail!");
                                                }
                                                // set the text of textviews
                                                numPasses.setText(String.valueOf(intPasses));
                                                numFails.setText(String.valueOf(intFails));
                                            }
                                        }
                                    }

                                }
                            });
                        }
                        // set the text of textviews from other listener
                        numPasses.setText(String.valueOf(intPasses));
                        numFails.setText(String.valueOf(intFails));
                    }
                } else {
                    Log.d("Could not get data", "no data to get");
                }
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
                data.put("Experiment ID", experimentID);
                data.put("Date", date);
                data.put("UID", uid);

                data.put("Binomial Type", "Pass");

                // add to firestore
                trialsCollectionReference
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
                data.put("Experiment ID", experimentID);
                data.put("Date", date);
                data.put("UID", uid);

                data.put("Binomial Type", "Fail");

                // add to firestore
                trialsCollectionReference
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

        // when the ignore results button is clicked, redirect to activity
        ignoreResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BinomialActivity.this, CurrentExperimentersActivity.class);
                intent.putExtra("Experiment ID", experimentID);
                startActivity(intent);
            }
        });
    }
}
