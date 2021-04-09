package com.cmput301.penguindive;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents an activity that will show all published experiments and act as the main activity to reach other features
 */
public class MainActivity extends AppCompatActivity implements ExperimentFragment.OnFragmentInteractionListener{

    protected ArrayList<Experiment> experimentDataList;
    private ArrayAdapter<Experiment> experimentArrayAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");
    SearchView searchBar;
    String uid;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view
        setContentView(R.layout.experiment_activity);

        // Get UID
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        uid = sharedPref.getString("UID", "");

        // Initialize elements
        ListView experimentList = findViewById(R.id.experimentList);
        searchBar = findViewById(R.id.experimentSearchBar);

        // Setup list and adapter
        experimentDataList = new ArrayList<>();
        experimentArrayAdapter = new ExperimentCustomList(this, experimentDataList, uid);
        experimentList.setAdapter(experimentArrayAdapter);

        // Assign drawer
        drawerLayout = findViewById(R.id.experiment_activity);

        experimentList.setOnItemClickListener((parent, view, position, id) -> {
            // Get experiment in question
            Experiment currentExperiment = experimentDataList.get(position);

            // Get fields
            Boolean locationState = currentExperiment.getLocationState();
            String status = currentExperiment.getStatus();
            List<String> experimenters = currentExperiment.getExperimenters();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // If the user hasn't subscribed to a publish location enabled experiment
            if (locationState && status.equals("Published") && !experimenters.contains(uid)){
                builder.setTitle("Subscribe Confirmation")
                        .setMessage("Do you want to be an experimenter of this experiment? This experiment is a located experiment")
                        .setPositiveButton("OK", (dialog, which) -> {
                            experimentCollectionReference.document(experimentDataList.get(position).getExperimentId())
                                    .update("experimenterIDs", FieldValue.arrayUnion(uid));
                            dialog.cancel();
                            Toast.makeText(MainActivity.this,"You have subscribed to the experiment.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
            // If the user hasn't subscribed to a published experiment
            else if (status.equals("Published") && !experimenters.contains(uid)) {
                builder.setTitle("Subscribe Confirmation")
                        .setMessage("Do you want to be an experimenter of this experiment?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            experimentCollectionReference.document(experimentDataList.get(position).getExperimentId())
                                    .update("experimenterIDs", FieldValue.arrayUnion(uid));
                            dialog.cancel();
                            Toast.makeText(MainActivity.this,"You have subscribed to the experiment.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
            // Already subscribed, prompt to unsubscribe
            else{
                builder.setTitle("Unsubscribe Confirmation")
                        .setMessage("Do you want to unsubscribe from this experiment?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            experimentCollectionReference.document(experimentDataList.get(position).getExperimentId())
                                    .update("experimenterIDs", FieldValue.arrayRemove(uid));
                            dialog.cancel();
                            Toast.makeText(MainActivity.this,"You have subscribed to the experiment.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        });

        // Populate list from firestore
        loadData(); // All results that are published
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
        List<GeoPoint> locations = new ArrayList<>();

        // Get all the keywords from the experiment
        List<String> keywords = getKeywords(newExperiment);

        docData.put("Status",newExperiment.getStatus());
        docData.put("ownerId",ownerId);
        docData.put("ownerName", newExperiment.getOwnerUserName());
        docData.put("Region", newExperiment.getRegion());
        docData.put("Description", newExperiment.getDescription());
        docData.put("Title", newExperiment.getTitle());
        docData.put("MinimumTrials", newExperiment.getMinTrials());
        docData.put("experimenterIDs", newExperiment.getExperimenters());
        docData.put("Keywords", keywords);
        docData.put("LocationStatus", newExperiment.getLocationState());
        docData.put("TrialType", newExperiment.getTrialType());
        docData.put("Locations", locations);

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
        Toast.makeText(MainActivity.this,"The description or title exceeds the maximum limitation, please try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void nullValueError() {
        Toast.makeText(MainActivity.this,"A field has been left empty, please try again.", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will load all experiments that are published into experimentDataList
     */
    public void loadData(){
        experimentCollectionReference.addSnapshotListener((value, error) -> {
            experimentDataList.clear();
            for(QueryDocumentSnapshot doc: Objects.requireNonNull(value)) {
                String status = (String) doc.getData().get("Status"); // Use status to see if it should be visible
                if (status.equals("Published")||status.equals("Ended")) {
                    String expID = doc.getId();
                    String description = (String) doc.getData().get("Description");
                    String region = (String) doc.getData().get("Region");
                    String title = (String) doc.getData().get("Title");
                    Integer minTrials = Math.toIntExact((Long) doc.getData().get("MinimumTrials"));
                    String ownerId = (String) doc.getData().get("ownerId");
                    String ownerName = (String) doc.getData().get("ownerName");
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
                else {
                    experimentDataList.clear(); // Prevent duplicates

                    // Turn query into proper list
                    List<String> queryList = new ArrayList<>(Arrays.asList(query.trim().toLowerCase().split("\\W+")));

                    // Do not query if only a non-char has been entered
                    if (!queryList.isEmpty()) {
                        experimentCollectionReference.whereArrayContainsAny("Keywords", queryList)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        // Scan results and add to list
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            String status = (String) doc.getData().get("Status");
                                            if (status.equals("Published") || status.equals("Ended")) {
                                                String expID = doc.getId();
                                                String description = (String) doc.getData().get("Description");
                                                String region = (String) doc.getData().get("Region");
                                                String title = (String) doc.getData().get("Title");

                                                // For casting long to int, https://stackoverflow.com/a/32869210
                                                Integer minTrials = Math.toIntExact((Long) doc.getData().get("MinimumTrials"));
                                                String ownerId = (String) doc.getData().get("ownerId");
                                                String ownerName = (String) doc.getData().get("ownerName");
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
        List<String> keywords;
        keywords = new ArrayList<>();
        // For getting it to add multiple elements properly
        // https://stackoverflow.com/a/36560577
        // For whitespace and punctuation
        // https://stackoverflow.com/a/28257108
        keywords.add(newExperiment.getOwnerId().trim().toLowerCase()); // Full UserId will need to be searched
        keywords.add(newExperiment.getOwnerUserName().trim().toLowerCase()); // Full Username will need to be searched
        keywords.addAll(Arrays.asList(newExperiment.getTitle().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getDescription().trim().toLowerCase().split("\\W+")));
        keywords.addAll(Arrays.asList(newExperiment.getRegion().toLowerCase().trim().split("\\W+")));
        keywords.add(newExperiment.getStatus().trim().toLowerCase());
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
        redirectActivity(this, MainActivity.class);
    }

    /**
     * This method gives the current drawer layout to the openDrawer method
     * It is called when the hamburger icon is clicked on the toolbar
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMenu(View view){
        // open the drawer
        openDrawer(drawerLayout);
    }

    /**
     * This method opens the navigation drawer
     * @param drawerLayout
     * Takes a drawerLayout that represents the current activities drawer
     */
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**This method gives the current drawer layout to the closeDrawer method
     * It is called when the PenguinDive logo is clicked in the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    /**
     * This method closes the navigation drawer
     * @param drawerLayout
     * Takes a drawerLayout that represents the current activities drawer
     */
    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * This method redirects the user to MainActivity
     * Since the user is current on the MainActivity, it simply closes the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickHome(View view){
        MainActivity.closeDrawer(drawerLayout);
    }

    /**
     * This method redirects the user to the MyExperimentActivity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMyExperiments(View view){
        redirectActivity(this,MyExperimentActivity.class);
    }

    /**
     * This method redirects the user to the PickScanType Activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickScanQrCode(View view){
        redirectActivity(this,PickScanType.class);
    }

    /**
     * This method redirects the user to the PickQRType Activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGenerateQrCode(View view){
        redirectActivity(this,PickQRType.class);
    }

    /**
     * This method redirects the user to the their profile page (Profile Activity)
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMyProfile(View view){
        redirectActivity(this,Profile.class);
    }

    /**
     * This method redirects the user to the search users page (SearchProfile Activity)
     * @param view
     * Takes a view representing the current view
     */
    public void ClickSearchUsers(View view){
        redirectActivity(this,SearchProfile.class);
    }


    /**
     * This method calls openGitHub and provides it the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGitHub(View view){
        openGitHub(this);
    }

    /**
     * This method opens the GitHub page in Google Chrome
     * @param activity
     * Takes an activity that represents the current activity
     */
    public static void openGitHub(Activity activity){
        // Courtesy of Parag Chauhan
        // https://stackoverflow.com/a/4930319
        Uri uri = Uri.parse("https://github.com/CMPUT301W21T24/PenguinDive");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        System.out.println(intent);
        activity.startActivity(intent);
    }

    /**
     * This method is a generic method that redirects the user from the current activity to another
     * @param activity
     * Takes an Activity that represents the current activity
     * @param aClass
     * Takes a Class that represents the class of the next Activity
     */
    public static void redirectActivity(Activity activity, Class aClass){
        Intent intent = new Intent(activity,aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * This method closes the drawer when the activity is paused
     */
    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }

}