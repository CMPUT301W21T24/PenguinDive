package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents an activity that will show all non-ignored experimenters for a given trial
 * The experiment owner can then choose to ignore experimenters to hide their results from the experiment
 */
public class CurrentExperimentersActivity extends AppCompatActivity {
    ListView experimentersList;
    private ArrayAdapter<String> experimentersAdapter;
    private ArrayList<String> experimentersDataList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.current_experimenters);
        experimentersList = findViewById(R.id.current_experimenters_list);

        // Get intent
        Intent intent = getIntent();
        String experimentId = intent.getStringExtra("Experiment ID");

        // Set up list and adapter
        experimentersDataList = new ArrayList<>();
        experimentersAdapter = new ArrayAdapter<>(this, R.layout.current_experimenters_layout, experimentersDataList);
        experimentersList.setAdapter(experimentersAdapter);


        // Get list of all experimenters (exclude ignored)
        experimentCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                    if(doc.getId().equals(experimentId)){

                        // Get lists to compare
                        ArrayList<String> experimenters = (ArrayList<String>) doc.getData().get("experimenterIDs");
                        ArrayList<String> ignoredExperimenters = (ArrayList<String>) doc.getData().get("ignoredExperimenters");

                        // Ensure non-nulls
                        if (experimenters == null){
                            experimenters = new ArrayList<String>();
                        }
                        if (ignoredExperimenters == null){
                            ignoredExperimenters = new ArrayList<String>();
                            experimentCollectionReference.document(experimentId).update("ignoredExperimenters", ignoredExperimenters);
                        }

                        // Generate list of current experimenters
                        for(String id : experimenters){
                            // Do not display user's that have been ignored
                            if(!ignoredExperimenters.contains(id)){
                                experimentersDataList.add(id);
                            }
                        }

                    }
                }
                experimentersAdapter.notifyDataSetChanged();
            }
        });

        // If an experimenter ID is clicked, add them to the ignore list
        experimentersList.setOnItemClickListener((parent, view, position, id) -> {
            String experimenter = experimentersDataList.get(position);
            experimentCollectionReference.get().addOnCompleteListener(task -> {
                for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                    if(doc.getId().equals(experimentId)){
                        // Add them to the ignore list
                        experimentCollectionReference.document(experimentId).update("ignoredExperimenters", FieldValue.arrayUnion(experimenter));
                        experimentersDataList.remove(position);
                    }
                }
                experimentersAdapter.notifyDataSetChanged();
            });
            Toast.makeText(getApplicationContext(),"This user's trials will now be ignored.",Toast.LENGTH_LONG).show();
            finishActivity(0);
        });

    }
}
