package com.cmput301.penguindive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents an activity that will show all the user's experiments
 */
public class MyExperimentActivity extends AppCompatActivity implements ExperimentFragment.OnFragmentInteractionListener {

    private ListView experimentList;
    protected ArrayList<Experiment> experimentDataList;
    private ArrayAdapter<Experiment> experimentArrayAdapter;
    private SearchView searchBar;
    DrawerLayout drawerLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");
    CollectionReference profileCollectionReference = db.collection("Experimenter");
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view
        setContentView(R.layout.my_experiment_activity);

        // Get UID
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        uid = sharedPref.getString("UID", "");

        // Initialize elements
        experimentList = findViewById(R.id.experimentList);
        searchBar = findViewById(R.id.experimentSearchBar);

        // Setup list and adapter
        experimentDataList = new ArrayList<>();
        experimentArrayAdapter = new ExperimentCustomList(this, experimentDataList, uid);
        experimentList.setAdapter(experimentArrayAdapter);

        // create a clickListener on the existing experiment
        experimentList.setOnItemClickListener((parent, view, position, id) -> {
            Experiment trial = experimentDataList.get(position);

            ExperimentFragment.newInstance(trial, position).show(getSupportFragmentManager(), "Edit");
        });

        // Assign drawer
        drawerLayout = findViewById(R.id.my_experiment_activity);

        // Populate list from firestore
        loadData(); // All results related to userID
        checkSearchBar(); // Check search bar and populate list accordingly

