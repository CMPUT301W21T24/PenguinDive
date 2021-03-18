package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Profile extends AppCompatActivity {
    private String phoneNumber;
    private Boolean experimenter = false;
    private Boolean owner = false;
    private EditText name;
    private EditText email;
    private TextView id;
    Button saveButton;
    Button searchButton;

    //private ArrayList<Experiment> ownedExperiments;
    //private ArrayList<Experiment> subscribedExperiments;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference profileCollectionReference = db.collection("Experimenter");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile);
        id = findViewById(R.id.UserID);
        name = findViewById(R.id.userName);
        email = findViewById(R.id.Email);
        saveButton = findViewById(R.id.saveButton);
        searchButton = findViewById(R.id.searchButton);

        //make a query on the given profile to display the contact information
        DocumentReference docRef = profileCollectionReference.document("Test");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    id.setText("User ID: " + document.getId());
                    name.setText(document.getString("name"));
                    email.setText(document.getString("email"));

                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
                    }
                });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.update("name",name.getText().toString());
                docRef.update("email",email.getText().toString());
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, SearchProfile.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

}


