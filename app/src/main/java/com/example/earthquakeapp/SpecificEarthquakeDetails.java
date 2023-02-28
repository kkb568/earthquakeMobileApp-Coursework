package com.example.earthquakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SpecificEarthquakeDetails extends AppCompatActivity {

    TextView locationDetails, latitudeDetails, longitudeDetails,
    dateTimeDetails, depthDetails ,magnitudeDetails;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_earthquake_details);

        locationDetails = findViewById(R.id.locationDetails);
        latitudeDetails = findViewById(R.id.latitudeDetails);
        longitudeDetails = findViewById(R.id.longitudeDetails);
        dateTimeDetails = findViewById(R.id.dateTimeDetails);
        depthDetails = findViewById(R.id.depthDetails);
        magnitudeDetails = findViewById(R.id.magnitudeDetails);

        Intent i = getIntent();
        String getLocation = i.getStringExtra("location");
        String getLatitude = i.getStringExtra("latitude");
        String getLongitude = i.getStringExtra("longitude");
        String getDateTime = i.getStringExtra("date/time");
        String getDepth = i.getStringExtra("depth");
        String getMagnitude = i.getStringExtra("magnitude");

        locationDetails.setText(String.format("Location: %s", getLocation));
        latitudeDetails.setText(String.format("Latitude: %s°", getLatitude));
        longitudeDetails.setText(String.format("Longitude: %s°", getLongitude));
        dateTimeDetails.setText(String.format("Date and time: %s", getDateTime));
        depthDetails.setText(String.format("Depth: %s km", getDepth));
        magnitudeDetails.setText(String.format("Magnitude: %s", getMagnitude));
    }
}
