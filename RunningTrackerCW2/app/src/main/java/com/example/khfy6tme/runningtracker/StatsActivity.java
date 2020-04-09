package com.example.khfy6tme.runningtracker;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.khfy6tme.runningtracker.utilities.Contract;
import com.example.khfy6tme.runningtracker.utilities.MyContentProvider;
import com.example.khfy6tme.runningtracker.utilities.RunLogDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
    // declare global UI elements

    private static LinearLayout todayLayout;
    private static LinearLayout monthLayout;
    private static LinearLayout weekLayout;
    private static LinearLayout allTimeLayout;
    private static LinearLayout allLayout;

    private static TextView todayDurationVal;
    private static TextView todayDistanceVal;
    private static TextView todayAvgSpeedVal;
    private static TextView todayHighSpeedVal;

    private static ImageView filterBtn;

    private static TextView weekDurationVal;
    private static TextView weekDistanceVal;
    private static TextView weekAvgSpeedVal;
    private static TextView weekHighSpeedVal;

    private static TextView monthDurationVal;
    private static TextView monthDistanceVal;
    private static TextView monthAvgSpeedVal;
    private static TextView monthHighSpeedVal;

    private static TextView totalDurationVal;
    private static TextView totalDistanceVal;
    private static TextView totalAvgSpeedVal;
    private static TextView totalHighSpeedVal;

    // declare global variables
    private static List<RunLogDetail> runLogDataList;
    private List<RunLogDetail> todayRunningLogList;
    private List<RunLogDetail> weekRunningLogList;
    private List<RunLogDetail> monthRunningLogList;

    //
    private double allTimeDuration = 0;
    private double allTimeDistance = 0;
    private double allTimeSpeed = 0;
    //private double allTimeAvgSpeed = 0;
    private double allTimeHighSpeed = 0;
    private int entryCount = 0;

    //Constant Global Variables for filtering display
    private final static int TODAY = 0;
    private final static int THIS_WEEK = 1;
    private final static int THIS_MONTH = 2;
    private final static int All_TIME = 3;
    private final static int SHOW_ALL = 4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Log.d("StatsActivity", "onCreate()");

        // initialize global lists
        todayRunningLogList = new ArrayList<RunLogDetail>();
        weekRunningLogList = new ArrayList<RunLogDetail>();
        monthRunningLogList = new ArrayList<RunLogDetail>();

        // initialize Layouts
        todayLayout = findViewById(R.id.todayStats);
        weekLayout = findViewById(R.id.thisWeekStats);
        monthLayout = findViewById(R.id.monthStat);
        allTimeLayout = findViewById(R.id.allTimeStats);
        allLayout = findViewById(R.id.allStat);

        //initialize today variables
        todayDurationVal = findViewById(R.id.todayDurationVal);
        todayDistanceVal = findViewById(R.id.todayDistanceVal);
        todayAvgSpeedVal = findViewById(R.id.todayAvgSpeedVal);
        todayHighSpeedVal = findViewById(R.id.todayHighSpeedVal);

        //initialize week variables
        weekDurationVal = findViewById(R.id.weekDurationVal);
        weekDistanceVal = findViewById(R.id.weekDistanceVal);
        weekAvgSpeedVal = findViewById(R.id.weekAvgSpeedVal);
        weekHighSpeedVal = findViewById(R.id.weekHighSpeedVal);

        //initialize month variables
        monthDurationVal = findViewById(R.id.monthDurationVal);
        monthDistanceVal = findViewById(R.id.monthDistanceVal);
        monthAvgSpeedVal = findViewById(R.id.monthAvgSpeedVal);
        monthHighSpeedVal = findViewById(R.id.monthHighSpeedVal);

        //initialize total variables
        totalDurationVal = findViewById(R.id.totalDurationVal);
        totalDistanceVal = findViewById(R.id.totalDistanceVal);
        totalAvgSpeedVal = findViewById(R.id.totalAvgSpeedVal);
        totalHighSpeedVal = findViewById(R.id.totalHighSpeedVal);

        //initialize filter button
        filterBtn = findViewById(R.id.filterBtn);

        // get stored logs from database
        getRunLogs();

        /*
        * Show dialog that allows user to filter stats
        * */
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogFilter = new AlertDialog.Builder(StatsActivity.this);
                dialogFilter.setTitle("Select your stats");
                //Dialog options
                CharSequence options[]  = new CharSequence[]
                        {
                                "Today",
                                "This Week",
                                "This Month",
                                "All Time",
                                "Show All"
                        };

                //if user selects yes log deletes
                //else do nothing
                dialogFilter.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        changeDisplay(i);
                    }
                });
                dialogFilter.show();
            }
        });

        //Default Display
        changeDisplay(SHOW_ALL);
    } // end of onCreate()


    // method to retrieve all run logs from database
    private void getRunLogs() {
        // start needed cursor
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI, null,
                null, null, null);

        runLogDataList = new ArrayList<RunLogDetail>();

        while (cursor.moveToNext()) { // loop through cursor elements
            // create new RunLogDetail object from run log details brought by cursor
            RunLogDetail log = new RunLogDetail(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(Contract.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(Contract.COLUMN_TIME)),
                    cursor.getString(cursor.getColumnIndex(Contract.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(Contract.COLUMN_DISTANCE)),
                    cursor.getString(cursor.getColumnIndex(Contract.COLUMN_DURATION)),
                    cursor.getString(cursor.getColumnIndex(Contract.COLUMN_AVG_SPEED)),
                    cursor.getString(cursor.getColumnIndex(Contract.COLUMN_HIGH_SPEED)));
            runLogDataList.add(log); // add to ArrayList
        }

        buildStatsLists();
    } // end of getRunLogs()


    // method to separate run entries into day, week and month
    private void buildStatsLists() {
        // get formatted date and time
        SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String todayDate = daySdf.format(now);

        // loop through all logs
        for (RunLogDetail current : runLogDataList) {
            if (isToday(todayDate, current)) { todayRunningLogList.add(current); }
            if (isThisWeek(todayDate, current)) { weekRunningLogList.add(current); }
            if (isThisMonth(todayDate, current)) { monthRunningLogList.add(current); }
            // set highest speed variable value
            if (Double.parseDouble(current.getHighSpeed()) > allTimeHighSpeed ) {
                allTimeHighSpeed = Double.parseDouble(current.getHighSpeed());
            }
            addAllDuration(Integer.toString(stringToIntDuration(current.getDuration())));
            addAllDistance(current.getDistance());
            addAllSpeed(current.getAvgSpeed());
            entryCount++;
        }
        //setAvgSpeed();
    } // end of buildStatsLists()


    // method to convert time from seconds to formatted HH:MM:SS
    private String intToStringDuration(double timer) {
        String formattedTime = "", secFormat = "", minFormatted = "00", hourFormatted = "";
        int hours = (int) (timer / 3600);
        int minutes = (int) ((timer % 3600) / 60);
        int seconds = (int) (timer % 60);

        if (seconds > 9) {
            secFormat = String.valueOf(seconds);
        } else if (seconds < 10) {
            secFormat = "0" + seconds;
        }

        if (minutes > 9) {
            minFormatted = String.valueOf(minutes);
        } else if (minutes < 10) {
            minFormatted = "0" + minutes;
        }
        formattedTime = minFormatted + ":" + secFormat;

        if (hours > 0) {
            hourFormatted = String.valueOf(hours);
            formattedTime = hourFormatted + ":" + formattedTime;
        }else{
            formattedTime = "00:"+minFormatted + ":" + secFormat;
        }

        return formattedTime;
    }


    //convert stored formatted time to seconds
    private int stringToIntDuration(String inStr) {
        String[] parts = inStr.split(":");
        int seconds = Integer.parseInt(parts[0])*3600 + Integer.parseInt(parts[1])*60 + Integer.parseInt(parts[2]);
        return seconds;
    }


    // check if current log belongs to today
    private boolean isToday(String date, RunLogDetail currentRunLogDetail) {
        if (currentRunLogDetail.getDate().equals(date)) {
            return true;
        }
        return false;
    }


    //check if current log belongs to this week
    private boolean isThisWeek(String date, RunLogDetail currentRunLogDetail) {
        int day = Integer.parseInt(date.substring(date.length() - 2));
        String dateStr = currentRunLogDetail.getDate();
        int today = Integer.parseInt(dateStr.substring(dateStr.length() - 2));

        if ((day <= today) && (day >= (today - 6))) { return true; }
        return false;
    }


    // check if current log belongs to this month
    private boolean isThisMonth(String date, RunLogDetail currentRunLogDetail) {
        int month = Integer.parseInt(date.substring((date.length() - 5),
                (date.length() - 3)));
        String dateStr = currentRunLogDetail.getDate();
        int thisMonth = Integer.parseInt(dateStr.substring((dateStr.length() - 5),
                (date.length() - 3)));

        if (thisMonth == month) { return true; }
        return false;
    }

    //set the values for today stats table
    private void setTodayDisplay(){
        // set today stats
        double todayTotalDuration = 0;
        double todayTotalDistance = 0;
        double todayTotalSpeed = 0;
        double todayTotalHighSpeed = 0;
        double todayRunCount = 0;
        for (RunLogDetail log : todayRunningLogList) {
            todayTotalDuration += stringToIntDuration(log.getDuration());
            todayTotalDistance += Double.parseDouble(log.getDistance());
            todayTotalSpeed += Double.parseDouble(log.getAvgSpeed());
            if (Double.parseDouble(log.getHighSpeed()) > todayTotalHighSpeed) {
                todayTotalHighSpeed = Double.parseDouble(log.getHighSpeed());
            }
            todayRunCount++;
        }

        todayDurationVal.setText(intToStringDuration(todayTotalDuration));
        todayDistanceVal.setText(String.format("%.3f", todayTotalDistance));
        todayAvgSpeedVal.setText(String.format("%.2f", todayTotalSpeed/todayRunCount));
        todayHighSpeedVal.setText(String.format("%.2f", todayTotalHighSpeed));
    }
    //set the values for this week stats table
    private void setThisWeekDisplay(){
        double weekTotalDuration = 0;
        double weekTotalDistance = 0;
        double weekTotalSpeed = 0;
        double weekTotalHighSpeed = 0;
        double weekRunCount = 0;
        for (RunLogDetail log : weekRunningLogList) {
            weekTotalDuration += stringToIntDuration(log.getDuration());
            weekTotalDistance += Double.parseDouble(log.getDistance());
            weekTotalSpeed += Double.parseDouble(log.getAvgSpeed());
            if (Double.parseDouble(log.getHighSpeed()) > weekTotalHighSpeed) {
                weekTotalHighSpeed = Double.parseDouble(log.getHighSpeed());
            }
            weekRunCount++;
        }

        weekDurationVal.setText(intToStringDuration(weekTotalDuration));
        weekDistanceVal.setText(String.format("%.3f", weekTotalDistance));
        weekAvgSpeedVal.setText(String.format("%.2f", weekTotalSpeed/weekRunCount));
        weekHighSpeedVal.setText(String.format("%.2f", weekTotalHighSpeed));
    }


    //set the values for this month stats table
    private void setThisMonthDisplay(){
        // set this month stats
        double monthTotalDuration = 0;
        double monthTotalDistance = 0;
        double monthTotalSpeed = 0;
        double monthHighestSpeed = 0;
        double monthRunCount = 0;
        for (RunLogDetail log : monthRunningLogList) {
            monthTotalDuration += stringToIntDuration(log.getDuration());
            monthTotalDistance += Double.parseDouble(log.getDistance());
            monthTotalSpeed += Double.parseDouble(log.getAvgSpeed());
            if (Double.parseDouble(log.getHighSpeed()) > monthHighestSpeed) {
                monthHighestSpeed = Double.parseDouble(log.getHighSpeed());
            }
            monthRunCount++;
        }

        monthDurationVal.setText(intToStringDuration(monthTotalDuration));
        monthDistanceVal.setText(String.format("%.3f", monthTotalDistance));
        monthAvgSpeedVal.setText(String.format("%.2f", monthTotalSpeed/monthRunCount));
        monthHighSpeedVal.setText(String.format("%.2f", monthHighestSpeed));
    }

    //set the values for all time stats table
    private void setAllTimeDisplay(){
        totalDurationVal.setText(intToStringDuration(allTimeDuration));
        totalDistanceVal.setText(String.format("%.3f", allTimeDistance));
        totalAvgSpeedVal.setText(String.format("%.2f", allTimeSpeed/entryCount));
        totalHighSpeedVal.setText(String.format("%.2f", allTimeHighSpeed));
    }





    // methods to calculate total duration, distance, and speed
    private void addAllDuration(String duration) { allTimeDuration += Double.parseDouble(duration); }
    private void addAllDistance(String distance) { allTimeDistance += Double.parseDouble(distance); }
    private void addAllSpeed(String speed) { allTimeSpeed += Double.parseDouble(speed); }


