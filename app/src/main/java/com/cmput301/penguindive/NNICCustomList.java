package com.cmput301.penguindive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NNICCustomList extends ArrayAdapter<Non_Negative_Integer_Counts_Trial> {

    // define NNIC list and context
    private ArrayList<Non_Negative_Integer_Counts_Trial> NNICArrayList;
    private Context context;

    // constructor
    public NNICCustomList(Context context, ArrayList<Non_Negative_Integer_Counts_Trial> NNICArrayList) {
        super(context, 0, NNICArrayList);
        this.NNICArrayList = NNICArrayList;
        this.context = context;
    }

    // getview to adapt views and data
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // if convertView doesnt yet exist, inflate content and return its layout file as a view
        // will be reused for every item in the list
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trial_nnic_layout, parent, false);
        }

        // get object from list
        Non_Negative_Integer_Counts_Trial NNIC = NNICArrayList.get(position);

        // get the views to set values for from the convertView
        TextView nniView = (TextView) convertView.findViewById(R.id.nnic_nni);
        TextView nnicNameView = (TextView) convertView.findViewById(R.id.nnic_name);

        // set the value of the view with the object
        String nniString = String.valueOf(NNIC.getNonNegativeInteger());
        String nnicName = NNIC.getNNICName();
        nniView.setText(nniString);
        nnicNameView.setText(nnicName);

        // return
        return convertView;

    }
}
