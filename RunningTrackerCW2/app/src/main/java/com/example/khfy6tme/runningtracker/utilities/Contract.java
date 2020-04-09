package com.example.khfy6tme.runningtracker.utilities;

import android.net.Uri;

public class Contract {

    // communication contract shared among classes

    public static final String AUTHORITY = "com.example.khfy6tme.runningtracker.utilities.MyContentProvider";
    public static final String TABLE_LOGS = "runninglog";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_LOGS);
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_AVG_SPEED = "averageSpeed";
    public static final String COLUMN_HIGH_SPEED = "highSpeed";
    public static final String DATABASE_NAME = "trackingDB.db";
    public static final int DATABASE_VERSION = 1;
}
