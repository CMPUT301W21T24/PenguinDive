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
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ToggleButton experimentLocation;
    private Spinner experimentStatus;
    private OnFragmentInteractionListener listener;
    public Experiment experiment;
    private String experimentID;
    private List<String> experimenterIDs;
    private int position;
    public Boolean locationStatus;
    private Spinner spinnerTrialType;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");


    public interface OnFragmentInteractionListener {
        //check the validity of input
        void onOkPressed(Experiment newExperiment);
        void onEditPressed(Experiment experiment, int position);
        void onDeletePressed(Experiment experiment);
        void extraStringError();
        void nullValueError();
    }

    @Override
    public void onAttach(@NotNull Context context) {
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
        experimentLocation = view.findViewById(R.id.location_switch);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("identity", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("UID", "");
        experimentOwner.setText(userID);
        spinnerTrialType = view.findViewById(R.id.spinnerTrialType);

        // Set status spinner adapter and link it to the list of options in strings.xml
        ///https://developer.android.com/guide/topics/ui/controls/spinner
        ArrayAdapter<String> statusAdapter;
        statusAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.status_array));
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        experimentStatus.setAdapter(statusAdapter);

        // Set Trial Type Spinner adapter and link it to the list of options in strings.xml
        ArrayAdapter<String> trialtypeAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.trialtype_array));
        trialtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTrialType.setAdapter(trialtypeAdapter);

        // Set minTrials number picker
        experimentMinimumTrials.setMinValue(1);
        experimentMinimumTrials.setMaxValue(999);

        Bundle bundle = getArguments();
        if (bundle != null) {
            experiment = (Experiment) bundle.getSerializable("experiment");
            locationStatus = experiment.getLocationState();
            position = (int) bundle.getSerializable("pos");
            experimentID = experiment.getExperimentId();
            experimentTitle.setText(experiment.getTitle());
            experimentDescription.setText(experiment.getDescription());
            experimentRegion.setText(experiment.getRegion());
            experimentOwner.setText(experiment.getOwnerId());
            // Return number picker to last choice
            experimentMinimumTrials.setValue(experiment.getMinTrials());
            // Return spinner to last choice
            int spinnerPosition = statusAdapter.getPosition(experiment.getStatus());
            experimentStatus.setSelection(spinnerPosition);
            // Trial Type Spinner
            int trialtypeSpinnerPosition = trialtypeAdapter.getPosition(experiment.getTrialType());
            spinnerTrialType.setSelection(trialtypeSpinnerPosition);
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
                        if(experiment != null){
                            listener.onDeletePressed(experiment);
                        }
                    }
                })
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    if (experimentID == null){
                        experimentID = UUID.randomUUID().toString();
                        locationStatus = false;
                        experimenterIDs = new ArrayList<String>();
                    }
                    String title = experimentTitle.getText().toString();
                    String description = experimentDescription.getText().toString();
                    String region = experimentRegion.getText().toString();
                    Integer minTrials = experimentMinimumTrials.getValue();
                    String ownerId = experimentOwner.getText().toString();
                    String status = experimentStatus.getSelectedItem().toString();
                    String trialType = spinnerTrialType.getSelectedItem().toString();



                    if(description.length()==0){
                        listener.nullValueError();
                    }
                    else if(description.length() >500){
                        listener.extraStringError();
                    }
                    else if(title.length()==0){
                        listener.nullValueError();
                    }
                    else if(title.length() >100){
                        listener.extraStringError();
                    }
                    // If an existing experiment
                    else if(experiment != null){
                        // Re-use existing username
                        String ownerName = experiment.getOwnerUserName(); //
                        // set the location status
                        locationStatus = experimentLocation.isChecked();
                        Experiment newExperiment = new Experiment(experimentID, title, description, region, minTrials, ownerId, ownerName, status, experimenterIDs, locationStatus, trialType);
                        listener.onEditPressed(newExperiment, position);
                    }
                    // If it's a new experiment
                    else {

                        // Query the ownerId in Profiles collection
                        CollectionReference profileCollectionReference = db.collection("Experimenter");
                        DocumentReference docRef = profileCollectionReference.document(ownerId);
                        docRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot document = task.getResult();
                                String ownerName;

                                // Get the given name
                                String firestoreOwnerName = document.getString("name");

                                // Check to see if there is a name available
                                if (firestoreOwnerName != null) {
                                    ownerName = firestoreOwnerName;
                                }
                                // Otherwise use userId
                                else{
                                    ownerName = ownerId;
                                }

                                // set the location status
                                locationStatus = experimentLocation.isChecked();
                                Experiment newExperiment = new Experiment(experimentID, title, description, region, minTrials, ownerId, ownerName, status, experimenterIDs, locationStatus, trialType);
                                listener.onOkPressed(newExperiment);
                            }
                        });
                    }
                }).create();
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
