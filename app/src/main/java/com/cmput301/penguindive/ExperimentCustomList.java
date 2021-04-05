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
    Button trials_button;

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
        Button questions_button = convertView.findViewById(R.id.questions_experiment);
        Button trials_button = convertView.findViewById(R.id.trials_experiment);

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

        // This is the OnClickListener for the Trials button
        // It redirects the user to a different activity depending on what Trial Type the experiment is
        trials_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get experiment properties
                Experiment experiment = getItem(position);
                String experimentName = experiment.getTitle();
                String experimentTrialType = experiment.getTrialType();

                // cases for different trial types
                // if the trial type matches, make the intent, put the name in, and redirect
                switch (experimentTrialType) {
                    case "Count Trial": {
                        Intent intent = new Intent(context, CountActivity.class);
                        intent.putExtra("Experiment Name", experimentName);

                        // redirect to activity
                        context.startActivity(intent);
                        break;
                    }
                    case "Binomial Trial": {
                        Intent intent = new Intent(context, BinomialActivity.class);
                        intent.putExtra("Experiment Name", experimentName);

                        // redirect to activity
                        context.startActivity(intent);
                        break;
                    }
                    case "Non-Negative Integer Count Trial": {
                        Intent intent = new Intent(context, NNICActivity.class);
                        intent.putExtra("Experiment Name", experimentName);

                        // redirect to activity
                        context.startActivity(intent);
                        break;
                    }
                    case "Measurement Trial": {
                        Intent intent = new Intent(context, MeasurementActivity.class);
                        intent.putExtra("Experiment Name", experimentName);

                        // redirect to activity
                        context.startActivity(intent);
                        break;
                    }
                    default: {
                        System.out.println("There is no correct trial type set for this experiment!");
                    }
                }
             }
         });

        experimentName.setText(experiment.getTitle());
        description.setText(experiment.getDescription());
        status.setText(experiment.getStatus());
        owner.setText(experiment.getOwnerUserName());

        return convertView;
    }

}

