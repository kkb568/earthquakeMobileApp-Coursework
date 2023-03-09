package com.example.earthquakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

// Name: Brian Koome
// Student ID: S2004892
public class ListEarthquakes extends AppCompatActivity {

    ArrayList<Earthquake> earthquakes = MainActivity.items;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_earthquakes);

        ListView listEarthquakes = findViewById(R.id.listEarthquakes);
        Intent i = getIntent();
        ArrayList<String> titles = i.getStringArrayListExtra("Titles");
        TitleAdapter titleAdapter = new TitleAdapter(this, titles);
        listEarthquakes.setAdapter(titleAdapter);

        listEarthquakes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemTitle = (String)parent.getItemAtPosition(position);
                for (Earthquake data : earthquakes) {
                    if (data.getTitle().equals(itemTitle)) {
                        Intent i = new Intent(ListEarthquakes.this, SpecificEarthquakeDetails.class);
                        i.putExtra("location", getLocation(data.getDescription()));
                        i.putExtra("latitude", data.getLatitude());
                        i.putExtra("longitude", data.getLongitude());
                        i.putExtra("date/time", data.getPublishedDate());
                        i.putExtra("depth", getDepth(data.getDescription()));
                        i.putExtra("magnitude", getMagnitude(data.getDescription()));
                        startActivity(i);
                    }
                }
            }
        });
    }

    // Method for extracting the location information from the description data part.
    private String getLocation (String desc) {
        // Split the whole description information using the specified delimiter ' ; '.
        String[] descContent = desc.split(" ; ");
        // Split the specific location information (for example, Location: BISMARCK SEA) into the label section and the value section.
        String[] location = descContent[1].split(": ");
        return location[1]; // That is, 'BISMARCK SEA'.
    }

    // Method for extracting the depth information from the description data part.
    private String getDepth(String desc) {
        // Split the whole description information using the specified delimiter ' ; '.
        String[] descContent = desc.split(" ; ");
        // Split the specific depth information (for example, Depth: 598 km) into the label section and the value section.
        String[] depth = descContent[3].split(": ");
        // Split the value section (598 km) into the number and the sign ('km') section.
        String[] depthValue = depth[1].split(" ");
        return depthValue[0]; // That is '598'.
    }

    // Method for extracting the magnitude information from the description data part.
    private String getMagnitude(String desc) {
        // Split the whole description information using the specified delimiter ' ; '.
        String[] descContent = desc.split(" ; ");
        // Split the specific magnitude information (for example, Magnitude: 6.5) into the label section and the value section.
        String[] magnitude = descContent[4].split(": ");
        return magnitude[1]; // That is, 6.5
    }
}