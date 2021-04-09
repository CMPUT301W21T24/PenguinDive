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

public class MeasurementActivity extends AppCompatActivity implements MeasurementFragment.OnFragmentInteractionListener {

    // declare variables for adapting
    ListView MeasurementTrialList;
    ArrayList<Measurement_Trial> MeasurementTrialDataList;
    ArrayAdapter<Measurement_Trial> MeasurementArrayAdapter;

    // declare add Trial button
    private Button addMeasurementTrialButton;
    private Button ignoreResultsButton;

    // initialize name
    TextView experimentNameView;
    String experimentName;
    String experimentID;
    String uid;

    // initialize firestore stuff
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference trialsCollectionReference = db.collection("Trials");
    CollectionReference experimentCollectionReference = db.collection("Experiments");

    final String TAG = "Measurement Activity";

    // date
    Calendar curDate = Calendar.getInstance();
    String date = curDate.get(Calendar.DAY_OF_MONTH) + "-" + (curDate.get(Calendar.MONTH) + 1) + "-" +
            curDate.get(Calendar.YEAR) + " " + curDate.get(Calendar.HOUR) + ":" +
            curDate.get(Calendar.MINUTE) + ":" + curDate.get(Calendar.SECOND);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_measurement_activity);  // set the content view to NNICActivity

        // get the experiment name
        Intent intent = getIntent();
        experimentName = intent.getStringExtra("Experiment Name");
        experimentID = intent.getStringExtra("Experiment ID");
        experimentNameView = findViewById(R.id.measurement_experiment_name);
        experimentNameView.setText(experimentName);
        uid = intent.getStringExtra("UID");
        String ownerId = intent.getStringExtra("Owner ID");


        // set the trial list to the view
        MeasurementTrialList = findViewById(R.id.measurement_trial_list);
        // data list is new arraylist
        MeasurementTrialDataList = new ArrayList<>();

        // set the adapter to be the MeasurementCustomlist adapter, with dataList as the data
        MeasurementArrayAdapter = new MeasurementCustomList(this, MeasurementTrialDataList);

        // set MeasurementTrialList's (the view) adapter to MeasurementArrayAdapter, letting the data be used for the view
        MeasurementTrialList.setAdapter(MeasurementArrayAdapter);

        // fragment stuff
        // initialize button
        addMeasurementTrialButton = findViewById(R.id.measurement_add_trial_button);
        ignoreResultsButton = findViewById(R.id.measurement_ignore_results_button);

        // Hide ignore results button if not owner
        if(uid.equals(ownerId)){
            ignoreResultsButton.setVisibility(View.VISIBLE);
        }
        else{
            ignoreResultsButton.setVisibility(View.GONE);
        }

        // set the listener for the button
        addMeasurementTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make a new instance of the fragment and show it with a fragment manager/tag
                new MeasurementFragment().show(getSupportFragmentManager(), "Add Measurement");
            }
        });

        // run FillDataFromDatabase once because a listener attaches to use later
        FillDataFromDatabase();

    }

    // onOKPressed used to add new trial to database
    // This method activates every time OK is pressed in the fragment, aka every time a new trial is made
    // Used to add the trial to the database
    @Override
    public void onOKPressed(Measurement_Trial measurementTrial) {

        // initialize variables to be put in
        String measurementString = String.valueOf(measurementTrial.getMeasurement());  // convert to string
        String measurementName = measurementTrial.getMeasurementName();

        // check that values were entered in the fragment, and if they were proceed
        if ((measurementString.length() > 0) && (measurementName.length() > 0)) {


            // make the hashmap
            HashMap<String, String> hashmap = new HashMap<>();

            // add mandatory Trial Type and Experiment Name into hashmap
            hashmap.put("Trial Type", "Measurement Trial");  // not really used but good to have for qr codes/in general
            hashmap.put("Experiment Name", experimentName);
            hashmap.put("Experiment ID", experimentID);
            hashmap.put("Date", date);
            hashmap.put("UID", uid);

            // add the values into the hashmap
            hashmap.put("Measurement", measurementString);
            hashmap.put("Measurement Name", measurementName);

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


    // This method adds the snapshot listener, making the app have real-time connection to the database.
    // It is used to update the data in the list, and is called every time the database changes, built for importing entire collection
    public void FillDataFromDatabase() {

        // add the snapshot listener, this only needs to happen once
        trialsCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            // every time the database changes
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                // reset the list because this method is intended to reimport all documents every time, we rebuild from scratch
                MeasurementTrialDataList.clear();

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
                                            String measurementName = (String) trialDoc.get("Measurement Name");
                                            String measurementString = (String) trialDoc.get("Measurement");
                                            Double measurement = Double.valueOf(measurementString);  // convert to double

                                            // make new trial to populate datalist
                                            MeasurementTrialDataList.add(new Measurement_Trial(measurement, measurementName));
                                        }
                                    }
                                }
                                // notify adapter that the data has changed
                                MeasurementArrayAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(MeasurementActivity.this, CurrentExperimentersActivity.class);
                intent.putExtra("Experiment ID", experimentID);

                startActivity(intent);
            }
        });

    }

    // implement adding trial method required by interface
    @Override
    public void AddMeasurement_Trial(Measurement_Trial measurement_trial) {
        MeasurementArrayAdapter.add(measurement_trial);
    }
}
