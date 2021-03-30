package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
/**
 * This class represents an activity which shows the selected profile
 */
public class SelectedProfile extends AppCompatActivity {
    private TextView name;
    private TextView email;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference profileCollectionReference = db.collection("Experimenter");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set view
        setContentView(R.layout.selected_profile);

        // initialize elements
        name = findViewById(R.id.searchedName);
        email = findViewById(R.id.searchedEmail);

        // receive the profile Id of the selected profile
        String docID = getIntent().getStringExtra("profile_ID");

        // Display the name and email of selected profile
        DocumentReference docRef = profileCollectionReference.document(docID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    name.setText("Name: " + document.getString("name"));
                    email.setText("Email: " + document.getString("email"));

                }
            }
        });
    }
    // return to the profile list activity on back button press
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(SelectedProfile.this, SearchProfile.class);
        startActivity(intent);
        finishAffinity();
    }
}