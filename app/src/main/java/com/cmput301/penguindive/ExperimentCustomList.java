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
import java.util.Locale;

public class ExperimentCustomList extends ArrayAdapter<Experiment> {
    private ArrayList<Experiment> experiments;
    private Context context;

    public ExperimentCustomList(Context context, ArrayList<Experiment> experiment) {
        super(context,0,experiment);
        this.context = context;
        this.experiments = experiment;
    }
    public class ViewHolder{
        TextView titleText;
        TextView descriptionText;
        TextView ownerText;
        TextView statusText;
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

    // Filters what is to be shown in the experimentDataList based on user search
//    public void filter(String charText) {
//        // Get lowercase input
//        charText = charText.toLowerCase(Locale.getDefault());
//        // Clear list
//        MainActivity.experimentDataList.clear();
//        // If no input show all results
//        if (charText.length() == 0) {
//            MainActivity.experimentDataList.addAll(experiments);
//        }
//        // If there is an input
//        else {
//            // Cycle all experiments
//            for (Experiment current : experiments) {
//
//                // Search Title, description and owner for matches
//                if (current.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    MainActivity.experimentDataList.add(current);
//                } else if (current.getDescription().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    MainActivity.experimentDataList.add(current);
//                } else if (current.getOwnerUserName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    MainActivity.experimentDataList.add(current);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
}

