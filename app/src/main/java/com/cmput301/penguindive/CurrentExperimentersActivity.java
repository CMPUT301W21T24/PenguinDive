package com.cmput301.penguindive;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CurrentExperimentersActivity extends AppCompatActivity {
    ListView experimentersList;
    ArrayList<String> experimentersDataList;
    ArrayAdapter<String> experimentersArrayAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_experimenters);

        experimentersList = findViewById(R.id.current_experimenters);

        experimentersDataList = new ArrayList<String>();
        experimentersArrayAdapter = new curre

    }
}
