package com.example.earthquakeapp;

import static android.view.View.*;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TitleAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> titles;

    public TitleAdapter(Context context, ArrayList<String> titles) {
        this.context = context;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public String getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflate(context, R.layout.list_earthquakes_text_view, null);
        TextView earthquakeTitle = rowView.findViewById(R.id.listEarthquake_item);
        earthquakeTitle.setText(getItem(position));
        String magnitude = earthquakeTitle.getText().toString().substring(27, 30);
        double magnitudeValue = Double.parseDouble(magnitude);
        if (magnitudeValue < 6.0) {
            earthquakeTitle.setBackgroundColor(Color.parseColor("#ADD8E6"));
        }
        else if (magnitudeValue >= 6.0 && magnitudeValue < 6.5) {
            earthquakeTitle.setBackgroundColor(Color.parseColor("#90EE90"));
        }
        else if (magnitudeValue >= 6.5 && magnitudeValue < 7.0) {
            earthquakeTitle.setBackgroundColor(Color.YELLOW);
        }
        else if (magnitudeValue >= 7.0 && magnitudeValue < 7.5) {
            earthquakeTitle.setBackgroundColor(Color.parseColor("#FFA500"));
        }
        else if (magnitudeValue >= 7.5) {
            earthquakeTitle.setBackgroundColor(Color.RED);
        }
        return rowView;
    }
}