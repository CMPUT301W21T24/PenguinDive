package com.cmput301.penguindive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

// TODO: Fix this class


// adapter between Count_Trial class and view, not a customlist
public abstract class Count_TrialCustomAdapter implements Adapter {

    // declare object and view
    private Count_Trial countTrial;
    private Context context;

    public Count_TrialCustomAdapter(Count_Trial countTrial, Context context) {
        this.countTrial = countTrial;
        this.context = context;
    }

    // adapt the object and views
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // if convertView doesnt yet exist, inflate content and return its layout file as a view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trial_count_activity, parent, false);
        }

        // the object we need is already countTrial
        // so get the views we want to set values for from the convertView
        TextView experimentName = (TextView) convertView.findViewById(R.id.count_experiment_name);
        TextView countNumber = (TextView) convertView.findViewById(R.id.count_number);

        // set the values of these views with the data from the object
        String stringCount = String.valueOf(countTrial.getCount());  // set to string from int
        countNumber.setText(stringCount);
        // TODO: experimentName.setText();


        return convertView;
    }
}
