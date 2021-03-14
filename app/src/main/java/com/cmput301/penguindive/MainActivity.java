package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ExperimentFragment.OnFragmentInteractionListener{

    private ListView experimentList;
    private SearchView SearchContent;
    private ExperimentCustomList experimentAdapter;
    public static ArrayList<Experiment> experimentDataList = new ArrayList<Experiment>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        experimentList = findViewById(R.id.experimentList);
        experimentDataList = new ArrayList<>();

        experimentAdapter = new ExperimentCustomList(this);
        experimentList.setAdapter(experimentAdapter);

        final FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExperimentFragment().show(getSupportFragmentManager(), "ADD");
            }
        });

        // create a clickListener on the existing experiment
        experimentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Experiment trail = experimentDataList.get(position);
                ExperimentFragment.newInstance(trail, position).show(getSupportFragmentManager(), "Edit");
            }
        });

    }

    //changes after clicking OK/Edit/Delete button
    @Override
    public void onOkPressed(Experiment newExperiment) {
        experimentDataList.add(newExperiment);
        experimentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditPressed(Experiment newExperiment, int position) {
        experimentDataList.set(position,newExperiment);
        experimentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeletePressed(Experiment experiment) {
        experimentDataList.remove(experiment);
        experimentAdapter.notifyDataSetChanged();
    }

    //showing message when there is any invalid input
    @Override
    public void extraStringError() {
        Toast.makeText(MainActivity.this,"The description exceeds the maximum limitation.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void nullValueError() {
        Toast.makeText(MainActivity.this,"The information of description and date of the experiment is required.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        experimentAdapter.filter(newText);
        return false;
    }
}