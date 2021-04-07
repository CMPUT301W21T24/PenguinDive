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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    // list of binomial trials to use with database
    private ArrayList<Binomial_Trial> binomialTrialList = new ArrayList<Binomial_Trial>();
    // use a two-value array to store the last number of passes/fails
    ArrayList<Integer> passFailList = new ArrayList<Integer>();

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
        //TODO: setting the values of the numpasses/numfails at startup doesnt work
        // the list is out of bounds and idk what else to do, thinking of just making a random
        // document in the database that stores the values but thats hacky

        // set experiment name
        experimentNameView = findViewById(R.id.binomial_experiment_name);
        experimentNameView.setText(experimentName);

        // set onClickListeners
        // when this is clicked, run the class's add pass method
        addPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get number of passes from list
                numPasses.setText(String.valueOf(passFailList.get(0)));

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

                // get value from list
               numFails.setText(String.valueOf(passFailList.get(1)));

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

        // add the snapshot listener once to get real time updates
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            // whenever the database changes, reset and use the database values
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                // reset the values
                binomialTrialList.clear();
                passFailList.clear();
                passFailList.add(0);
                passFailList.add(0);

                // for each document in the snapshot
                for (QueryDocumentSnapshot document: queryDocumentSnapshots) {

                    // if the document in the collection is for this experiment
                    if (document.get("Experiment Name").equals(experimentName)) {

                        // get values for object through document
                        String binomialType = (String) document.get("Binomial Type");

                        // put the trials in the list depending on what type they are
                        if (binomialType.equals("Pass")) {
                            binomialTrialList.add(new Binomial_Trial(true));
                            int incrementP = passFailList.get(0) + 1;
                            passFailList.set(0, incrementP);
                        }
                        else if (binomialType.equals("Fail")) {
                            binomialTrialList.add(new Binomial_Trial(false));
                            int incrementF = passFailList.get(1) + 1;
                            passFailList.set(1, incrementF);
                        }
                        else {
                            System.out.println("Binomial Trial is not pass or fail!");
                        }

                    }
                }
            }
        });





    }



}
