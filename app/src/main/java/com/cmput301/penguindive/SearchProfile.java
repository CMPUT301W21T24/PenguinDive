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


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchProfile extends AppCompatActivity {
    private ListView profileList;
    private ArrayList<String> profileArray;
    private ArrayAdapter<String> profileAdapter;
    private ArrayList<String> profileID;
    SearchView searchBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_profile);
        searchBar = findViewById(R.id.searchLayout);

        profileList = findViewById(R.id.profileList);

        profileArray = new ArrayList<String>();
        profileID = new ArrayList<String>();

        profileAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, profileArray);
        profileList.setAdapter(profileAdapter);


        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                db.collection("Experimenter")
                        .whereEqualTo("name", s).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            profileArray.clear();
                            profileID.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                profileArray.add((document.getString("name")));
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

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(SearchProfile.this, Profile.class);
        startActivity(intent);
        finishAffinity();
    }
}
