package com.cmput301.penguindive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private EditText name;
    private EditText email;
    private TextView id;
    Button saveButton;
    Button searchButton;
    String uid;

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
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);

        uid = sharedPref.getString("UID", "");

        //make a query on the given profile to display the id, name, contact information of the instance's user
        DocumentReference docRef = profileCollectionReference.document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    id.setText("User ID: " + document.getId());
                    name.setText(document.getString("name"));
                    email.setText(document.getString("email"));

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