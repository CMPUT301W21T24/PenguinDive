package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;

public class NNICActivity extends AppCompatActivity implements NNICFragment.OnFragmentInteractionListener {

    // declare variables for adapting
    ListView NNICTrialList;
    ArrayList<Non_Negative_Integer_Counts_Trial> NNICTrialDataList;
    ArrayAdapter<Non_Negative_Integer_Counts_Trial> NNICArrayAdapter;

    // declare add Trial button
    private Button addNNICTrialButton;

    // declare name variables
    private TextView experimentNameView;
    private String experimentName;

    // initialize firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // get reference to Trials collection
    CollectionReference collectionReference = db.collection("Trials");

    // make tag
    final String TAG = "NNICActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_nnic_activity);  // set the content view to NNICActivity

        // get intent/experiment name
        Intent intent = getIntent();
        experimentName = intent.getStringExtra("Experiment Name");
        experimentNameView = findViewById(R.id.nnic_experiment_name);
        experimentNameView.setText(experimentName);

        // set the trial list to the view
        NNICTrialList = findViewById(R.id.nnic_trial_list);
        // data list is new arraylist
        NNICTrialDataList = new ArrayList<>();

        // set the adapter to be the NNICCustomlist adapter, with dataList as the data
        NNICArrayAdapter = new NNICCustomList(this, NNICTrialDataList);

        // set NNICTrialList's (the view) adapter to NNICArrayAdapter, letting the data be used for the view
        NNICTrialList.setAdapter(NNICArrayAdapter);

        // fragment stuff
        // initialize button
        addNNICTrialButton = findViewById(R.id.nnic_add_trial_button);

        // set the listener for the button
        addNNICTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make a new instance of the fragment and show it with a fragment manager/tag
                new NNICFragment().show(getSupportFragmentManager(), "Add NNIC");
            }
        });

        // fill the list with any existing data from the database
        // only needs to be run once here because the listener gets attached and its real time afterwards
        FillDataFromDatabase();

    }


    // This method activates every time OK is pressed in the fragment, aka every time a new trial is made
    // Used to add the trial to the database
    @Override
    public void onOKPressed(Non_Negative_Integer_Counts_Trial nnicTrial) {

        // initialize variables to be put in
        String nniString = String.valueOf(nnicTrial.getNonNegativeInteger());  // convert to string
        String nnicName = nnicTrial.getNNICName();

        // check that values were entered in the fragment, and if they were proceed
        if ((nniString.length() > 0) && (nnicName.length() > 0)) {


            // make the hashmap
            HashMap<String, String> hashmap = new HashMap<>();

            // add mandatory Trial Type and Experiment Name into hashmap
            hashmap.put("Trial Type", "NNIC Trial");  // not really used but good to have for qr codes/in general
            hashmap.put("Experiment Name", experimentName);

            // add the values into the hashmap
            hashmap.put("Non-Negative Integer", nniString);
            hashmap.put("NNIC Name", nnicName);

            // add the hashmap to the collection
            collectionReference
                    .add(hashmap)  // adds data and gives it a unique id
                    // on success
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Data was added successfully.");
                        }
                    })
                    // on failure
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "An error occurred while adding the data: " + e.toString());
                        }
                    });

        }
    }


    // This method adds the snapshot listener, making the app have real-time connection to the database.
    // It is used to update the data in the list, and is called every time the database changes, built for importing entire collection
    public void FillDataFromDatabase() {

        // add the snapshot listener, this only needs to happen once
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            // every time the database changes
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                // reset the list because this method is intended to reimport all documents every time, we rebuild from scratch
                NNICTrialDataList.clear();

                // done to every document in the collection at the current snapshot
                for (QueryDocumentSnapshot document: queryDocumentSnapshots) {

                    // if the document in the collection is for this experiment
                    if (String.valueOf((document.get("Experiment Name"))).equals(experimentName)) {

                        // put a trial made from the document's data in the dataList
                        // no need to do anything else than cast because we added these in onOKPressed
                        String NNICName = (String) document.get("NNIC Name");
                        String NNIString = (String) document.get("Non-Negative Integer");
                        Integer NNI = Integer.valueOf(NNIString);  // convert to int

                        // make new trial to populate datalist
                        NNICTrialDataList.add(new Non_Negative_Integer_Counts_Trial(NNI, NNICName));

                    }
                }

                // notify adapter that the data has changed
                NNICArrayAdapter.notifyDataSetChanged();
            }
        });

    }


    // implement adding trial method required by interface
    public void AddNNIC_Trial(Non_Negative_Integer_Counts_Trial NNICTrial) {
        NNICArrayAdapter.add(NNICTrial);
    }

}
