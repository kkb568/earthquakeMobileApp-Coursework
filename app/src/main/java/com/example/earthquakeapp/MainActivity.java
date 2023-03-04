package com.example.earthquakeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

// Name: Brian Koome
// Student ID: S2004892
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText date;
    private TextView dataDisplayMagnitude, dataDisplayDepth;
    private Button largestMagnitudeContent, deepestEarthquakeContent, searchByLocation, searchByDate;
    private Spinner spinner;
    ArrayList<Earthquake> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataDisplayMagnitude = findViewById(R.id.dataDisplayMagnitude);
        dataDisplayDepth = findViewById(R.id.dataDisplayDepth);
        date = findViewById(R.id.date);
        date.setOnClickListener(this);

        largestMagnitudeContent = findViewById(R.id.largestMagnitudeContent);
        largestMagnitudeContent.setOnClickListener(this);

        deepestEarthquakeContent = findViewById(R.id.deepestEarthquakeContent);
        deepestEarthquakeContent.setOnClickListener(this);

        searchByLocation = findViewById(R.id.searchByLocation);
        searchByLocation.setOnClickListener(this);

        searchByDate = findViewById(R.id.searchByDate);
        searchByDate.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.directions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.directionSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent == spinner) {
                    String text = spinner.getSelectedItem().toString();
                    showFragment(text);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.e("MyTag","in onClick");
        String urlSource = "https://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
        new Thread(new Task(urlSource)).start();
        Log.e("MyTag","after startProgress");
    }

    private class Task implements Runnable
    {
        private final String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            InputStream stream = null;
            Earthquake eq = new Earthquake();

            Log.e("MyTag","in run");

            try
            {
                // The following involves connecting to the xml link and creating
                // an input stream for the data found from the xml link.
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                yc.connect();
                stream = yc.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(stream));
                int eventType = parser.getEventType();
                Log.e("MyTag","after ready");
                //
                // Now read the data. Make sure that there are no specific headers
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                //
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(eventType == XmlPullParser.START_TAG) {
                        switch (parser.getName()) {
                            case "item":
                                eq = new Earthquake();
                                break;
                            case "title":
                                eq.setTitle(parser.nextText());
                                break;
                            case "description":
                                eq.setDescription(parser.nextText());
                                break;
                            case "pubDate":
                                eq.setPublishedDate(parser.nextText());
                                break;
                            case "geo:lat":
                                eq.setLatitude(parser.nextText());
                                break;
                            case "geo:long":
                                eq.setLongitude(parser.nextText());
                                break;
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG) {
                        if (parser.getName().equalsIgnoreCase("item")) {
                            items.add(eq);
                            Log.e("MyTag", "Object added.");
                        }
                    }
                    eventType = parser.next();
                }
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception in run");
            } catch (XmlPullParserException e) {
                Log.e("MyTag", "xmlPullParserException in run");
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    showLargestMagnitude(items);
                    showDeepest(items);
                }
            });
        }

        // The method below shows the earthquake with the largest magnitude from the extracted data from the xml file.
        private void showLargestMagnitude(ArrayList<Earthquake> items) {
            ArrayList<Double> magnitudeValues = new ArrayList<>();
            // Extract the magnitude information, of type double, from the description section of the earthquake object and add to an arraylist.
            for (Earthquake data : items) {
                double magnitude = Double.parseDouble(getMagnitude(data.getDescription()));
                magnitudeValues.add(magnitude);
            }
            // Check the highest magnitude value.
            double highestMagnitude = Collections.max(magnitudeValues);
            // Check the earthquake object from the items array with the highest magnitude value
            // and then display it as text for the dataDisplay text.
            for (Earthquake data1 : items) {
                double magnitudeValue1 = Double.parseDouble(getMagnitude(data1.getDescription()));
                if (magnitudeValue1 == highestMagnitude) {
                    dataDisplayMagnitude.setText(String.format("%s", data1.getTitle()));
                }
            }
        }

        private void showDeepest(ArrayList<Earthquake> items) {
            ArrayList<Double> depthValues = new ArrayList<>();
            // Extract the depth information, of type double, from the description section of the earthquake object and add to an arraylist.
            for (Earthquake data : items) {
                double depthValue = Double.parseDouble(getDepth(data.getDescription()));
                depthValues.add(depthValue);
            }
            // Check the highest depth value.
            double highestDepth = Collections.max(depthValues);
            // Check the earthquake object from the items array with the highest depth value
            // and then display it as text for the dataDisplay text.
            for (Earthquake data1 : items) {
                double depthValue1 = Double.parseDouble(getDepth(data1.getDescription()));
                if (depthValue1 == highestDepth) {
                    dataDisplayDepth.setText(String.format("%s", data1.getTitle()));
                }
            }
        }
    }

    public void onClick(View v) {
        if(v == date) {
            setCalendar();
        }
        else if (v == largestMagnitudeContent) {
            viewDetailedContent(dataDisplayMagnitude);
        }

        else if (v == deepestEarthquakeContent) {
            viewDetailedContent(dataDisplayDepth);
        }
        else if (v == searchByLocation) {
            displayEarthquakeByLocation();
        }
        else if (v == searchByDate) {
            displayEarthquakeByDate();
        }
    }

    // Used to show the calendar when user searches for earthquake by date
    // and showcasing the chosen date in the specified format to the date textview.
    public void setCalendar() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(
                MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = String.format(Locale.ENGLISH,"%d/%d/%d", dayOfMonth, month + 1, year);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        try {
                            Date stringDate = formatter.parse(selectedDate);
                            SimpleDateFormat formatter1 = new SimpleDateFormat("E, dd MMM yyyy", Locale.ENGLISH);
                            String stringDate1 = formatter1.format(stringDate);
                            date.setText(stringDate1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                year,month,day);
        dpd.show();
    }

    // The method below opens a new page showing the detailed earthquake information on the specific_earthquake_details activity.
    // This is done by creating a new intent instance and starting the activity through the new instance.
    // For example, when the user clicks on 'View More' button below the largest magnitude earthquake information on the main activity,
    // the method looks through which earthquake instance contains the title as same as the information and when found,
    // it gets the other information about the earthquake, such as depth and magnitude, and inserts it to the specific_earthquake_details activity.
    public void viewDetailedContent(TextView tv) {
        String title = tv.getText().toString();
        for (Earthquake data : items) {
            if (data.getTitle().equals(title)) {
                Intent i = new Intent(MainActivity.this, SpecificEarthquakeDetails.class);
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

    // Method for showcasing the listview page of earthquakes that have the same location as the one inserted by the user on the 'Search earthquake by location' edittext view.
    public void displayEarthquakeByLocation() {
        EditText insertedLocation = findViewById(R.id.earthquakeLocation);
        // Convert input to string, with whitespaces at the end of the string already removed.
        String location = insertedLocation.getText().toString().stripTrailing();
        // Create new arraylist of type string.
        ArrayList<String> titles = new ArrayList<>();
        // Check if the input is an empty string.
        // If the input is empty, send an error message and break the method from continuing.
        if (location.equals("")) {
            String nullMessage = "Please enter a location.";
            Toast.makeText(getApplicationContext(), nullMessage, Toast.LENGTH_LONG).show();
            return;
        }
        // Variable used to indicate if any earthquake is found.
        boolean foundLocation = false;
        for (Earthquake data : items) {
            // Get the location details from the description part for each earthquake instance.
            String dataLocation = getLocation(data.getDescription());
            // If the location details is same as the input, the instance with the details is added to the arraylist, which is added to the Intent instance
            // and the foundLocation is set to true.
            if (location.equalsIgnoreCase(dataLocation)) {
                foundLocation = true;
                titles.add(data.getTitle());
            }
        }
        // Check if the foundLocation is still false after looping through all earthquake instances in the items arraylist.
        if (!foundLocation) {
            String notFoundMessage = "Earthquake not found.";
            Toast.makeText(getApplicationContext(), notFoundMessage, Toast.LENGTH_LONG).show();
            return;
        }
        // The Intent instance is then used to open a new activity showing list of all earthquakes with the same location details as the input.
        Intent i = new Intent(MainActivity.this, ListEarthquakes.class);
        i.putStringArrayListExtra("Titles", titles);
        startActivity(i);
    }

    // Method for showcasing the listview page of earthquakes that have the same location as the one inserted by the user on the 'Search earthquake by date' edittext view.
    public void displayEarthquakeByDate() {
        EditText insertedDate = findViewById(R.id.date);
        // Convert input to string.
        String date = insertedDate.getText().toString();
        // Check if the input is an empty string.
        // If the input is empty, send an error message and break the method from continuing.
        if (date.equals("")) {
            String nullMessage = "Please enter a date.";
            Toast.makeText(getApplicationContext(), nullMessage, Toast.LENGTH_LONG).show();
            return;
        }
        // Create new arraylist of type string
        ArrayList<String> titles = new ArrayList<>();
        // Variable used to indicate if any earthquake is found.
        boolean foundDate = false;
        for (Earthquake data1 : items) {
            String publishedDate = data1.getPublishedDate();
            // Extract the date part (without the hour, minute and second part) from the publishedDate field for each earthquake instance.
            String earthquakeDate = publishedDate.substring(0, 16);
            // If the date part is same as the input, the instance with the date part is added to the arraylist, which is added to the Intent instance
            // and the foundDate is set to true.
            if (date.equalsIgnoreCase(earthquakeDate)) {
                foundDate = true;
                titles.add(data1.getTitle());
            }
        }
        // Check if the foundDate is still false after looping through all earthquake instances in the items arraylist.
        if (!foundDate) {
            String notFoundMessage = "Earthquake not found.";
            Toast.makeText(getApplicationContext(), notFoundMessage, Toast.LENGTH_LONG).show();
            return;
        }
        // The Intent instance is then used to open a new activity showing list of all earthquakes with the same date part details as the input.
        Intent i = new Intent(MainActivity.this, ListEarthquakes.class);
        i.putStringArrayListExtra("Titles", titles);
        startActivity(i);
    }

    public void showFragment(String text) {
        Fragment fr = new Fragment();
        switch (text) {
            case "Northerly":
                fr = new FragmentNorth();
                break;
            case "Southerly":
                fr = new FragmentSouth();
                break;
            case "Westerly":
                fr = new FragmentWest();
                break;
            case "Easterly":
                fr = new FragmentEast();
                break;
        }
        // Create new fragment manager.
        FragmentManager fm = getSupportFragmentManager();
        // Fragment transaction for replacing the current fragment to new fragment.
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.direction_fragment, fr);
        // making a commit after the transaction
        // to assure that the change is effective
        ft.commit();
    }
}