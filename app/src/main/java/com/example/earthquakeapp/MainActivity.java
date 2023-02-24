package com.example.earthquakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

// Name: Brian Koome
// Student ID: S2004892
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText date;
    private TextView dataDisplayMagnitude, dataDisplayDepth;
    ArrayList<Earthquake> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataDisplayMagnitude = findViewById(R.id.dataDisplayMagnitude);
        dataDisplayDepth = findViewById(R.id.dataDisplayDepth);
        date = findViewById(R.id.date);
        date.setOnClickListener(this);

        Button largestMagnitudeContent = findViewById(R.id.largestMagnitudeContent);
        largestMagnitudeContent.setOnClickListener(this);

        Log.e("MyTag","in onClick");
        String urlSource = "http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
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
                    dataDisplayMagnitude.setText(String.format("%s\n", data1.getTitle()));
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
                    dataDisplayDepth.setText(String.format("%s\n", data1.getTitle()));
                }
            }
        }
    }

    public void onClick(View v) {
        if(v == date) {
            setCalendar();
        }
        // TO BE CONFIRMED WHY IT'S NOT WORKING.
        else if(v.getId() == R.id.largestMagnitudeContent) {
            viewDetailedContent(dataDisplayMagnitude);
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
                        date.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
                    }
                },
                year,month,day);
        dpd.show();
    }

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

    private String getLocation (String desc) {
        String[] descContent = desc.split(" ; ");
        String[] location = descContent[1].split(": ");
        return location[1];
    }

    private String getDepth(String desc) {
        String[] descContent = desc.split(" ; ");
        String[] depth = descContent[3].split(": ");
        String[] depthValue = depth[1].split(" ");
        return depthValue[0];
    }

    // Method for extracting the magnitude information from the description data part.
    private String getMagnitude(String desc) {
        // Split the whole description information e.g Magnitude.
        String[] descContent = desc.split(" ; ");
        // Split the specific magnitude information into the label section and the value section.
        String[] magnitude = descContent[4].split(": ");
        return magnitude[1];
    }
}

//dataDisplay.setText(String.format("%s\nTitle: %s\n", dataDisplay.getText(), data.getTitle()));
//dataDisplay.setText(String.format("%s\nLocation: %s\n", dataDisplay.getText(), getLocation(data.getDescription())));
//dataDisplay.setText(String.format("%s\nDepth: %s\n", dataDisplay.getText(), getDepth(data.getDescription())));
//dataDisplay.setText(String.format("%s\nMagnitude: %s\n", dataDisplay.getText(), getMagnitude(data.getDescription())));
//dataDisplay.setText(String.format("%s\nPublished date: %s\n", dataDisplay.getText(), data.getPublishedDate()));
//dataDisplay.setText(String.format("%s\nLatitude: %s\n", dataDisplay.getText(), data.getLatitude()));
//dataDisplay.setText(String.format("%s\nLongitude: %s\n", dataDisplay.getText(), data.getLongitude()));