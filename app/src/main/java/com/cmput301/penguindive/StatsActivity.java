package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
/**
 * This class is an activity which displays the statistics and gives the user the ability to display histograms relative to
 * four trial types an experiment may have
 */
public class StatsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> count = new ArrayList<>();

    String type;
    String name;
    String expID;

    TextView ExpName;
    TextView Q1;
    TextView Q2;
    TextView Q3;
    TextView Mean;
    TextView Stdev;

    Button histogramButton;
    Button plotButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set view
        setContentView(R.layout.activity_stats);

        //import the name of the experiment
        Intent intent = getIntent();
        expID = intent.getStringExtra("EXPID");

        //Initialize text and button xml
        histogramButton = findViewById(R.id.histogramButton);
        ExpName = findViewById(R.id.Name);
        Q1 = findViewById(R.id.Q1);
        Q2 = findViewById(R.id.Q2);
        Q3 = findViewById(R.id.Q3);
        Mean = findViewById(R.id.Mean);
        Stdev = findViewById(R.id.Stdev);

        DocumentReference docRef = db.collection("Experiments").document(expID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //Find the Name and the Trial Type of the Experiment
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        type = document.getString("TrialType");
                        name = document.getString("Title");
                    }}

                //Stats specific to count trials
                if (type.equals("Count Trial")){
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    count.add(document.getString("Count Type"));
                                }
                            }
                            int TRUE = Collections.frequency(count,"TRUE");
                            int FALSE = Collections.frequency(count,"FALSE");
                            int total = TRUE - FALSE;

                            ExpName.setText("Experiment Name: "+ name);
                            Q1.setText("Count = " + total);

                        }
                    });
                }

                //Stats specific to Binomial trials
                else if (type.equals("Binomial Trial")){
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    count.add(document.getString("Binomial Type"));
                                }
                            }
                            int TRUE = Collections.frequency(count,"Pass");
                            int FALSE = Collections.frequency(count,"Fail");

                            ExpName.setText("Experiment Name: "+ name);
                            Q1.setText("Pass: " + TRUE + " Fail: "+ FALSE);
                        }
                    });
                }


                //Stats specific to Measurement trials
                else if (type.equals("Measurement Trial")){
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String value = document.getString("Measurement");
                                    count.add(value);
                                }
                            }
                            //make sure all elements in count are floats and not a mix of floats and ints
                            float [] floatValues = new float[count.size()];
                            float total = 0;
                            for (int i = 0; i < count.size(); i++) {
                                floatValues[i] = Float.parseFloat(count.get(i));
                                total += floatValues[i];
                            }
                            Arrays.sort(floatValues);
                            count = new ArrayList<String>();
                            for (int i = 0; i <floatValues.length; i++) {
                                count.add(String.valueOf(floatValues[i]));
                            }
                            Log.d("TAG", String.valueOf(count));
                            ExpName.setText("Experiment Name: "+ name);
                            //Q2 is also median
                            // if size of count is even
                            if ((count.size()%2)==0){
                                int pos1 = (count.size()/2)- 1;
                                int pos2 = count.size()/2;
                                String Median = String.valueOf((Float.parseFloat(count.get(pos1)) + Float.parseFloat(count.get(pos2)))/2);
                                Q2.setText("Q2/Median: " + Median);
                                if ((pos1 % 2) == 1){
                                    int left = pos1/2;
                                    int right = (pos1/2)+1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q1.setText("Q1: " + Median);
                                    left = pos1/2 + pos2;
                                    right = left + 1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q3.setText("Q3: " + Median);
                                }
                                else{
                                    Median = count.get(pos1/2);
                                    Q1.setText("Q1: " + Median);
                                    Median = count.get((pos1/2)+pos2);
                                    Q3.setText("Q3: " + Median);
                                }
                            }
                            // if size of count is odd
                            else{
                                String Median = count.get((count.size()/2));
                                Q2.setText("Q2/Median: " + Median);
                                if ((count.size()/2)%2 == 0){
                                    int left = (count.size()/2)-2;
                                    int right = left+1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q1.setText("Q1: " + Median);
                                    left = (count.size()/2) + (count.size()/2)/2;
                                    right = left + 1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q3.setText("Q3: " + Median);
                                }
                                else{
                                    int q1 = (count.size()/2)/2;
                                    Q1.setText("Q1: " + count.get(q1));
                                    int q3 = (count.size()/2) + q1 + 1;
                                    Q3.setText("Q3: " + count.get(q3));

                                }
                            }
                            //find mean
                            Mean.setText("Mean: " +  total/floatValues.length);
                            float stdevTotal = 0;
                            float [] stdevValues = new float[floatValues.length];
                            for (int i = 0; i <floatValues.length; i++) {
                                stdevValues[i] = (floatValues[i] - (total/floatValues.length)) * (floatValues[i] - (total/floatValues.length));
                                stdevTotal += stdevValues[i];
                            }
                            Stdev.setText("Stdev: " + Math.sqrt(stdevTotal/stdevValues.length));

                        }
                    });
                }

                //Stats specific to Non-negative Integer Trials
                else if (type.equals("Non-Negative Integer Count Trial")) {
                    db.collection("Trials")
                            .whereEqualTo("Experiment Name", name)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String value = document.getString("Non-Negative Integer");
                                    count.add(value);
                                }
                            }
                            //make sure all elements in count are integers
                            int [] intValues = new int[count.size()];
                            float total = 0;
                            for (int i = 0; i < count.size(); i++) {
                                intValues[i] = Integer.parseInt(count.get(i));
                                total += intValues[i];
                            }
                            Arrays.sort(intValues);
                            count = new ArrayList<String>();
                            for (int i = 0; i <intValues.length; i++) {
                                count.add(String.valueOf(intValues[i]));
                            }
                            ExpName.setText("Experiment Name: "+ name);
                            //Q2 is also median
                            // if size of count is even
                            if ((count.size()%2)==0){
                                int pos1 = (count.size()/2)- 1;
                                int pos2 = count.size()/2;
                                String Median = String.valueOf((Float.parseFloat(count.get(pos1)) + Float.parseFloat(count.get(pos2)))/2);
                                Q2.setText("Q2/Median: " + Median);

                                if ((pos1 % 2) == 1){
                                    int left = pos1/2;
                                    int right = (pos1/2)+1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q1.setText("Q1: " + Median);
                                    left = pos1/2 + pos2;
                                    right = left + 1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q3.setText("Q3: " + Median);
                                }
                                else{
                                    Median = count.get(pos1/2);
                                    Q1.setText("Q1: " + Median);
                                    Median = count.get((pos1/2)+pos2);
                                    Q3.setText("Q3: " + Median);
                                }
                            }
                            // if size of count is odd
                            else{
                                String Median = count.get((count.size()/2));
                                Q2.setText("Q2/Median: " + Median);
                                if ((count.size()/2)%2 == 0){
                                    int left = (count.size()/2)-2;
                                    int right = left+1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q1.setText("Q1: " + Median);
                                    left = (count.size()/2) + (count.size()/2)/2;
                                    right = left + 1;
                                    Median = String.valueOf((Float.parseFloat(count.get(left)) + Float.parseFloat(count.get(right)))/2);
                                    Q3.setText("Q3: " + Median);
                                }
                                else{
                                    int q1 = (count.size()/2)/2;
                                    Q1.setText("Q1: " + count.get(q1));
                                    int q3 = (count.size()/2) + q1 + 1;
                                    Q3.setText("Q3: " + count.get(q3));

                                }
                            }
                            //find mean
                            Mean.setText("Mean: " +  total/intValues.length);
                            float stdevTotal = 0;
                            float [] stdevValues = new float[intValues.length];
                            for (int i = 0; i <intValues.length; i++) {
                                stdevValues[i] = (intValues[i] - (total/intValues.length)) * (intValues[i] - (total/intValues.length));
                                stdevTotal += stdevValues[i];
                            }
                            Stdev.setText("Stdev: " + Math.sqrt(stdevTotal/stdevValues.length));
                        }
                    });
                }
            }
        });

        histogramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatsActivity.this, Histogram.class);
                intent.putExtra("EXPID",expID);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    //return to the main activity on back button press.
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(StatsActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