//    Chaange view of layout according to user selection
private void changeDisplay(int displayFilter){
    switch (displayFilter){
        case TODAY:
            setTodayDisplay();
            todayLayout.setVisibility(View.VISIBLE);
            weekLayout.setVisibility(View.GONE);
            monthLayout.setVisibility(View.GONE);
            allTimeLayout.setVisibility(View.GONE);
            break;
        case THIS_WEEK:
            setThisWeekDisplay();
            todayLayout.setVisibility(View.GONE);
            weekLayout.setVisibility(View.VISIBLE);
            monthLayout.setVisibility(View.GONE);
            allTimeLayout.setVisibility(View.GONE);
            break;
        case THIS_MONTH:
            setThisMonthDisplay();
            todayLayout.setVisibility(View.GONE);
            weekLayout.setVisibility(View.GONE);
            monthLayout.setVisibility(View.VISIBLE);
            allTimeLayout.setVisibility(View.GONE);
            break;
        case All_TIME:
            setAllTimeDisplay();
            todayLayout.setVisibility(View.GONE);
            weekLayout.setVisibility(View.GONE);
            monthLayout.setVisibility(View.GONE);
            allTimeLayout.setVisibility(View.VISIBLE);
            break;
        case SHOW_ALL:
            setTodayDisplay();
            setThisWeekDisplay();
            setThisMonthDisplay();
            setAllTimeDisplay();
            todayLayout.setVisibility(View.VISIBLE);
            weekLayout.setVisibility(View.VISIBLE);
            monthLayout.setVisibility(View.VISIBLE);
            allTimeLayout.setVisibility(View.VISIBLE);
            break;
    }
}
    // activity lifecycle logs
    @Override
    protected void onStart() { Log.d("StatsActivity", "onStart()"); super.onStart(); }
    @Override
    protected void onStop() { Log.d("StatsActivity", "onStop()"); super.onStop(); }
    @Override
    protected void onDestroy() { Log.d("StatsActivity", "onDestroy()"); super.onDestroy(); }
} // end of class StatsActivity.java
