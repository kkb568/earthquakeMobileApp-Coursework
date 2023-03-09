package com.example.earthquakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

// Name: Brian Koome
// Student ID: S2004892
public class FragmentSouth extends Fragment {

    ArrayList<Earthquake> eqs = MainActivity.items;
    TextView dataDisplayNearestSouth;
    Button nearestSouthContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflating the layout of the fragment
        // and returning the view component
        View view = inflater.inflate(R.layout.fragment_southern, container, false);
        dataDisplayNearestSouth = view.findViewById(R.id.dataDisplayNearestSouth);
        nearestFromMauritius(dataDisplayNearestSouth);

        nearestSouthContent = view.findViewById(R.id.nearestSouthContent);
        nearestSouthContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDetailedContent(dataDisplayNearestSouth);
            }
        });
        return view;
    }

    // Method for getting nearest earthquake from Mauritius in all four directions (North, South, East and West).
    public void nearestFromMauritius(TextView textView) {
        // Latitude value of Mauritius.
        double latMauritius = -20.3484;
        // Array for all earthquakes south of Mauritius.
        ArrayList<Earthquake> southEarthquakes = new ArrayList<>();

        for (Earthquake eq : eqs) {
            double latitude = Double.parseDouble(eq.getLatitude());
            // Add earthquakes whose latitude value is less than latMauritius to southEarthquakes.
            if (latitude < latMauritius) {
                southEarthquakes.add(eq);
            }
        }

        // The earthquake with the largest latitude value from the southEarthquakes array is the nearest from the south.
        getLargestLatitudeValue(southEarthquakes, textView);
    }

    private void getLargestLatitudeValue(ArrayList<Earthquake> eq, TextView textView) {
        // Get the length of the earthquake array.
        int lengthArray = eq.size();
        // Check if the array has no earthquake instance.
        if (lengthArray > 0) {
            // Check if the array has only one earthquake instance.
            if (lengthArray == 1) {
                Earthquake eq1 = eq.get(0);
                textView.setText(eq1.getTitle());
            }
            // Done if the array has more than one earthquake instance.
            else {
                double maxLatitudeValue = Double.parseDouble(eq.get(0).getLatitude());
                // Loop to find the maximum latitude value from arraylist.
                for (int i = 1; i < lengthArray; i++) {
                    double latValue = Double.parseDouble(eq.get(i).getLatitude());
                    if (latValue > maxLatitudeValue) {
                        maxLatitudeValue = latValue;
                        textView.setText(eq.get(i).getTitle());
                    }
                }
            }
        }
    }

    // The method below opens a new page showing the detailed earthquake information on the specific_earthquake_details activity.
    // This is done by creating a new intent instance and starting the activity through the new instance.
    // For example, when the user clicks on 'View More' button below the largest magnitude earthquake information on the main activity,
    // the method looks through which earthquake instance contains the title as same as the information and when found,
    // it gets the other information about the earthquake, such as depth and magnitude, and inserts it to the specific_earthquake_details activity.
    public void viewDetailedContent(TextView tv) {
        String title = tv.getText().toString();
        boolean foundEarthquake = false;
        for (Earthquake data : eqs) {
            if (data.getTitle().equals(title)) {
                foundEarthquake = true;
                Intent i = new Intent(getActivity(), SpecificEarthquakeDetails.class);
                i.putExtra("location", getLocation(data.getDescription()));
                i.putExtra("latitude", data.getLatitude());
                i.putExtra("longitude", data.getLongitude());
                i.putExtra("date/time", data.getPublishedDate());
                i.putExtra("depth", getDepth(data.getDescription()));
                i.putExtra("magnitude", getMagnitude(data.getDescription()));
                startActivity(i);
            }
        }
        if (!foundEarthquake) {
            String notFoundMessage = "No earthquake to display.";
            Toast.makeText(getContext(), notFoundMessage, Toast.LENGTH_LONG).show();
        }
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