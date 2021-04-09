package com.cmput301.penguindive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents the welcome screen where the user is prompted with their hardware id
 */
public class WelcomeActivity extends AppCompatActivity {
    String uid;
    TextView uidText;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        uidText = findViewById(R.id.uidText);
        startButton = findViewById(R.id.startButton);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference experimenterCollectionReference = db.collection("Experimenter");
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);

        uid = sharedPref.getString("UID", "");

        if (uid.equals("")){
            uid = UUID.randomUUID().toString();
            uidText.setText(uid);
            saveIdentity(uid);
            Map<String, Object> docData = new HashMap<>();
            docData.put("name", uid);
            experimenterCollectionReference.document(uid).set(docData);
        }else{
            uidText.setText(uid);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    public void saveIdentity(final String UID) {
        Context context = WelcomeActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences("identity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("UID", UID);
        editor.apply();
    }
}
