package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NNICActivity extends AppCompatActivity implements NNICFragment.OnFragmentInteractionListener {

    // declare variables for adapting
    ListView NNICTrialList;
    ArrayList<Non_Negative_Integer_Counts_Trial> NNICTrialDataList;
    ArrayAdapter<Non_Negative_Integer_Counts_Trial> NNICArrayAdapter;

    // declare add Trial button
    private Button addNNICTrialButton;

    // declare name view
    private TextView experimentNameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_nnic_activity);  // set the content view to NNICActivity

        // get intent/experiment name
        Intent intent = getIntent();
        String experimentName = intent.getStringExtra("Experiment Name");
        experimentNameView = findViewById(R.id.nnic_experiment_name);
        experimentNameView.setText(experimentName);

        // set the trial list to the view
        NNICTrialList = findViewById(R.id.nnic_trial_list);
        // data list is new arraylist
        NNICTrialDataList = new ArrayList<>();

        // set the adapter to be the NNICCustomlist adapter, with dataList as the data
        NNICArrayAdapter = new NNICCustomList(this, NNICTrialDataList);

        // set NNICTrialList's (the view) adapter to NNICArrayAdapter, letting the data be used for the view
        NNICTrialList.setAdapter(NNICArrayAdapter);

        // fragment stuff
        // initialize button
        addNNICTrialButton = findViewById(R.id.nnic_add_trial_button);

        // set the listener for the button
        addNNICTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make a new instance of the fragment and show it with a fragment manager/tag
                new NNICFragment().show(getSupportFragmentManager(), "Add NNIC");
            }
        });
    }

    // implement adding trial method required by interface
    public void AddNNIC_Trial(Non_Negative_Integer_Counts_Trial NNICTrial) {
        NNICArrayAdapter.add(NNICTrial);
    }

}
