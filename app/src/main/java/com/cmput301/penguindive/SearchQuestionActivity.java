package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SearchQuestionActivity extends AppCompatActivity {

    ListView questionList;
    ArrayAdapter<Question> questionAdapter;
    ArrayList<Question> questionDataList;
    FirebaseFirestore db;
    final String TAG = "Question_Activity";
    String expID;
    String keyword;
    Button searchQuestionButton2;
    EditText searchQuestion2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_question);

        // fetch intent
        Intent intent = getIntent();
        expID = intent.getStringExtra("EXPID");
        keyword = intent.getStringExtra("KEYWORD");

        // get views
        questionList = findViewById(R.id.question_list);
        searchQuestionButton2 = findViewById(R.id.search_question_button2);
        searchQuestion2 = findViewById(R.id.search_question2);


        questionDataList = new ArrayList<>();
        questionAdapter = new QuestionCustomList(this,R.layout.question_content, questionDataList);

        questionList.setAdapter(questionAdapter);

        //initialize db
        db = FirebaseFirestore.getInstance();

        // Get a top level reference to the collection
        final CollectionReference collectionReference = db.collection("Questions");

        searchQuestionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchQuestion2.getText().toString();
                if(keyword.length() > 0){
                    // pass intent and start new activity
                    Intent intent = new Intent(SearchQuestionActivity.this, SearchQuestionActivity.class);
                    // putting word
                    intent.putExtra("KEYWORD", keyword);
                    intent.putExtra("EXPID",expID);
                    startActivity(intent);
                }
            }
        });

        // pull question from DB
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                questionDataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getData().get("Text")));
                    String question = (String)doc.getData().get("Text");
                    String questionId = doc.getId();
                    String questionTitle = (String)doc.getData().get("question_title");
                    String experimentID = (String)doc.getData().get("experiment_id");

                    //add new question
                    if(experimentID.equals(expID)){
                        if(question.contains(keyword) || questionId.contains(keyword) || questionTitle.contains(keyword)){
                            questionDataList.add(new Question(question, questionId, questionTitle));
                        }
                    }


                }
                //Notifying the adapter to render any new data fetched from the cloud
                questionAdapter.notifyDataSetChanged();
            }
        });


    }

}

