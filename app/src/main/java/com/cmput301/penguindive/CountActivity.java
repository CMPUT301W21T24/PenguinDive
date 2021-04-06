package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CountActivity extends AppCompatActivity {

    // initialize object
    private Count_Trial countTrial;

    // initialize experiment name
    private TextView experimentNameView;

    // declare buttons
    private Button incrementCount;
    private Button decrementCount;

    // declare count
    private TextView count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_count_activity);

        // set up intent to get experiment name
        Intent intent = getIntent();
        String experimentName = intent.getStringExtra("Experiment Name");

        // set buttons
        incrementCount = findViewById(R.id.count_add_button);
        decrementCount = findViewById(R.id.count_subtract_button);

        // set object
        countTrial = new Count_Trial();

        // set count view
        count = findViewById(R.id.count_number);  // already set to zero in the constructor
        count.setText(String.valueOf(countTrial.getCount()));
        // set experiment name
        experimentNameView = findViewById(R.id.count_experiment_name);
        experimentNameView.setText(experimentName);

        // set onclicklisteners
        incrementCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countTrial.incrementCount();
                count.setText(String.valueOf(countTrial.getCount()));
            }
        });

        decrementCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countTrial.decrementCount();
                count.setText(String.valueOf(countTrial.getCount()));

            }
        });

    }
}
