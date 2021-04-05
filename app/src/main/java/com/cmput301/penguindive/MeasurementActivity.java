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

public class MeasurementActivity extends AppCompatActivity implements MeasurementFragment.OnFragmentInteractionListener {

    // declare variables for adapting
    ListView MeasurementTrialList;
    ArrayList<Measurement_Trial> MeasurementTrialDataList;
    ArrayAdapter<Measurement_Trial> MeasurementArrayAdapter;

    // declare add Trial button
    private Button addMeasurementTrialButton;

    // initialize name view
    TextView experimentNameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_measurement_activity);  // set the content view to NNICActivity

        // get the experiment name
        Intent intent = getIntent();
        String experimentName = intent.getStringExtra("Experiment Name");
        experimentNameView = findViewById(R.id.measurement_experiment_name);
        experimentNameView.setText(experimentName);

        // set the trial list to the view
        MeasurementTrialList = findViewById(R.id.measurement_trial_list);
        // data list is new arraylist
        MeasurementTrialDataList = new ArrayList<>();

        // set the adapter to be the MeasurementCustomlist adapter, with dataList as the data
        MeasurementArrayAdapter = new MeasurementCustomList(this, MeasurementTrialDataList);

        // set MeasurementTrialList's (the view) adapter to MeasurementArrayAdapter, letting the data be used for the view
        MeasurementTrialList.setAdapter(MeasurementArrayAdapter);

        // fragment stuff
        // initialize button
        addMeasurementTrialButton = findViewById(R.id.measurement_add_trial_button);

        // set the listener for the button
        addMeasurementTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make a new instance of the fragment and show it with a fragment manager/tag
                new MeasurementFragment().show(getSupportFragmentManager(), "Add Measurement");
            }
        });
    }

    // implement adding trial method required by interface
    @Override
    public void AddMeasurement_Trial(Measurement_Trial measurement_trial) {
        MeasurementArrayAdapter.add(measurement_trial);
    }
}
