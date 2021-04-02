package com.cmput301.penguindive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

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
    private NumberPicker experimentMinimumTrials;
    private TextView experimentOwner;
    private Spinner experimentStatus;
    private OnFragmentInteractionListener listener;
    public Experiment experiment;
    private String experimentID;
    private List<String> experimenterIDs;
    private int position;

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
        experimentMinimumTrials = view.findViewById(R.id.editCount);
        experimentOwner = view.findViewById(R.id.editOwner);
        experimentStatus = view.findViewById(R.id.editStatus);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("identity", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("UID", "");
        experimentOwner.setText(userID);

        // Set status spinner adapter and link it to the list of options in strings.xml
        ///https://developer.android.com/guide/topics/ui/controls/spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.status_array));
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        experimentStatus.setAdapter(statusAdapter);

        // Set minTrials number picker
        experimentMinimumTrials.setMinValue(1);
        experimentMinimumTrials.setMaxValue(999);

        Bundle bundle = getArguments();
        if (bundle != null) {
            experiment = (Experiment) bundle.getSerializable("experiment");
            position = (int) bundle.getSerializable("pos");
            experimentID = experiment.getExperimentId();
            experimentTitle.setText(experiment.getTitle());
            experimentDescription.setText(experiment.getDescription());
            experimentRegion.setText(experiment.getRegion());
            experimentOwner.setText(experiment.getOwnerUserName());
            // Return number picker to last choice
            experimentMinimumTrials.setValue(experiment.getMinTrials());
            // Return spinner to last choice
            int spinnerPosition = statusAdapter.getPosition(experiment.getStatus());
            experimentStatus.setSelection(spinnerPosition);

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
                        }
                        String title = experimentTitle.getText().toString();
                        String description = experimentDescription.getText().toString();
                        String region = experimentRegion.getText().toString();
                        Integer minTrials = experimentMinimumTrials.getValue();
                        String ownerUserName = experimentOwner.getText().toString();
                        String status = experimentStatus.getSelectedItem().toString();
                        experimenterIDs = new ArrayList<String>();
                        if(description.length()==0){
                            listener.nullValueError();
                        }
                        else if(description.length() >250){
                            listener.extraStringError();
                        }
                        else if(title.length()==0){
                            listener.nullValueError();
                        }
                        else if(title.length() >40){
                            listener.extraStringError();
                        }
                        else if(experiment != null){
                            listener.onEditPressed(new Experiment(experimentID,title,description,region, minTrials,ownerUserName,status,experimenterIDs), position);
                        }
                        else {
                            listener.onOkPressed(new Experiment(experimentID, title,description,region, minTrials,ownerUserName,status,experimenterIDs));
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