        // Call fragment to add experiment
        final FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(view ->
                new ExperimentFragment().show(getSupportFragmentManager(), "ADD"));

    }

    //changes after clicking OK/Edit/Delete button
    @Override
    public void onOkPressed(Experiment newExperiment) {
        String experimentId = newExperiment.getExperimentId();
        String ownerId = newExperiment.getOwnerId();
        Map<String, Object> docData = new HashMap<>();


        // Query the ownerId in Profiles collection
        DocumentReference docRef = profileCollectionReference.document(ownerId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    // Get the given name
                    String ownerName = document.getString("name");

                    // Check to see if there is a name available and that it is different
                    if (ownerName != null && !ownerName.equals(newExperiment.getOwnerUserName())) {
                        newExperiment.setOwnerUserName(ownerName);
                    }//Otherwise keep the ownerId assigned from the fragment

                    // Get all the keywords from the experiment
                    List<String> keywords = getKeywords(newExperiment);
                    List<GeoPoint> locations = new ArrayList<>();

                    docData.put("Status",newExperiment.getStatus());
                    docData.put("ownerId",ownerId);
                    docData.put("ownerName", newExperiment.getOwnerUserName());
                    docData.put("Region", newExperiment.getRegion());
                    docData.put("Description", newExperiment.getDescription());
                    docData.put("Title", newExperiment.getTitle());
                    docData.put("MinimumTrials", newExperiment.getMinTrials());
                    docData.put("experimenterIDs", newExperiment.getExperimenters());
                    docData.put("TrialType", newExperiment.getTrialType());
                    docData.put("Keywords", keywords);
                    docData.put("LocationStatus", newExperiment.getLocationState());
                    docData.put("Locations", locations);

                    db.collection("Experiments").document(experimentId)
                            .set(docData)
                            .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

                }
            }
        });

    }

    @Override
    public void onEditPressed(Experiment newExperiment, int position) {
        DocumentReference docRe = experimentCollectionReference.document(newExperiment.getExperimentId());
        docRe.update("Status", newExperiment.getStatus(),
                "ownerId", newExperiment.getOwnerId(),
                "Region", newExperiment.getRegion(),
                "Description", newExperiment.getDescription(),
                "Title", newExperiment.getTitle(),
                "LocationStatus", newExperiment.getLocationState(),
                "MinimumTrials", newExperiment.getMinTrials(),
                "TrialType", newExperiment.getTrialType(),
                "Keywords", getKeywords(newExperiment));
    }

    @Override
    public void onDeletePressed(Experiment experiment) {
        String experimendId = experiment.getExperimentId();
        experimentCollectionReference.document(experimendId).delete();

        // Delete all relevant trials
        db.collection("Trials").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                    if (Objects.equals(doc.getData().get("Experiment ID"), experimendId)){
                        String trialsId = doc.getId();
                        db.collection("Trials").document(trialsId).delete();
                    }
                }
            }
        });

        // Delete all relevant questions and answers
        db.collection("Questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                    if (Objects.equals(doc.getData().get("experiment_id"), experimendId)){
                        String questionId = doc.getId();
                        db.collection("Answers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                                    if (Objects.equals(doc.getData().get("question_id"), questionId)){
                                        String trialsId = doc.getId();
                                        db.collection("Answers").document(trialsId).delete();
                                    }
                                }
                            }
                        });
                        db.collection("Questions").document(questionId).delete();
                    }
                }
            }
        });
    }

    //showing message when there is any invalid input
    @Override
    public void extraStringError() {
        Toast.makeText(this,"The description exceeds the maximum limitation.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void nullValueError() {
        Toast.makeText(this,"The information of description and date of the experiment is required.", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will load all experiments that the current user owns into experimentDataList
     */
    public void loadData(){

        experimentCollectionReference.addSnapshotListener((value, error) -> {
            experimentDataList.clear();
            for(QueryDocumentSnapshot doc: Objects.requireNonNull(value)) {
                String ownerId = (String) doc.getData().get("ownerId"); // Use userID and ownerID to see if it belongs to the current user
                if (ownerId.equals(uid)) {
                    String expID = doc.getId();
                    String description = (String) doc.getData().get("Description");
                    String region = (String) doc.getData().get("Region");
                    String status = (String) doc.getData().get("Status");
                    String ownerName = (String) doc.getData().get("ownerName");
                    String title = (String) doc.getData().get("Title");
                    Integer minTrials = Math.toIntExact((Long) doc.getData().get("MinimumTrials"));
                    List<String> experimenters = (List<String>) doc.getData().get("experimenterIDs");
                    Boolean locationStatus = (Boolean) doc.getData().get("LocationStatus");
                    String trialType = (String) doc.getData().get("TrialType");
                    experimentDataList.add(new Experiment(expID, title, description, region, minTrials, ownerId, ownerName, status, experimenters, locationStatus, trialType));
                }
            }
            experimentArrayAdapter.notifyDataSetChanged();
        });
    }

    /**
     * This method will modify the experimentDataList when text is entered into the searchBox
     * Only experiments that are owned by the user and match the query will be put into the experimentDataList
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
                else{

                    experimentDataList.clear(); // Clear before writing

                    // Turn query into proper list
                    List<String> queryList = new ArrayList<>(Arrays.asList(query.trim().toLowerCase().split("\\W+")));

                    // Do not query if only a non-char has been entered
                    if (!queryList.isEmpty()) {
                        // Query the database
                        experimentCollectionReference.whereArrayContainsAny("Keywords", queryList)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        // Scan query results and add to list
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            String ownerId = (String) doc.getData().get("ownerId");
                                            if (ownerId.equals(uid)) {
                                                String expID = doc.getId();
                                                String description = (String) doc.getData().get("Description");
                                                String region = (String) doc.getData().get("Region");
                                                String title = (String) doc.getData().get("Title");

                                                // For casting long to int, https://stackoverflow.com/a/32869210
                                                Integer minTrials = Math.toIntExact((Long) doc.getData().get("MinimumTrials"));
                                                String ownerName = (String) doc.getData().get("ownerName");
                                                String status = (String) doc.getData().get("Status");
                                                List<String> experimenters = (List<String>) doc.getData().get("experimenterIDs");
                                                String trialType = (String) doc.getData().get("TrialType");
                                                Boolean locationStatus = (Boolean) doc.getData().get("LocationStatus");

                                                // Check to see if it has been added before
                                                // Prevent duplicates even though firestore doc says ArrayContainsAny will de-dupe
                                                boolean isAdded = false;
                                                for (Experiment experiment : experimentDataList) {
                                                    if (experiment.getExperimentId().equals(expID)) {
                                                        isAdded = true;
                                                        break;
                                                    }
                                                }
                                                if (!isAdded) {
                                                    Experiment newExperiment = new Experiment(expID, title, description, region, minTrials, ownerId, ownerName, status, experimenters, locationStatus, trialType);
                                                    experimentDataList.add(newExperiment);
                                                }
                                            }
                                        }
                                        experimentArrayAdapter.notifyDataSetChanged();

                                    }
                                });
                    }
                }
                return false;
            }
        });
    } // End of checkSearchBar()

    /**
     * This method gets all searchable keywords from an experiment.
     * This includes it's title, description, ownerId, ownerUserName, status and region
     * @param newExperiment
     * An experiment object to reference when required values
     * @return
     * A list of strings containing all keywords gathered within the experiment
     */
    public List<String> getKeywords(Experiment newExperiment){
        List<String> keywords = new ArrayList<>();
        // For getting it to add multiple elements properly
        // https://stackoverflow.com/a/36560577
        // For whitespace and punctuation
        // https://stackoverflow.com/a/28257108
        keywords.add(newExperiment.getOwnerId().trim().toLowerCase()); // Full UserId will need to be searched
        keywords.add(newExperiment.getOwnerUserName().trim().toLowerCase()); // Full Username will need to be searched
        keywords.addAll(Arrays.asList(newExperiment.getTitle().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getDescription().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getRegion().toLowerCase().trim().split("\\W+")));
        keywords.add(newExperiment.getStatus().trim().toLowerCase()); // Full UserId will need to be searched
        keywords.add(newExperiment.getExperimentId().trim().toLowerCase()); // Full experiment Id will need to be searched
        keywords.add(newExperiment.getMinTrials().toString());

        return keywords;
    }

    /**
     * This method refreshes the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, MyExperimentActivity.class);
    }

    /**
     * This method gives the current drawer layout to the openDrawer method in MainActivity
     * It is called when the hamburger icon is clicked on the toolbar
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMenu(View view){ MainActivity.openDrawer(drawerLayout);}

    /**
     * This method gives the current drawer layout to the closeDrawer method in MainActivity
     * It is called when the PenguinDive logo is clicked in the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickLogo(View view){ MainActivity.closeDrawer(drawerLayout);}

    /**
     * This method redirects the user to MainActivity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickHome(View view){MainActivity.redirectActivity(this,MainActivity.class); }

    /**
     * This method redirects the user to the MyExperimentActivity
     * Since the user is already on MyExperimentActivity, it simply closes the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMyExperiments(View view){ MainActivity.closeDrawer(drawerLayout); }

    /**
     * This method redirects the user to the PickScanType Activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickScanQrCode(View view){ MainActivity.redirectActivity(this,PickScanType.class); }

    /**
     * This method redirects the user to the PickQRType Activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGenerateQrCode(View view){ MainActivity.redirectActivity(this,PickQRType.class); }

    /**
     * This method redirects the user to the their profile page (Profile Activity)
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMyProfile(View view){
        MainActivity.redirectActivity(this,Profile.class);
    }

    /**
     * This method redirects the user to the search users page (SearchProfile Activity)
     * @param view
     * Takes a view representing the current view
     */
    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

    /**
     * This method calls openGitHub in MainActivity and provides it the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGitHub(View view){
        MainActivity.openGitHub(this);
    }
}
