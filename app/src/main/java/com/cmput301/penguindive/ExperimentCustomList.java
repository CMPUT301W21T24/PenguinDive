package com.cmput301.penguindive;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This class is a custom list for experiments
 */
public class ExperimentCustomList extends ArrayAdapter<Experiment> {
    private ArrayList<Experiment> experiments;
    private Context context;
    Button questions_button;

    public ExperimentCustomList(Context context, ArrayList<Experiment> experiment) {
        super(context,0,experiment);
        this.context = context;
        this.experiments = experiment;
    }

    @Nullable
    @Override
    public Experiment getItem(int position) {
        return super.getItem(position);
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
        TextView Location = convertView.findViewById(R.id.experiment_locationStatus);
        Button questions_button = convertView.findViewById(R.id.questions_experiment);

        questions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionActivity.class);
                Experiment exp = getItem(position);
                String exp_id = exp.getExperimentId();
                intent.putExtra("EXPID", exp_id);

                context.startActivity(intent);
            }
        });
        experimentName.setText(experiment.getTitle());
        description.setText(experiment.getDescription());
        status.setText(experiment.getStatus());
        owner.setText(experiment.getOwnerUserName());
        if (experiment.getLocationState()){
            Location.setText("ON");
        }else{
            Location.setText("OFF");
        }

        return convertView;
    }

}

