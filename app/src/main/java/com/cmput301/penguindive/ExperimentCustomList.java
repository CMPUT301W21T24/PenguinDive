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

public class ExperimentCustomList extends ArrayAdapter<Experiment>{
    private ArrayList<Experiment> experiment;
    private Context context;

    public ExperimentCustomList(Context context, ArrayList<Experiment> experiments) {
        super(context,0,experiments);
        this.experiment = experiments;
        this.context = context;
    }

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.experiment_content,parent,false);
        }

        Experiment experiments = experiment.get(position);

        TextView title = view.findViewById(R.id.titleText);
        TextView description = view.findViewById(R.id.descriptionText);
        TextView region = view.findViewById(R.id.regionText);
        TextView totalTrail = view.findViewById(R.id.countText);
        TextView ownerUserName = view.findViewById(R.id.ownerUserNameText);

        title.setText(experiments.getTitle());
        description.setText(experiments.getDescription());
        region.setText(experiments.getRegion());
        totalTrail.setText(experiments.getTotalTrail());
        ownerUserName.setText(experiments.getOwnerUserName());

        return view;
    }
}
