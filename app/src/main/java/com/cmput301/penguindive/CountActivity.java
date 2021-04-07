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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CountActivity extends AppCompatActivity {

    // initialize experiment name
    private TextView experimentNameView;

    // declare buttons
    private Button incrementCount;
    private Button decrementCount;

    // declare count
    private TextView count;

    // firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Trials");
    // tag
    final String TAG = "Count Activity";

    // initial values
    Integer sum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_count_activity);

        // set up intent to get experiment name
        Intent intent = getIntent();
        String experimentName = intent.getStringExtra("Experiment Name");

        // set buttons
        incrementCount = findViewById(R.id.count_add_button);
        decrementCount = findViewById(R.id.count_subtract_button);

        // set count view
        count = findViewById(R.id.count_number);  // already set to zero in the constructor

        // get the initial values for count with a new oncompletelistener
        // Made with help from Ryan Brooks
        db.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    sum = 0;

                    // for each document
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        // if the document is for the experiment
                        if (document.get("Experiment Name").equals(experimentName)) {
                            // get values for object through document
                            String countType = (String) document.get("Count Type");

                            // put the trials in the list depending on what type they are
                            if (countType.equals("TRUE")) {
                                sum++;
                            } else if (countType.equals("FALSE")) {
                                sum--;
                            } else {
                                System.out.println("Count Trial is not pass or fail!");
                            }
                        }
                    }
                } else {
                    Log.d("Could not get data", "no data to get");
                }

                // set the text of count
                count.setText(String.valueOf(sum));
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

                data.put("Count Type", "TRUE");

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

                data.put("Count Type", "FALSE");

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

