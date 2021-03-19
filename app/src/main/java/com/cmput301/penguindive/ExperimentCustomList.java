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

public class ExperimentCustomList extends ArrayAdapter<Experiment> {
    private ArrayList<Experiment> experiments;
    private Context context;

    public ExperimentCustomList(Context context, ArrayList<Experiment> experiment) {
        super(context,0,experiment);
        this.context = context;
        this.experiments = experiment;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.experiment_row, parent, false);
        }

        Experiment experiment = experiments.get(position);

        TextView experimentName = convertView.findViewById(R.id.experiment_name);
        TextView description = convertView.findViewById(R.id.experiment_description);
        TextView status = convertView.findViewById(R.id.experiment_status);
        TextView owner = convertView.findViewById(R.id.experiment_owner);

        experimentName.setText(experiment.getTitle());
        description.setText(experiment.getDescription());
        status.setText(experiment.getStatus());
        owner.setText(experiment.getOwnerUserName());

        return convertView;
    }

}

