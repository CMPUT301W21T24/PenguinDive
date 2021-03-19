package com.example.trialstuff03_11_2021;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class AddTrialFragment extends DialogFragment {

    // initialize variables used in fragment
    EditText fragmentNumTrials;

    // interface to force activities to implement it and these methods
    interface OnFragmentInteractionListener {
        // TODO: make whichever activity implements this include these methods
        void AddTrial(Trial newTrial);
        void EditTrial(Trial trial, int numTrials);
    }

    // make variable called listener, this will be our context (here, it is MainActivity)
    private OnFragmentInteractionListener listener;

    //OnAttach to get activity name so can be used on many activities
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // get a reference to the hosting activity in listener, works bc host activity must implement interface
        listener = (OnFragmentInteractionListener) context;
    }


    // new method to pass Trial objects into the fragment
    static AddTrialFragment newInstance(Trial trial) {
        // make a bundle to actually store object in
        Bundle args = new Bundle();
        // put Trial in with key "Trial"
        args.putSerializable("Trial", trial);

        // make a new fragment of this type
        AddTrialFragment fragment = new AddTrialFragment();
        // set the fragment arguments to the bundle
        fragment.setArguments(args);
        // return the fragment
        return fragment;
    }


    // override onCreateDialog
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // inflate fragment layout to get the fragment's views
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.add_trial_fragment_layout, null);

        // use the convertView to get the views and set the edittexts to them
        fragmentNumTrials = convertView.findViewById(R.id.fragment_num_trials);

        // for editing an existing Trial, get the object/arguments from the bundle
        Bundle argumentBundle = getArguments();

        // view selected Trial details
        // if arguments exist (check if we're editing or not)
        if (argumentBundle != null) {

            // get the arguments from the bundle
            Trial trial = (Trial) argumentBundle.getSerializable("Trial");
            int numTrials = trial.getNumberOfTrials();

            // set the values of the edittexts to the preexisting argument values
            // convert these to strings
            fragmentNumTrials.setText(String.valueOf(numTrials), TextView.BufferType.EDITABLE);
        }


        // use AlertDialog.Builder to make the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // return the builder with these specified parameters
        return builder
                .setView(convertView)
                .setTitle("Please enter the details for this Trial.")
                .setNegativeButton("Cancel", null)

                // make an onclicklistener for OK button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // prepare variables to be put in class, we have none here
                        // try parsing these strings into ints, here just in case user somehow enters nonnumbers
                        try {
                            // try ints
                            int numTrials = Integer.parseInt(fragmentNumTrials.getText().toString());

                            // if we're editing
                            if (argumentBundle != null) {
                                // get Trial through bundle getting the Trial we set earlier, casted to Trial type
                                Trial trial = (Trial) argumentBundle.getSerializable("Trial");
                                // call EditTrial from mainactivity which sets the trial's values to these
                                listener.EditTrial(trial, numTrials);
                            }
                            // if we're not editing (adding)
                            else {
                                // now use listener to run mainactivity's AddTrial method and pass Trial to activity
                                listener.AddTrial(new Trial());
                            }

                        }

                        // if cannot parse input, catch exception
                        catch (NumberFormatException exception) {
                            System.out.println("Please enter correct numbers! Incorrect number: " + exception);
                        }

                    }
                })
                .create();  // make the dialog
    }
}

