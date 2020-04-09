package com.example.khfy6tme.runningtracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.khfy6tme.runningtracker.utilities.Contract;
import com.example.khfy6tme.runningtracker.utilities.DatabaseManager;
import com.example.khfy6tme.runningtracker.utilities.RunLogDetail;
import com.example.khfy6tme.runningtracker.utilities.TrackingService;

import java.util.Random;



public class MainActivity extends AppCompatActivity {
    // declare global variables
    public final static int REQUEST_CODE = 1;
    TrackingService trackingService;
    DatabaseManager databaseManager;
    Intent bindingIntent;
    boolean isBind = false;
    boolean isStartVisible = true;
    Button mainBtn,pauseBtn;
    ImageView historyBtn, statsBtn;
    TextView speedVal, distanceVal, elevationVal,speedTV, distanceTV, elevationTV;
    float totalDistance = 0, newDistance = 0;
    float timer = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity:", "onCreate()");


        // initialize UI elements
        mainBtn = findViewById(R.id.mainBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        historyBtn = findViewById(R.id.historyBtn);
        statsBtn = findViewById(R.id.statsBtn);
        speedTV = findViewById(R.id.speedTV);
        distanceTV = findViewById(R.id.distanceTV);
        elevationTV = findViewById(R.id.elevationTV);
        speedVal = findViewById(R.id.speedVal);
        distanceVal = findViewById(R.id.distanceVal);
        elevationVal = findViewById(R.id.elevationVal);
        speedTV.setVisibility(View.INVISIBLE);
        speedVal.setVisibility(View.INVISIBLE);
        distanceTV.setVisibility(View.INVISIBLE);
        distanceVal.setVisibility(View.INVISIBLE);
        elevationTV.setVisibility(View.INVISIBLE);
        elevationVal.setVisibility(View.INVISIBLE);

        // initialize database handler
        databaseManager = new DatabaseManager(this, null, null,
                Contract.DATABASE_VERSION);

        // check for permission
        checkPermissions();

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //total distance = total distance travelled + latest distance travelled
                totalDistance += Float.parseFloat(trackingService.getDistance());
                stopServiceClass(true);
                showLayoutOnStop();

            }
        });



        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRunning();
            }
        });


        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start activity to show all previous run logs
                Intent intent = new Intent(MainActivity.this, RunningLogActivity.class);
                startActivity(intent);
            }
        });

        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start activity to show stats about all run logs in the database
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });

    } // end of onCreate()

    private void startRunning()
    {
        if (isStartVisible ) { // if initial screen is visible
            startServiceClass();

            showLayoutOnStart();
            // create thread to keep updating UI elements
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (!isStartVisible) {
                        try {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    timer++;

                                    //total distance travelled plus new distance travelled by user
                                    newDistance = totalDistance + Float.parseFloat(trackingService.getDistance());
                                    distanceVal.setText(String.format("%.3f", newDistance));
                                    speedVal.setText(trackingService.getSpeed());
                                    elevationVal.setText(getTimeFormated(timer));


                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start(); // start thread
        } else { // if user has pressed START

            totalDistance = newDistance;
            newDistance = 0;

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Do you want to save this entry?");


            //Dialog options
            CharSequence options[]  = new CharSequence[]
                    {
                            "Yes",
                            "No"
                    };

            //if user selects yes log deletes
            //else do nothing
            dialog.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0)
                    {
                        saveLogInstance();
                        Toast.makeText(MainActivity.this,"Running Log Saved",Toast.LENGTH_LONG).show();

                    }else if(i==1){

                        Toast.makeText(MainActivity.this,"Running Log was not saved",Toast.LENGTH_LONG).show();
                        timer = 0;
                        totalDistance = 0;

                    }
                }
            });
            dialog.show();


            stopServiceClass(true);
            showLayoutOnStop();




        } // end if-else statement
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // if permission is granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else { // if permission not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);
            }
        }
    } // end of checkPermissions()


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            // if permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "GPS permission granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startServiceClass() { // method to start the GPS tracking service

            // create binding intent
            bindingIntent = new Intent(this, TrackingService.class);
            // start and bind service
            startService(bindingIntent);
            bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    } // end of startServiceClass()


    public void stopServiceClass(boolean isUnbind) { // method to stop the GPS tracking service
        Log.d("MainActivity", "SERVICE STOPPED");
        if(isUnbind){
            if(isBind){
                unbindService(serviceConnection);
                isBind = false ;
            }
        }
        //trackingService.getDuration();
        // stop service
        bindingIntent = new Intent(getApplicationContext(), TrackingService.class);
        stopService(bindingIntent);
    } // end of stopServiceClass()



    private void showLayoutOnStop(){
        //trackingService.getDuration();
        mainBtn.setBackgroundResource(R.drawable.start_button);
        mainBtn.setTextColor(Color.parseColor("#fff200"));
        mainBtn.setText("START");
        historyBtn.setVisibility(View.VISIBLE);
        statsBtn.setVisibility(View.VISIBLE);
        speedTV.setVisibility(View.INVISIBLE);
        speedVal.setVisibility(View.INVISIBLE);
        distanceTV.setVisibility(View.INVISIBLE);
        distanceVal.setVisibility(View.INVISIBLE);
        elevationTV.setVisibility(View.INVISIBLE);
        elevationVal.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.INVISIBLE);
        isStartVisible = true;
    }

    private void showLayoutOnStart(){
        // update UI elements
        mainBtn.setBackgroundResource(R.drawable.stop_button);
        mainBtn.setTextColor(Color.parseColor("#fff200"));
        mainBtn.setText("STOP");
        historyBtn.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
        statsBtn.setVisibility(View.INVISIBLE);
        speedTV.setVisibility(View.VISIBLE);
        speedVal.setVisibility(View.VISIBLE);
        distanceTV.setVisibility(View.VISIBLE);
        distanceVal.setVisibility(View.VISIBLE);
        elevationTV.setVisibility(View.VISIBLE);
        elevationVal.setVisibility(View.VISIBLE);
        speedVal.setText("0.00");
        distanceVal.setText(String.format("%.3f", newDistance));
        elevationVal.setText("00:00:00");
        isStartVisible = false;
    }

    /*
     * Formats duration and progress into hours, minutes and seconds
     * */
    public String getTimeFormated(float timer) {
        String formatedTime = "", secFormat = "", minFormated = "00", hourFormated = "";
        int hours = (int) (timer / 3600);
        int minutes = (int) ((timer % 3600) / 60);
        int seconds = (int) (timer % 60);

        if (seconds > 9) {
            secFormat = String.valueOf(seconds);
        } else if (seconds < 10) {
            secFormat = "0" + seconds;
        }

        if (minutes > 9) {
            minFormated = String.valueOf(minutes);
        } else if (minutes < 10) {
            minFormated = "0" + minutes;
        }
        formatedTime = minFormated + ":" + secFormat;

        if (hours > 0) {
            hourFormated = String.valueOf(hours);
            formatedTime = hourFormated + ":" + formatedTime;
        }else{
            formatedTime = "00:"+minFormated + ":" + secFormat;
        }

        Log.d("MainActivity",formatedTime);
        return formatedTime;
    }


    // method to save new run log into database of the app
    public void saveLogInstance() {
        // randomize a three-digit id
        Random random = new Random();
        int id = random.nextInt(1000);

        // get run log information from service
        String time = trackingService.getTime();
        String date = trackingService.getDate();
        String distance = String.format("%.2f", totalDistance);
        String duration = getTimeFormated(timer);
        String avgSpeed = trackingService.getAvgSpeed();
        String highSpeed = trackingService.getHighestSpeed();

        // create new run log object with information
        RunLogDetail runLogDetail = new RunLogDetail(id, time, date, distance, duration, avgSpeed, highSpeed);

        // add new running log to database
        databaseManager.addRunLog(runLogDetail);

        stopServiceClass(true);
        //Reset total Distance and timer
        totalDistance = 0;
        timer = 0;
    } // end of saveLogInstance()


    // connect to the service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackingService.GPSBinder binder = (TrackingService.GPSBinder) iBinder;
            Log.d("MainActivity", "SERVICE CONNECTED");
            trackingService = binder.getService();
            isBind = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) { isBind = false; }
    };


    // activity lifecycle logs
    @Override
    protected void onStart() { Log.d("MainActivity", "onStart()"); super.onStart(); }
    @Override
    protected void onStop() { Log.d("MainActivity", "onStop()"); super.onStop(); }
    @Override
    protected void onDestroy() { Log.d("MainActivity", "onDestroy()"); super.onDestroy(); }

    @Override
    public void onBackPressed() {
        totalDistance += Float.parseFloat(trackingService.getDistance());
        stopServiceClass(true);
        showLayoutOnStop();
    }
} // end of class MainActivity.java
