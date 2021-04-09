package com.cmput301.penguindive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.UUID;

public class QuestionActivity extends AppCompatActivity {

    ListView questionList;
    ArrayAdapter<Question> questionAdapter;
    ArrayList<Question> questionDataList;
    FirebaseFirestore db;
    final String TAG = "Question_Activity";
    Button addQuestionButton;
    EditText addQuestionSubjectText;
    EditText addQuestionEditText;
    String expID;
    DrawerLayout drawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Assign drawer
        drawerLayout = findViewById(R.id.questionactivity);

        // fetch intent
        Intent intent = getIntent();
        expID = intent.getStringExtra("EXPID");

        // get views
        questionList = findViewById(R.id.question_list);
        addQuestionButton = findViewById(R.id.add_question_button);
        addQuestionEditText = findViewById(R.id.add_question_detail_field);
        addQuestionSubjectText = findViewById(R.id.add_question_subject_field);


        questionDataList = new ArrayList<>();
        questionAdapter = new QuestionCustomList(this,R.layout.question_content, questionDataList);

        questionList.setAdapter(questionAdapter);

        //initialize db
        db = FirebaseFirestore.getInstance();

        // Get a top level reference to the collection
        final CollectionReference collectionReference = db.collection("Questions");

        // push question to DB
        addQuestionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionText = addQuestionEditText.getText().toString();
                String questionSubject = addQuestionSubjectText.getText().toString();
                String questionId = UUID.randomUUID().toString();

                HashMap<String, String> data = new HashMap<>();

                if ((questionText.length() > 0) && (questionSubject.length() > 0) ) {

                    data.put("Text", questionText);
                    data.put("question_title",questionSubject);
                    data.put("question_id",questionId);
                    data.put("experiment_id",expID);

                    collectionReference

                            .document(questionId)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // These are a method which gets executed when the task is succeeded
                                    Log.d(TAG, "Data has been added successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // These are a method which gets executed if there’s any problem
                                    Log.d(TAG, "Data could not be added!" + e.toString());
                                }
                            });

                    addQuestionEditText.setText("");
                    addQuestionSubjectText.setText("");
                    };

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
                        questionDataList.add(new Question(question, questionId, questionTitle));
                    }


                }
                //Notifying the adapter to render any new data fetched from the cloud
                questionAdapter.notifyDataSetChanged();
            }
        });

        // click on question
        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question question = questionDataList.get(position);

                // pass intent and start new activity
                Intent intent = new Intent(QuestionActivity.this, AnswerActivity.class);
                // putting id
                intent.putExtra("ID", question.getQuestionId());
                intent.putExtra("TITLE", question.getQuestionTitle());
                intent.putExtra("TEXT", question.getQuestion());
                startActivity(intent);

            }
        });
    }
    /**
     * This method refreshes the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickRefresh(View view){
        MainActivity.redirectActivity(this, AnswerActivity.class);
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
     * @param view
     * Takes a view representing the current view
     */
    public void ClickSearchUsers(View view){ MainActivity.redirectActivity(this,SearchProfile.class); }

    /**
     * This method calls openGitHub in MainActivity and provides it the current activity
     * @param view
     * Takes a view representing the current view
     */
    public void ClickGitHub(View view){ MainActivity.openGitHub(this); }

}







