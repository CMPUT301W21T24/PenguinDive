package com.cmput301.penguindive;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CurrentExperimentersActivity extends AppCompatActivity {
    ListView experimentersList;
    ArrayAdapter<String> experimentersAdapter;
    ArrayList<String> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_experimenters);
        experimentersList = findViewById(R.id.current_experimenters_list);
        String[] experimenters = {"Hello", "world"};

        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(experimenters));

        experimentersAdapter = new ArrayAdapter<>(this, R.layout.current_experimenters_layout, dataList);
        experimentersList.setAdapter(experimentersAdapter);


    }
}
