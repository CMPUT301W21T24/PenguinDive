package com.cmput301.penguindive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private ListView list;
    private SearchView SearchContent;
    private ExampleAdapter adapter;
    public static ArrayList<Example> exampleArrayList = new ArrayList<Example>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list.setAdapter(adapter);

        Example e = new Example("user", "owner!", "this is a desc", "big title", "active");

        list = (ListView) findViewById(R.id.listView);

        exampleArrayList.add(e);

        SearchContent = (SearchView) findViewById(R.id.searchLayout);
        SearchContent.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }


}