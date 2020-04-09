package com.example.khfy6tme.runningtracker.utilities;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackingService extends Service implements LocationListener {
    // declare global variables

    boolean isStartingPosition = true;
    static Location startLocation;
    static Location endLocation;
    static float highestSpeed = 0;
    static int changeCount = 0;
    static double totalSpeed = 0;

    // create and initialize binder object
    GPSBinder gpsBinder = new GPSBinder();

    // nested public class to let other classes bind to TrackingService
    public class GPSBinder extends Binder {
        public TrackingService getService(){
            Log.d("GPSBinder", "getService()");
            return TrackingService.this;
        }
    }

    // define constructors
    public TrackingService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TrackingService", "onStartCommand()");
        return START_NOT_STICKY;
    }



    public void getLocation() {
        Log.d("TrackingService", "getLocation()");

        // initialize location listener and manager

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        TrackingService locationListener = new TrackingService();


        try {
            // start periodical location update
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, 1, locationListener);
        } catch (SecurityException e) {
            Log.d("MYAPP", e.toString());
        }
    }

    public String getDistance() {
        float distance = (float) 0.000;
        if(endLocation != null){
            distance = endLocation.distanceTo(startLocation);
        }

        return String.format("%.3f", distance/1000);
    }



    public String getSpeed() {
        double currentSpeed = 0.00;
        if(endLocation != null) {
            if (endLocation.hasSpeed()) {
                currentSpeed = endLocation.getSpeed();
            }
        }
        return String.format("%.2f", currentSpeed);
    }

    public String getElevation() {
        double alt = 0.00;
        if(endLocation !=null) {
            if(endLocation.hasAltitude()){
                alt = endLocation.getAltitude();
            }

        }
        return String.format("%.2f", alt);
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String timeStr = sdf.format(now);
        return timeStr;
    }



    public String getDate() {
        SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String dateStr = daySdf.format(now);
        return dateStr;
    }


    public String getAvgSpeed() {
        String avgSpeed = "";
        if (changeCount > 0) { avgSpeed = String.format("%.2f", totalSpeed/changeCount); }
        return avgSpeed;
    }

    public String getHighestSpeed() {
        return String.format("%.2f", highestSpeed);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TrackingService", "onBind()");
        isStartingPosition = true;
        getLocation();
        return gpsBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("TrackingService", "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location ","Latitude: "+location.getLatitude() +" Longitude: "+location.getLongitude());
        if (isStartingPosition) {
            startLocation = location;
            isStartingPosition = false;
            totalSpeed = 0;
            changeCount = 0;
        }

        endLocation = location;
        if (location.hasSpeed()) {
            double currentSpeed = location.getSpeed();
            if (currentSpeed > highestSpeed) { highestSpeed = (float) currentSpeed; }
            totalSpeed += currentSpeed;
            changeCount++;
        }


    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("TrackingService", "onStatusChanged()");

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("TrackingService", "onProviderEnabled()");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("TrackingService", "onProviderDisabled()");
    }

} // end of class TrackingService.java


