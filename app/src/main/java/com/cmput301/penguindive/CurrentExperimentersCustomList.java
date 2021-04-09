package com.cmput301.penguindive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CurrentExperimentersCustomList extends ArrayAdapter<String> {
    private ArrayList<String> experimentersArrayList;
    private Context context;

    public CurrentExperimentersCustomList(Context context, ArrayList<String> experimentersArrayList) {
        super(context, 0, experimentersArrayList);
        this.experimentersArrayList = experimentersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.current_experimenters_layout, parent, false);
        }
    }
}
