package com.cmput301.penguindive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ExampleAdapter extends BaseAdapter {

    // Declare Variables

    Context context;
    LayoutInflater inflater;
    private ArrayList<Example> exampleArrayList;

    public ExampleAdapter(Context context ) {
        context = context;
        inflater = LayoutInflater.from(context);
        this.exampleArrayList = new ArrayList<Example>();
        this.exampleArrayList.addAll(MainActivity.exampleArrayList);
    }

    public class ViewHolder {
        TextView username;
        TextView title;
        TextView status;
        TextView description;
        TextView owner;
    }

    @Override
    public int getCount() {
        return exampleArrayList.size();
    }

    @Override
    public Example getItem(int position) {
        return MainActivity.exampleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.search_content2, null);
            // Locate the TextViews in search_content xml
            holder.username=(TextView) view.findViewById(R.id.userName_text);
            holder.status=(TextView) view.findViewById(R.id.experiment_status);
            holder.title=(TextView) view.findViewById(R.id.experiment_name);
            holder.description=(TextView) view.findViewById(R.id.experiment_description);
            holder.owner=(TextView) view.findViewById(R.id.experiment_owner);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.username.setText(MainActivity.exampleArrayList.get(position).getUserName());
        holder.status.setText(MainActivity.exampleArrayList.get(position).getStatus());
        holder.description.setText(MainActivity.exampleArrayList.get(position).getDescription());
        holder.title.setText(MainActivity.exampleArrayList.get(position).getTitle());
        holder.owner.setText(MainActivity.exampleArrayList.get(position).getOwner());

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        MainActivity.exampleArrayList.clear();
        if (charText.length() == 0) {
            MainActivity.exampleArrayList.addAll(exampleArrayList);
        } else {
            for (Example current : exampleArrayList) {
                if (current.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    MainActivity.exampleArrayList.add(current);
                }
            }
        }
        notifyDataSetChanged();
    }

}