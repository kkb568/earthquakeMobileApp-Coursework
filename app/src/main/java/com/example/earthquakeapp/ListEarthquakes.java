package com.example.earthquakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ListEarthquakes extends AppCompatActivity {

    ArrayList<Earthquake> earthquakes = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_earthquakes);

        ListView listEarthquakes = findViewById(R.id.listEarthquakes);
        Intent i = getIntent();
        ArrayList<String> titles = i.getStringArrayListExtra("Titles");
        TitleAdapter titleAdapter = new TitleAdapter(this, titles);
        listEarthquakes.setAdapter(titleAdapter);
        String urlSource = "https://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
        new Thread(new ListEarthquakes.Task(urlSource)).start();

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

    private class Task implements Runnable
    {
        private final String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run() {
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
                            earthquakes.add(eq);
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
        }
    }
}