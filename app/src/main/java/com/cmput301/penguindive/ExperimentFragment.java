package com.cmput301.penguindive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ExperimentFragment extends DialogFragment {
    private EditText experimentTitle;
    private EditText experimentDescription;
    private EditText experimentRegion;
    private OnFragmentInteractionListener listener;
    private Experiment experiment;
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


        Bundle bundle = getArguments();
        if (bundle != null) {
            experiment = (Experiment) bundle.getSerializable("experiment");
            position = (int) bundle.getSerializable("pos");
            experimentTitle.setText(experiment.getTitle());
            experimentDescription.setText(experiment.getDescription());
            experimentRegion.setText(experiment.getRegion());
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
                        String title = experimentTitle.getText().toString();
                        String description = experimentDescription.getText().toString();
                        String region = experimentRegion.getText().toString();
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
                        if(experiment != null){
                            listener.onEditPressed(new Experiment(title,description,region,totalTrail,ownerUserName), position);
                        }
                        else{
                            listener.onOkPressed(new Experiment(title,description,region,totalTrail,ownerUserName));
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
