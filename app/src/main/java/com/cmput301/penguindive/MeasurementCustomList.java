package com.cmput301.penguindive;

import android.content.Context;
import android.icu.util.Measure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MeasurementCustomList extends ArrayAdapter<Measurement_Trial> {

    // define measurement list and context
    private ArrayList<Measurement_Trial> MeasurementArrayList;
    private Context context;

    // constructor
    public MeasurementCustomList(Context context, ArrayList<Measurement_Trial> MeasurementArrayList) {
        super(context, 0, MeasurementArrayList);
        this.MeasurementArrayList = MeasurementArrayList;
        this.context = context;
    }

    // getview to adapt views and data
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // if convertView doesnt yet exist, inflate content and return its layout file as a view
        // will be reused for every item in the list
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trial_measurement_layout, parent, false);
        }

        // get object from list
        Measurement_Trial measurementTrial = MeasurementArrayList.get(position);

        // get the view to set value for from the convertView
        TextView measurementView = (TextView) convertView.findViewById(R.id.measurement_double);
        TextView measurementNameView = (TextView) convertView.findViewById(R.id.measurement_name);

        // set the value of the view with the object
        String measurementString = String.valueOf(measurementTrial.getMeasurement());
        String measurementNameString = measurementTrial.getMeasurementName();

        measurementView.setText(measurementString);
        measurementNameView.setText(measurementNameString);

        // return
        return convertView;

    }
}
