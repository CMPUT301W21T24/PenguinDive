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

public class NNICFragment extends DialogFragment {

    // initialize variables used in the fragment
    EditText fragmentNNI;
    EditText fragmentName;

    // interface to force activities to implement the NNICFragment methods
    interface OnFragmentInteractionListener {

        void AddNNIC_Trial(Non_Negative_Integer_Counts_Trial non_negative_integer_counts_trial);

    }

    // variable called listener to be our context, this will likely be NNICActivity
    private OnFragmentInteractionListener listener;

    //OnAttach to get activity name so can be used on many activities
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // get a reference to the hosting activity in listener, works bc host activity must implement interface
        listener = (OnFragmentInteractionListener) context;
    }

    // new method to pass NNIC objects into the fragment
    static NNICFragment newInstance(Non_Negative_Integer_Counts_Trial NNIC) {
        // make a bundle to actually store object in
        Bundle args = new Bundle();
        // put NNIC in with key "NNIC"
        args.putSerializable("NNIC", NNIC);

        // make a new fragment of this type
        NNICFragment nnicfragment = new NNICFragment();
        // set the fragment arguments to the bundle
        nnicfragment.setArguments(args);
        // return the fragment
        return nnicfragment;
    }

    // override oncreatedialog
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // inflate fragment layout to get the layout files views
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.trial_nnic_fragment_layout, null);

        // get the views from the convertView
        fragmentNNI = convertView.findViewById(R.id.nnic_fragment_nni);
        fragmentName = convertView.findViewById(R.id.nnic_fragment_name);


        // no need to edit, so start building dialog
        // use AlertDialog.Builder to make the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // return the builder with these specified parameters
        return builder
                .setView(convertView)
                .setTitle("Please enter the details for the NNIC Trial.")
                .setNegativeButton("Cancel", null)

                // make an onclicklistener for OK button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // prepare variables to be put in class
                        String name = fragmentName.getText().toString();
                        // try parsing NonNegativeInteger into ints, here just in case user somehow enters nonnumbers
                        try {
                            // try int
                            int NNI = Integer.parseInt(fragmentNNI.getText().toString());

                            // now use listener to run NNICActivity's Add Trial method and pass NNIC to activity
                            listener.AddNNIC_Trial(new Non_Negative_Integer_Counts_Trial(NNI, name));

                        }

                        // if cannot parse input (because the non negative integer is wrong), catch exception
                        catch (NumberFormatException exception) {
                            System.out.println("Please enter correct numbers! Incorrect number: " + exception);
                        }
                    }
                })

                .create();  // make the dialog
    }
}
