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

/**
 * This class represents an activity that shows the given NNIC trials for an experiment
 */
public class NNICActivity extends AppCompatActivity implements NNICFragment.OnFragmentInteractionListener {

    // declare variables for adapting
    ListView NNICTrialList;
    ArrayList<Non_Negative_Integer_Counts_Trial> NNICTrialDataList;
    ArrayAdapter<Non_Negative_Integer_Counts_Trial> NNICArrayAdapter;

    // declare add Trial button
    private Button addNNICTrialButton;
    private Button ignoreResultsButton;

    // declare name variables
    private TextView experimentNameView;
    private String experimentName;
    private String experimentID;

    // initialize firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // get reference to Trials collection
    CollectionReference trialsCollectionReference = db.collection("Trials");
    CollectionReference experimentCollectionReference = db.collection("Experiments");

    // make tag
    final String TAG = "NNICActivity";

    // date
    Calendar curDate = Calendar.getInstance();
    String date = curDate.get(Calendar.DAY_OF_MONTH) + "-" + (curDate.get(Calendar.MONTH) + 1) + "-" +
            curDate.get(Calendar.YEAR) + " " + curDate.get(Calendar.HOUR) + ":" +
            curDate.get(Calendar.MINUTE) + ":" + curDate.get(Calendar.SECOND);

    // uid
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_nnic_activity);  // set the content view to NNICActivity

        // get intent/experiment name
        Intent intent = getIntent();
        experimentName = intent.getStringExtra("Experiment Name");
        experimentID = intent.getStringExtra("Experiment ID");
        experimentNameView = findViewById(R.id.nnic_experiment_name);
        experimentNameView.setText(experimentName);
        uid = intent.getStringExtra("UID");
        String ownerId = intent.getStringExtra("Owner ID");


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
        ignoreResultsButton = findViewById(R.id.nnic_ignore_results_button);

        // Hide ignore results button if not owner
        if(uid.equals(ownerId)){
            ignoreResultsButton.setVisibility(View.VISIBLE);
        }
        else{
            ignoreResultsButton.setVisibility(View.GONE);
        }

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


    /**
     * This method activates every time OK is pressed in the fragment, aka every time a new trial is made
     * Used to add the trial to the database
     */
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
            hashmap.put("Experiment ID", experimentID);
            hashmap.put("Date", date);
            hashmap.put("UID", uid);

            // add the values into the hashmap
            hashmap.put("Non-Negative Integer", nniString);
            hashmap.put("NNIC Name", nnicName);

            // add the hashmap to the collection
            trialsCollectionReference
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




    /**
     * This method adds the snapshot listener, making the app have real-time connection to the database.
     * It is used to update the data in the list, and is called every time the database changes, built for importing entire collection
     */
    public void FillDataFromDatabase() {

        // add the snapshot listener, this only needs to happen once
        trialsCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            // every time the database changes
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                // reset the list because this method is intended to reimport all documents every time, we rebuild from scratch
                NNICTrialDataList.clear();

                // done to every document in the collection at the current snapshot
                for (QueryDocumentSnapshot trialDoc: queryDocumentSnapshots) {

                    // if the document in the collection is for this experiment
                    if (String.valueOf((trialDoc.get("Experiment ID"))).equals(experimentID)) {
                        experimentCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot experimentDoc : Objects.requireNonNull(task.getResult())) {
                                    if (experimentDoc.getId().equals(experimentID)){

                                        List<String> ignored = (List<String>) experimentDoc.getData().get("ignoredExperimenters");
                                        if (ignored == null){
                                            ignored = new ArrayList<String>();
                                        }
                                        // if the user is not ignored add their trial
                                        String uid = (String) trialDoc.getData().get("UID");
                                        if(!ignored.contains(uid)){
                                            // put a trial made from the document's data in the dataList
                                            // no need to do anything else than cast because we added these in onOKPressed
                                            String NNICName = (String) trialDoc.get("NNIC Name");
                                            String NNIString = (String) trialDoc.get("Non-Negative Integer");
                                            Integer NNI = Integer.valueOf(NNIString);  // convert to int

                                            // make new trial to populate datalist
                                            NNICTrialDataList.add(new Non_Negative_Integer_Counts_Trial(NNI, NNICName));
                                        }
                                    }
                                }
                                // notify adapter that the data has changed
                                NNICArrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });

        // when the ignore results button is clicked, redirect to activity
        ignoreResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NNICActivity.this, CurrentExperimentersActivity.class);
                intent.putExtra("Experiment ID", experimentID);

                startActivity(intent);
            }
        });

    }


    /**
     * This method adds a NNIC trial to the interface
     * @param NNICTrial
     * Takes a NNIC Trial object
     */
    public void AddNNIC_Trial(Non_Negative_Integer_Counts_Trial NNICTrial) {
        NNICArrayAdapter.add(NNICTrial);
    }

}
