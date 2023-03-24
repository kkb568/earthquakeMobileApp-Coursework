package com.example.earthquakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Name: Brian Koome
// Student ID: S2004892
public class SpecificEarthquakeDetails extends AppCompatActivity implements OnMapReadyCallback {

    TextView locationDetails, latitudeDetails, longitudeDetails,
    dateTimeDetails, depthDetails ,magnitudeDetails;
    MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_earthquake_details);
        viewEarthquakeDetails();
        setGoogleMap(savedInstanceState);
    }

    private void viewEarthquakeDetails() {
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

    // Method for setting up the google map to the map view.
    private void setGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    // Method for setting the marker for the specific earthquake location and the zoom ui setting.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        String getLatitude = getIntent().getStringExtra("latitude");
        double lat = Double.parseDouble(getLatitude);
        String getLongitude = getIntent().getStringExtra("latitude");
        double lng = Double.parseDouble(getLongitude);
        String getLocation = getIntent().getStringExtra("location");
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(getLocation));
        UiSettings uis = googleMap.getUiSettings();
        uis.setZoomControlsEnabled(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
