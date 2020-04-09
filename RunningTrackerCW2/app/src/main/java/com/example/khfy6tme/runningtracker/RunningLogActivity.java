package com.example.khfy6tme.runningtracker;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.khfy6tme.runningtracker.utilities.ContentListAdapter;
import com.example.khfy6tme.runningtracker.utilities.Contract;
import com.example.khfy6tme.runningtracker.utilities.DatabaseManager;
import com.example.khfy6tme.runningtracker.utilities.MyContentProvider;
import com.example.khfy6tme.runningtracker.utilities.RunLogDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RunningLogActivity extends AppCompatActivity {
    // declare global variables
    private static DatabaseManager databaseManager;
    private static ContentListAdapter contentListAdapter;
    private static List<RunLogDetail> runLogDataList;
    ListView runLogListView;
    TextView sortTextView;
    ImageView sortBtn;
    private final static int SORT_BY_DATE_TIME = 0;
    private final static int SORT_BY_DISTANCE = 1;
    private final static int SORT_BY_DURATION = 2;
    private final static int SORT_BY_HIGHEST_SPEED = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_log);
        Log.d("RunningLogActivity", "onCreate()");

        // initialize database handler object
        databaseManager = new DatabaseManager(this, null, null,
                Contract.DATABASE_VERSION);

        // initialize run logs list view and its onClickListener()
        runLogListView = findViewById(R.id.runLogListView);
        sortBtn = findViewById(R.id.sortBtn);
        sortTextView = findViewById(R.id.sortTextView);


        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder sortingDialog = new AlertDialog.Builder(RunningLogActivity.this);
                sortingDialog.setTitle("Sort Data By");
                //Dialog options
                CharSequence options[]  = new CharSequence[]
                        {
                                "Date & Time",
                                "Distance",
                                "Duration",
                                "Highest Speed"
                        };

                //if user selects yes log deletes
                //else do nothing
                sortingDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        displayContents(i);
                    }
                });
                sortingDialog.show();
            }
        });

        runLogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(RunningLogActivity.this);
                deleteDialog.setTitle("Do you want to delete this entry?");
                //Dialog options
                CharSequence options[]  = new CharSequence[]
                        {
                                "Yes",
                                "No"
                        };

                //if user selects yes log deletes
                //else do nothing
                deleteDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0)
                        {
                            RunLogDetail clicked = runLogDataList.get(position);
                            databaseManager.deleteRunLog(clicked.getId());
                            displayContents(SORT_BY_DATE_TIME);
                            Toast.makeText(RunningLogActivity.this,"Log Deleted",Toast.LENGTH_LONG).show();

                        }else if(i==1){

                        }
                    }
                });
                deleteDialog.show();
            }
        });

        //By Default displays content by data and time
        displayContents(SORT_BY_DATE_TIME);
    } // end of onCreate()


    // method to populate run logs ListView with runLogDataList coming from the database
    private void displayContents(int sortBy) {
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
            runLogDataList.add(log);
        }
        //Sort Running log by Descending order
        switch (sortBy){
            case SORT_BY_DATE_TIME:
                Collections.sort(runLogDataList, new Comparator<RunLogDetail>() {
                    @Override
                    public int compare(RunLogDetail runLogDetail, RunLogDetail runLogDetail2) {
                        String dataTime1 = runLogDetail.getDate() +runLogDetail.getTime();
                        String dataTime2 = runLogDetail2.getDate() + runLogDetail2.getTime();
                        return dataTime2.compareTo(dataTime1);
                    }
                });
                sortTextView.setText("Sorted by Date and Time");
                break;
            case SORT_BY_DISTANCE:
                Collections.sort(runLogDataList, new Comparator<RunLogDetail>() {
                    @Override
                    public int compare(RunLogDetail runLogDetail, RunLogDetail runLogDetail2) {
                        return runLogDetail2.getDistance().compareTo(runLogDetail.getDistance());
                    }
                });
                sortTextView.setText("Sorted By Distance");
                break;
            case SORT_BY_DURATION:
                Collections.sort(runLogDataList, new Comparator<RunLogDetail>() {
                    @Override
                    public int compare(RunLogDetail runLogDetail, RunLogDetail runLogDetail2) {
                        return runLogDetail2.getDuration().compareTo(runLogDetail.getDuration());
                    }
                });
                sortTextView.setText("Sorted By Duration");
                break;
            case SORT_BY_HIGHEST_SPEED:
                Collections.sort(runLogDataList, new Comparator<RunLogDetail>() {
                    @Override
                    public int compare(RunLogDetail runLogDetail, RunLogDetail runLogDetail2) {
                        return runLogDetail2.getHighSpeed().compareTo(runLogDetail.getHighSpeed());
                    }
                });
                sortTextView.setText("Sorted By Highest Speed");
                break;
        }

        // set contentListAdapter runLogDataList and list contentListAdapter
        contentListAdapter = new ContentListAdapter(this, runLogDataList);
        runLogListView.setAdapter(contentListAdapter);
    } // end of displayContents()


    // activity lifecycle logs
    @Override
    protected void onStart() { Log.d("RunningLogActivity", "onStart()"); super.onStart(); }
    @Override
    protected void onStop() { Log.d("RunningLogActivity", "onStop()"); super.onStop(); }
    @Override
    protected void onDestroy() { Log.d("RunningLogActivity", "onDestroy()"); super.onDestroy(); }
} // end of class RunningLogActivity.java
