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

public class AnswerCustomList extends ArrayAdapter<Answer> {

    private ArrayList<Answer> answerArrayList;
    private Context context;
    private int resource;

    public AnswerCustomList(@NonNull Context context, int resource, ArrayList<Answer> answerArrayList) {
        super(context, resource, answerArrayList);
        this.answerArrayList = answerArrayList;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.answer_content,parent,false);
        }

        // getting and setting answer
        Answer answer = answerArrayList.get(position);
        TextView answerText = view.findViewById(R.id.answer_text);
        TextView answerUserId = view.findViewById(R.id.answer_poster);
        answerText.setText(answer.getAnswerText());
        answerUserId.setText(answer.getAnswerUserID());
        return view;

    }
}
