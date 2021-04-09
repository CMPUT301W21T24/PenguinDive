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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AnswerActivity extends AppCompatActivity {

    ListView answerList;
    ArrayAdapter<Answer> answerAdapter;
    ArrayList<Answer> answerDataList;
    FirebaseFirestore db;
    final String TAG = "Answer_Activity";
    Button addAnswerButton;
    EditText addAnswerEditText;
    TextView subjectField;
    TextView questionField;
    EditText searchAnswer;
    Button searchAnswerButton;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        // getting intent
        Intent intent = getIntent();
        final String ID = intent.getStringExtra("ID");
        final String q_title = intent.getStringExtra("TITLE");
        final String q_text = intent.getStringExtra("TEXT");
        final String keyword = intent.getStringExtra("KEYWORD");

        // fetching views
        answerList = findViewById(R.id.answer_list);
        subjectField = findViewById(R.id.question_subject);
        questionField = findViewById(R.id.question_text);
        addAnswerButton = findViewById(R.id.reply_button);
        addAnswerEditText = findViewById(R.id.answer_editText);
        searchAnswer = findViewById(R.id.search_answer);
        searchAnswerButton = findViewById(R.id.search_answer_button);

        subjectField.setText(q_title);
        questionField.setText(q_text);

        answerDataList = new ArrayList<>();

        answerAdapter = new AnswerCustomList(this,R.layout.answer_content, answerDataList);

        answerList.setAdapter(answerAdapter);

        //initialize db
        db = FirebaseFirestore.getInstance();

        // Get UID
        SharedPreferences sharedPref = this.getSharedPreferences("identity", Context.MODE_PRIVATE);
        uid = sharedPref.getString("UID", "");

        // Get a top level reference to the collection
        final CollectionReference collectionReference = db.collection("Answers");
        // push to DB
        addAnswerButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answerText = addAnswerEditText.getText().toString();
                HashMap<String, String> data = new HashMap<>();

                if (answerText.length()>0) {
                    data.put("answer_text", answerText);
                    data.put("question_id", ID);
                    data.put("answer_user_id",uid);

                    collectionReference
                            // Add a new document with a generated id.
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                    //Set fields to null so that the user can add a new answer
                    addAnswerEditText.setText("");
                };

            }
        });
        searchAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchAnswer.getText().toString();
                if(keyword.length() > 0){
                    // pass intent and start new activity
                    Intent intent = new Intent(AnswerActivity.this, AnswerActivity.class);
                    // putting word
                    intent.putExtra("KEYWORD", keyword);
                    intent.putExtra("ID", ID);
                    intent.putExtra("TITLE", q_title);
                    intent.putExtra("TEXT", q_text);
                    startActivity(intent);
                }
            }
        });

        // pull from db
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                answerDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getData().get("answer_text")));

                    String question_id = (String)doc.getData().get("question_id");
                    String answer = (String)doc.getData().get("answer_text");
                    String answerUserid = (String)doc.getData().get("answer_user_id");

                    if(question_id.equals(ID)){
                        if(answer.contains(keyword.toLowerCase()) || answer.equals("")){
                            answerDataList.add(new Answer(answer,answerUserid));
                        }
                    }
                }
                //Notifying the adapter to render any new data fetched from the cloud
                answerAdapter.notifyDataSetChanged();
            }
        });


    }
    }
