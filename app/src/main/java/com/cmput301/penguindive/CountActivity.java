package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CountActivity extends AppCompatActivity {

    // initialize experiment name
    private TextView experimentNameView;

    // declare buttons
    private Button incrementCount;
    private Button decrementCount;
    private Button ignoreResultsButton;

    // declare count
    private TextView count;

    // firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference trialsCollectionReference = db.collection("Trials");
    CollectionReference experimentCollectionReference = db.collection("Experiments");
    // tag
    final String TAG = "Count Activity";

    // initial values
    Integer sum;

    // date
    Calendar curDate = Calendar.getInstance();
    String date = curDate.get(Calendar.DAY_OF_MONTH) + "-" + (curDate.get(Calendar.MONTH) + 1) + "-" +
            curDate.get(Calendar.YEAR) + " " + curDate.get(Calendar.HOUR) + ":" +
            curDate.get(Calendar.MINUTE) + ":" + curDate.get(Calendar.SECOND);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_count_activity);

        // set up intent to get experiment name
        Intent intent = getIntent();
        String experimentName = intent.getStringExtra("Experiment Name");
        String experimentID = intent.getStringExtra("Experiment ID");
        String uid = intent.getStringExtra("UID");
        String ownerId = intent.getStringExtra("Owner ID");

        // set buttons
        incrementCount = findViewById(R.id.count_add_button);
        decrementCount = findViewById(R.id.count_subtract_button);
        ignoreResultsButton = findViewById(R.id.count_ignore_results_button);

        // Hide ignore results button if not owner
        if(uid.equals(ownerId)){
            ignoreResultsButton.setVisibility(View.VISIBLE);
        }
        else{
            ignoreResultsButton.setVisibility(View.GONE);
        }

        // set count view
        count = findViewById(R.id.count_number);  // already set to zero in the constructor

        // get the initial values for count with a new oncompletelistener
        // Made with help from Ryan Brooks
        db.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    sum = 0;
                    count.setText(String.valueOf(sum));

                    // for each document
                    for (QueryDocumentSnapshot trialDoc : Objects.requireNonNull(task.getResult())) {
                        // if the document is for the experiment
                        if (trialDoc.get("Experiment ID").equals(experimentID)) {
                            experimentCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot experimentDoc : Objects.requireNonNull(task.getResult())) {

                                        if (experimentDoc.getId().equals(experimentID)){

                                            List<String> ignored = (List<String>) experimentDoc.getData().get("ignoredExperimenters");
                                            if (ignored == null){
                                                ignored = new ArrayList<String>();
                                            }

                                            String uid = (String) trialDoc.getData().get("UID");

                                            // if the user is not ignored add their trial
                                            if(!ignored.contains(uid)){
                                                // get values for object through document
                                                String countType = (String) trialDoc.get("Count Type");

                                                // put the trials in the list depending on what type they are
                                                if (countType.equals("TRUE")) {
                                                    sum++;
                                                } else if (countType.equals("FALSE")) {
                                                    sum--;
                                                } else {
                                                    System.out.println("Count Trial is not pass or fail!");
                                                }
                                                // set the text of count
                                                count.setText(String.valueOf(sum));
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        // set the text of count from other listener
                        count.setText(String.valueOf(sum));
                    }
                } else {
                    Log.d("Could not get data", "no data to get");
                }
            }
        });

        // set experiment name
        experimentNameView = findViewById(R.id.count_experiment_name);
        experimentNameView.setText(experimentName);

        // set onclicklisteners
        incrementCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get text
                Integer currentCount = Integer.valueOf(count.getText().toString());
                Integer newCount = currentCount + 1;
                // set text
                count.setText(String.valueOf(newCount));


                // firestore stuff
                // make hashmap
                HashMap<String, String> data = new HashMap<>();

                // add one pass to the hashmap, along with the mandatory trial type and experiment name
                data.put("Trial Type", "Count Trial");
                data.put("Experiment Name", experimentName);
                data.put("Experiment ID", experimentID);
                data.put("Date", date);
                data.put("UID", uid);

                data.put("Count Type", "TRUE");

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


        decrementCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get text
                Integer currentCount = Integer.valueOf(count.getText().toString());
                Integer newCount = currentCount - 1;
                // set text
                count.setText(String.valueOf(newCount));


                // firestore stuff
                // make hashmap
                HashMap<String, String> data = new HashMap<>();

                // add one fail to the hashmap, along with the mandatory trial type and experiment name
                data.put("Trial Type", "Count Trial");
                data.put("Experiment Name", experimentName);
                data.put("Experiment ID", experimentID);
                data.put("Date", date);
                data.put("UID", uid);

                data.put("Count Type", "FALSE");

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
                Intent intent = new Intent(CountActivity.this, CurrentExperimentersActivity.class);
                intent.putExtra("Experiment ID", experimentID);
                startActivity(intent);
            }
        });

    }
}

