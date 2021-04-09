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


public class QuestionCustomList extends ArrayAdapter<Question> {

    private ArrayList<Question> questionArrayList;
    private Context context;
    private int resource;

    public QuestionCustomList( Context context, int resource, ArrayList<Question> questionArrayList) {
        super(context, resource, questionArrayList);
        this.questionArrayList = questionArrayList;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.question_content,parent,false);
        }

        Question question = questionArrayList.get(position);
        TextView questionText = view.findViewById(R.id.question_text);
        TextView questionUserId = view.findViewById(R.id.question_poster);
        questionText.setText(question.getQuestionTitle());
        questionUserId.setText(question.getQuestionUserId());

        return view;
    }
}
