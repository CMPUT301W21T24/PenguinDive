package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectedProfile extends AppCompatActivity {
    private TextView name;
    private TextView email;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference profileCollectionReference = db.collection("Experimenter");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_profile);

        name = findViewById(R.id.searchedName);
        email = findViewById(R.id.searchedEmail);

        String docID = getIntent().getStringExtra("profile_ID");
        Log.d("cheese",docID);

        DocumentReference docRef = profileCollectionReference.document(docID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    name.setText(document.getString("name"));
                    email.setText(document.getString("email"));

                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(SelectedProfile.this, SearchProfile.class);
        startActivity(intent);
        finishAffinity();
    }
}
