package com.example.khfy6tme.runningtracker.utilities;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.khfy6tme.runningtracker.R;

import java.util.List;

public class ContentListAdapter extends ArrayAdapter<RunLogDetail> {
    // class to render ListView items through a custom adapter

    // declare global variables
    List<RunLogDetail> runLogDetailList;
    TextView date;
    TextView time;
    TextView duration;
    TextView distance;
    TextView high;
    TextView avg;


    // define class constructor
    public ContentListAdapter(Context context, List<RunLogDetail> runLogDetailList) {
        super(context, R.layout.listview_item, runLogDetailList);
        this.runLogDetailList = runLogDetailList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.listview_item, parent, false);

        // get run log details
        RunLogDetail item = getItem(position);
        date = customView.findViewById(R.id.dateLV);
        time = customView.findViewById(R.id.timeLV);
        duration = customView.findViewById(R.id.durationVal);
        distance = customView.findViewById(R.id.distanceVal);
        high = customView.findViewById(R.id.highVal);
        avg = customView.findViewById(R.id.avgVal);

        // update UI element values
        date.setText("Date: " + item.getDate());
        time.setText("Time: " + item.getTime());
        duration.setText(item.getDuration());
        distance.setText(item.getDistance());
        avg.setText(item.getAvgSpeed());
        high.setText(item.getHighSpeed());

        return customView;
    }
} // end of ContentListAdapter.java