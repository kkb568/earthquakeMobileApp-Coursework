package com.example.earthquakeapp;

import android.content.Intent;
import android.os.Bundle;
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
        TitleAdapter titleAdapter = new TitleAdapter(this, titles);
        listEarthquakes.setAdapter(titleAdapter);
    }
}
