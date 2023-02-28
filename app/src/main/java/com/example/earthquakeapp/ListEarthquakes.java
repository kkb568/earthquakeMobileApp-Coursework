package com.example.earthquakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListEarthquakes extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_earthquakes);

        ListView listEarthquakes = findViewById(R.id.listEarthquakes);
        Intent i = getIntent();
        ArrayList<String> titles = i.getStringArrayListExtra("Titles");
        ArrayAdapter<String> arr = new ArrayAdapter<>(this, R.layout.list_earthquakes_text_view, titles);
        listEarthquakes.setAdapter(arr);
    }
}
