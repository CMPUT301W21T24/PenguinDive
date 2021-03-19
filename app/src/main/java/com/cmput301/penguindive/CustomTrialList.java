package com.example.trialstuff03_11_2021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// This class serves as an adapter between the views for the custom array list (content.xml)
// and the values to be used in the array list (code from Trial, MainActivity, etc)
public class CustomTrialList extends ArrayAdapter<Trial> {

    // define Trial list and context
    private ArrayList<Trial> TrialArrayList;
    private Context context;

    // constructor
    public CustomTrialList(Context context, ArrayList<Trial> TrialArrayList) {
        super(context, 0, TrialArrayList);
        this.TrialArrayList = TrialArrayList;
        this.context = context;
    }

    // getview lets us get views and set values for the views
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // if convertView doesnt yet exist, inflate content and return its layout file as a view
        // will be reused for every item in the list
        if (convertView == null) {
            //convertView = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }

        // get object (we need this to set the values) from TrialArrayList
        Trial trial = TrialArrayList.get(position);


        // get the views we want to set values for from the convertView
        TextView numTrials = (TextView) convertView.findViewById(R.id.numtrials_view);

        // set the values of these views with the data from the item
        numTrials.setText(trial.getNumberOfTrials());
        // convert to string with message if needed later

        // return
        return convertView;

    }
}
