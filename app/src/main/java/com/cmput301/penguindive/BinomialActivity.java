package com.cmput301.penguindive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// This class is for the binomial activity
public class BinomialActivity extends AppCompatActivity {

    // initialize object
    private Binomial_Trial binomialTrial;

    // declare buttons
    private Button addPassButton;
    private Button addFailButton;

    // declare textviews
    private TextView numPasses;
    private TextView numFails;

    private TextView experimentNameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_binomial_activity);  // set view to be binomial activity view

        // get experiment name from intent
        Intent intent = getIntent();
        String experimentName = intent.getStringExtra("Experiment Name");

        // set buttons
        addPassButton = findViewById(R.id.binomial_pass_button);
        addFailButton = findViewById(R.id.binomial_fail_button);

        // set object
        binomialTrial = new Binomial_Trial(0, 0);

        // set textviews
        numPasses = findViewById(R.id.binomial_numpasses);
        numFails = findViewById(R.id.binomial_numfails);
        numPasses.setText(String.valueOf(binomialTrial.getNumPasses()));
        numFails.setText(String.valueOf(binomialTrial.getNumFails()));

        // set experiment name
        experimentNameView = findViewById(R.id.binomial_experiment_name);
        experimentNameView.setText(experimentName);

        // set onClickListeners
        // when this is clicked, run the class's add pass method
        addPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binomialTrial.addOnePass();
                numPasses.setText(String.valueOf(binomialTrial.getNumPasses()));
            }
        });

        addFailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binomialTrial.addOneFail();
                numFails.setText(String.valueOf(binomialTrial.getNumFails()));
            }
        });
    }
}
