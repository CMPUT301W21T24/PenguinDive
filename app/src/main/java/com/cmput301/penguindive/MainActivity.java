package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ExperimentFragment.OnFragmentInteractionListener{

    private ListView experimentList;
//    private SearchView SearchContent;
    private ArrayList<Experiment> experimentDataList;
    private ArrayAdapter<Experiment> experimentArrayAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");
    Button myExperiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_activity);

        experimentList = findViewById(R.id.experimentList);
        myExperiment = findViewById(R.id.myExperimentButton);

        experimentDataList = new ArrayList<Experiment>();
        experimentArrayAdapter = new ExperimentCustomList(this, experimentDataList);
        experimentList.setAdapter(experimentArrayAdapter);

        final FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(view ->
                new ExperimentFragment().show(getSupportFragmentManager(), "ADD"));

        // create a clickListener on the existing experiment
        experimentList.setOnItemClickListener((parent, view, position, id) -> {
            Experiment trail = experimentDataList.get(position);
            ExperimentFragment.newInstance(trail, position).show(getSupportFragmentManager(), "Edit");
        });

        experimentCollectionReference.addSnapshotListener((value, error) -> {
            experimentDataList.clear();
            for(QueryDocumentSnapshot doc: value) {
                String expID = doc.getId();
                String description = (String) doc.getData().get("Description");
                String region = (String) doc.getData().get("Region");
                String status = (String) doc.getData().get("Status");
                String title = (String) doc.getData().get("Title");
                String totalTrail = (String) doc.getData().get("TotalTrail");
                String ownerId = (String) doc.getData().get("ownerId");
                experimentDataList.add(new Experiment(expID, title, description,region, totalTrail,ownerId, status));
            }
            experimentArrayAdapter.notifyDataSetChanged();
        });

        myExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, MyExperiment.class);
//                startActivity(intent);
            }
        });

    }

    //changes after clicking OK/Edit/Delete button
    @Override
    public void onOkPressed(Experiment newExperiment) {
        String experimentId = newExperiment.getExperimentId();
        Map<String, Object> docData = new HashMap<>();

        docData.put("Status",newExperiment.getStatus());
        docData.put("ownerId",newExperiment.getOwnerUserName());
        docData.put("Region", newExperiment.getRegion());
        docData.put("Description", newExperiment.getDescription());
        docData.put("Title", newExperiment.getTitle());
        docData.put("TotalTrail", newExperiment.getTotalTrail());

        db.collection("Experiments").document(experimentId)
                .set(docData)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

    }

    @Override
    public void onEditPressed(Experiment newExperiment, int position) {
        DocumentReference docRe = experimentCollectionReference.document(newExperiment.getExperimentId());
        docRe.update("Status", newExperiment.getStatus(),
                "ownerId", newExperiment.getOwnerUserName(),
                "Region", newExperiment.getRegion(),
                "Description", newExperiment.getDescription(),
                "Title", newExperiment.getTitle(),
                "TotalTrail", newExperiment.getTotalTrail());
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


//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        experimentAdapter.filter(newText);
//        return false;
//    }
}