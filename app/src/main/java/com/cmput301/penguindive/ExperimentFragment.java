package com.cmput301.penguindive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class represents a dialog fragment for experiments
 */
public class ExperimentFragment extends DialogFragment {
    private EditText experimentTitle;
    private EditText experimentDescription;
    private EditText experimentRegion;
    private EditText experimentCount;
    private TextView experimentOwner;
    private EditText experimentStatus;
    private ToggleButton experimentLocation;
    private OnFragmentInteractionListener listener;
    public Experiment experiment;
    private String experimentID;
    private List<String> experimenterIDs;
    private int position;
    public Boolean locationStatus;

    public interface OnFragmentInteractionListener {
        //check the validity of input
        void onOkPressed(Experiment newExperiment);
        void onEditPressed(Experiment experiment, int position);
        void onDeletePressed(Experiment experiment);
        void extraStringError();
        void nullValueError();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.experiment_fragment, null);
        experimentTitle = view.findViewById(R.id.editTitle);
        experimentDescription = view.findViewById(R.id.editDescription);
        experimentRegion = view.findViewById(R.id.editRegion);
        experimentCount = view.findViewById(R.id.editCount);
        experimentOwner = view.findViewById(R.id.editOwner);
        experimentStatus = view.findViewById(R.id.editStatus);
        experimentLocation = view.findViewById(R.id.location_switch);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("identity", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("UID", "");
        experimentOwner.setText(userID);

        Bundle bundle = getArguments();
        if (bundle != null) {
            experiment = (Experiment) bundle.getSerializable("experiment");
            locationStatus = experiment.getLocationState();
            position = (int) bundle.getSerializable("pos");
            experimentID = experiment.getExperimentId();
            experimentTitle.setText(experiment.getTitle());
            experimentDescription.setText(experiment.getDescription());
            experimentRegion.setText(experiment.getRegion());
            experimentOwner.setText(experiment.getOwnerUserName());
            experimentCount.setText(experiment.getTotalTrail());
            experimentStatus.setText(experiment.getStatus());
            experimentLocation.setChecked(locationStatus);
            experimenterIDs = experiment.getExperimenters();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add/Edit/Delete Experiment")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        listener.onDeletePressed(experiment);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (experimentID == null){
                            experimentID = UUID.randomUUID().toString();
                            locationStatus = false;
                        }
                        String title = experimentTitle.getText().toString();
                        String description = experimentDescription.getText().toString();
                        String region = experimentRegion.getText().toString();
                        String totalTrail = experimentCount.getText().toString();
                        String ownerUserName = experimentOwner.getText().toString();
                        String status = experimentStatus.getText().toString();
                        experimenterIDs = new ArrayList<String>();

                        if(description.length()==0){
                            listener.nullValueError();
                        }
                        else if(description.length() >40){
                            listener.extraStringError();
                        }
                        else if(title.length()==0){
                            listener.nullValueError();
                        }
                        else if(title.length() >40){
                            listener.extraStringError();
                        }
                        // set the location status
                        locationStatus = experimentLocation.isChecked();

                        if(experiment != null){
                            listener.onEditPressed(new Experiment(experimentID, title,description,region,totalTrail,ownerUserName,status,experimenterIDs, locationStatus), position);
                        }
                        else{
                            listener.onOkPressed(new Experiment(experimentID, title,description,region,totalTrail,ownerUserName,status,experimenterIDs, locationStatus));
                        }
                    }}).create();
    }
    static ExperimentFragment newInstance(Experiment experiment, int position){
        Bundle args = new Bundle();
        args.putSerializable("experiment", experiment);
        args.putSerializable("pos", position);
        ExperimentFragment fragment = new ExperimentFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
