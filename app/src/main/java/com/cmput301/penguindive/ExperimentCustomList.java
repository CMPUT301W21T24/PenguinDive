package com.cmput301.penguindive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ExperimentCustomList extends BaseAdapter {
    private ArrayList<Experiment> experiment;
    private Context context;

    public ExperimentCustomList(Context newContext) {
        context = newContext;
        this.experiment = new ArrayList<Experiment>();
        this.experiment.addAll(MainActivity.experimentDataList);
    }
    public class ViewHolder{
        TextView titleText;
        TextView descriptionText;
        TextView ownerText;
        TextView statusText;
    }
    @Override
    public int getCount() {
        return MainActivity.experimentDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return MainActivity.experimentDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.experiment_row,parent,false);
            holder.titleText = (TextView) view.findViewById(R.id.experiment_name);
            holder.descriptionText = (TextView) view.findViewById(R.id.experiment_description);;
            holder.ownerText = (TextView) view.findViewById(R.id.experiment_owner);;
            holder.statusText = (TextView) view.findViewById(R.id.experiment_status);;
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        holder.titleText.setText(MainActivity.experimentDataList.get(position).getTitle());
        holder.descriptionText.setText(MainActivity.experimentDataList.get(position).getDescription());
        holder.ownerText.setText(MainActivity.experimentDataList.get(position).getOwnerUserName());
        holder.statusText.setText(MainActivity.experimentDataList.get(position).getStatus());
        return view;
    }

    // Filters what is to be shown in the experimentDataList based on user search
    public void filter(String charText) {
        // Get lowercase input
        charText = charText.toLowerCase(Locale.getDefault());
        // Clear list
        MainActivity.experimentDataList.clear();
        // If no input show all results
        if (charText.length() == 0) {
            MainActivity.experimentDataList.addAll(experiment);
        }
        // If there is an input
        else {
            // Cycle all experiments
            for (Experiment current : experiment) {

                // Search Title, description and owner for matches
                if (current.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    MainActivity.experimentDataList.add(current);
                } else if (current.getDescription().toLowerCase(Locale.getDefault()).contains(charText)) {
                    MainActivity.experimentDataList.add(current);
                } else if (current.getOwnerUserName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    MainActivity.experimentDataList.add(current);
                }
            }
        }
        notifyDataSetChanged();
    }
}

