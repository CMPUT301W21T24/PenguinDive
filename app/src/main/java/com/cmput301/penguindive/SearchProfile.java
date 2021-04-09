package com.cmput301.penguindive;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class represents an activity that allows
 * the user to search for other user's profiles
 */

public class SearchProfile extends AppCompatActivity {
    private ArrayList<String> profileArray;
    private ArrayAdapter<String> profileAdapter;
    private ArrayList<String> profileID;
    SearchView searchBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view
        setContentView(R.layout.search_profile);

        // Assign drawer
        drawerLayout = findViewById(R.id.searchprofile);

        // initialise elements
        searchBar = findViewById(R.id.searchLayout);
        ListView profileList = findViewById(R.id.profileList);

        //create an array adapter for the list of profiles
        profileArray = new ArrayList<>();
        profileID = new ArrayList<>();

        profileAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, profileArray);
        profileList.setAdapter(profileAdapter);

        // set up search bar
        searchBar.setIconified(true);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // clear the result listview so that new results will appear
                profileArray.clear();
                profileID.clear();
                profileAdapter.notifyDataSetChanged();

                // Search using name
                db.collection("Experimenter")
                        .whereEqualTo("name", s)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String x = document.getString("name") + " " + (document.getString("email"));
                                profileArray.add(x);
                                profileID.add((document.getId()));
                            }
                            profileAdapter.notifyDataSetChanged();
                        }
                    }
                });
                // search using email
                db.collection("Experimenter")
                        .whereEqualTo("email", s)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        profileArray.clear();
                        profileAdapter.notifyDataSetChanged();
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                profileArray.add((document.getString("email")));
                                profileID.add((document.getId()));
                            }
                            profileAdapter.notifyDataSetChanged();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        // send to selected profile activity when a profile is clicked on the list of profiles

        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchProfile.this, SelectedProfile.class);
                intent.putExtra("profile_ID",profileID.get(i));
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    // return to the profile activity on back button press
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(SearchProfile.this, Profile.class);
        startActivity(intent);
        finishAffinity();
    }

    /**
     * This method refreshes the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, SearchProfile.class);
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
     * @param view
     * Takes a view representing the current view
     */
    public void ClickMyExperiments(View view){ MainActivity.redirectActivity(this,MyExperimentActivity.class); }

    /**
     * This method redirects the user to the PickScanType Activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickScanQrCode(View view){ MainActivity.redirectActivity(this,PickScanType.class);  }

    /**
     * This method redirects the user to the PickQRType Activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGenerateQrCode(View view){ MainActivity.redirectActivity(this,PickQRType.class);}

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
     * Since the user is already on the SearchProfile Activity, this simply closes the drawer
     * @param view
     * Takes a view representing the current view
     */
    public void ClickSearchUsers(View view){ MainActivity.closeDrawer(drawerLayout); }

    /**
     * This method calls openGitHub in MainActivity and provides it the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGitHub(View view){ MainActivity.openGitHub(this); }

}