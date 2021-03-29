package com.cmput301.penguindive;

/*
Searching was developed with help from:

 */


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an activity that will show all published experiments and act as the main activity to reach other features
 */
public class MainActivity extends AppCompatActivity implements ExperimentFragment.OnFragmentInteractionListener{

    private ListView experimentList;
    protected ArrayList<Experiment> experimentDataList;
    private ArrayAdapter<Experiment> experimentArrayAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");
    Button myExperiment;
    Button profileButton;
    SearchView searchBar;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view
        setContentView(R.layout.experiment_activity);

        // Initialize elements
        experimentList = findViewById(R.id.experimentList);
        myExperiment = findViewById(R.id.myExperimentButton);
        searchBar = findViewById(R.id.experimentSearchBar);
        profileButton = findViewById(R.id.profileButton);

        // Setup list and adapter
        experimentDataList = new ArrayList<Experiment>();
        experimentArrayAdapter = new ExperimentCustomList(this, experimentDataList);
        experimentList.setAdapter(experimentArrayAdapter);

        // Get UID
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        uid = sharedPref.getString("UID", "");


        experimentList.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Subscribe Conformation")
                    .setMessage("Do you want to be an experimenter of this experiment?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            experimentCollectionReference.document(experimentDataList.get(position).getExperimentId())
                                    .update("experimenterIDs", FieldValue.arrayUnion(uid));
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        // Populate list from firestore
        loadData(); // All results that are published
        checkSearchBar(); // Check search bar and populate list accordingly

        // Jump to MyExperimentActivity (User's Experiments list)
        myExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyExperimentActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        // Call fragment to add experiment
        final FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(view ->
                new ExperimentFragment().show(getSupportFragmentManager(), "ADD"));

        // on click listeners for qr generation and scanning
        Button QRGen = findViewById(R.id.qr_gen);
        Button QRScan = findViewById(R.id.qr_scan);

        QRGen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PickQRType.class);
            startActivity(intent);
        });

        QRScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PickScanType.class);
            startActivity(intent);
        });
    }

    //changes after clicking OK/Edit/Delete button
    @Override
    public void onOkPressed(Experiment newExperiment) {
        String experimentId = newExperiment.getExperimentId();
        Map<String, Object> docData = new HashMap<>();

        List<String> keywords = new ArrayList<String>();
        // For getting it to add multiple elements properly
        // https://stackoverflow.com/a/36560577
        // For whitespace and punctuation
        // https://stackoverflow.com/a/28257108
        keywords.add(newExperiment.getOwnerUserName().trim().toLowerCase()); // Full Username will need to be searched
        keywords.addAll(Arrays.asList(newExperiment.getTitle().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getDescription().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getRegion().toLowerCase().trim().split("\\W+")));

        docData.put("Status",newExperiment.getStatus());
        docData.put("ownerId",newExperiment.getOwnerUserName());
        docData.put("Region", newExperiment.getRegion());
        docData.put("Description", newExperiment.getDescription());
        docData.put("Title", newExperiment.getTitle());
        docData.put("TotalTrail", newExperiment.getTotalTrail());
        docData.put("experimenterIDs", newExperiment.getExperimenters());
        docData.put("Keywords", keywords);

        db.collection("Experiments").document(experimentId)
                .set(docData)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
    }

    @Override
    public void onEditPressed(Experiment newExperiment, int position) {
    }

    @Override
    public void onDeletePressed(Experiment experiment) {
        experimentCollectionReference.document(experiment.getExperimentId()).delete();
    }

    //showing message when there is any invalid input
    @Override
    public void extraStringError() {
        Toast.makeText(MainActivity.this,"The description exceeds the maximum limitation.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void nullValueError() {
        Toast.makeText(MainActivity.this,"The information of description and date of the experiment is required.", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will load all experiments that are published into experimentDataList
     */
    public void loadData(){
        experimentCollectionReference.addSnapshotListener((value, error) -> {
            experimentDataList.clear();
            for(QueryDocumentSnapshot doc: value) {
                String status = (String) doc.getData().get("Status"); // Use status to see if it should be visible
                if (status.equals("publish")) {
                    String expID = doc.getId();
                    String description = (String) doc.getData().get("Description");
                    String region = (String) doc.getData().get("Region");
                    String title = (String) doc.getData().get("Title");
                    String totalTrail = (String) doc.getData().get("TotalTrail");
                    String ownerId = (String) doc.getData().get("ownerId");
                    List<String> experimenters = (List<String>) doc.getData().get("experimentIDs");
                    experimentDataList.add(new Experiment(expID, title, description, region, totalTrail, ownerId, status, experimenters));
                }
            }
            experimentArrayAdapter.notifyDataSetChanged();
        });
    }

    /**
     * This method will modify the experimentDataList when text is entered into the searchBox
     * Only experiments that are published and match the query will be put into the experimentDataList
     */
    public void checkSearchBar(){
        // Searching, currently only scans title
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // If there is no input, reset the list
                if (query.length() == 0) {
                        loadData();
                }
                // If there is input, filter based on input
                // Make query an array to allow multiple words in a search

                // Idea to make keywords and use it for searching
                // https://stackoverflow.com/a/52715590
                // https://firebase.googleblog.com/2018/08/better-arrays-in-cloud-firestore.html
                else{
                    experimentDataList.clear(); // Prevent duplicates
                    experimentCollectionReference.whereArrayContainsAny("Keywords", Arrays.asList(query.trim().toLowerCase()))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // Scan results and add to list
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    String status = (String) doc.getData().get("Status");
                                    if (status.equals("publish")) {
                                        String expID = doc.getId();
                                        String description = (String) doc.getData().get("Description");
                                        String region = (String) doc.getData().get("Region");
                                        String title = (String) doc.getData().get("Title");
                                        String totalTrail = (String) doc.getData().get("TotalTrail");
                                        String ownerId = (String) doc.getData().get("ownerId");
                                        List<String> experimenters = (List<String>) doc.getData().get("experimentIDs");
                                        experimentDataList.add(new Experiment(expID, title, description, region, totalTrail, ownerId, status, experimenters));
                                    }
                                }
                                experimentArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                return false;
            }
        });
    } // End of checkSearchBar()

}