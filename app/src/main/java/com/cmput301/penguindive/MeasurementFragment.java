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

public class MeasurementFragment extends DialogFragment {

    // initialize variables used in the fragment
    EditText fragmentMeasurementDouble;
    EditText fragmentMeasurementName;

    // interface to force activities to implement the MeasurementFragment methods
    interface OnFragmentInteractionListener {

        void AddMeasurement_Trial(Measurement_Trial measurement_trial);

    }

    // variable called listener to be our context, this will likely be MeasurementActivity
    private OnFragmentInteractionListener listener;

    //OnAttach to get activity name so can be used on many activities
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // get a reference to the hosting activity in listener, works bc host activity must implement interface
        listener = (OnFragmentInteractionListener) context;
    }

    // new method to pass Measurement objects into the fragment
    static MeasurementFragment newInstance(Measurement_Trial measurement_trial) {
        // make a bundle to actually store object in
        Bundle args = new Bundle();
        // put measurement_trial object in with key "MeasurementTrial"
        args.putSerializable("MeasurementTrial", measurement_trial);

        // make a new fragment of this type
        MeasurementFragment measurementfragment = new MeasurementFragment();
        // set the fragment arguments to the bundle
        measurementfragment.setArguments(args);
        // return the fragment
        return measurementfragment;
    }

    // override oncreatedialog
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // inflate fragment layout to get the layout files views
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.trial_measurement_fragment_layout, null);

        // get the views from the convertView
        fragmentMeasurementDouble = convertView.findViewById(R.id.measurement_fragment_double);
        fragmentMeasurementName = convertView.findViewById(R.id.measurement_fragment_name);

        // no need to edit, so start building dialog
        // use AlertDialog.Builder to make the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // return the builder with these specified parameters
        return builder
                .setView(convertView)
                .setTitle("Please enter the details for the Measurement Trial.")
                .setNegativeButton("Cancel", null)

                // make an onclicklistener for OK button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // prepare variables to be put in class
                        String name = fragmentMeasurementName.getText().toString();
                        // try parsing Measurement into double, here just in case user somehow enters nonnumbers
                        try {
                            // try int
                            Double measurement = Double.parseDouble((fragmentMeasurementDouble.getText().toString()));

                            // now use listener to run NNICActivity's Add Trial method and pass NNIC to activity
                            listener.AddMeasurement_Trial(new Measurement_Trial(measurement, name));

                        }

                        // if cannot parse input (because the double is wrong), catch exception
                        catch (NumberFormatException exception) {
                            System.out.println("Please enter correct numbers! Incorrect number: " + exception);
                        }
                    }
                })

                .create();  // make the dialog
    }
}
