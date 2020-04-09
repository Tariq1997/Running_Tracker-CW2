package com.example.khfy6tme.runningtracker.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* MOST OF THIS CLASS WAS DERIVED FROM LAB 5 (COMP3040) */

public class DatabaseManager extends SQLiteOpenHelper {


    // declare global variables
    private ContentResolver contentResolver;
    private static final int DATABASE_VERSION = Contract.DATABASE_VERSION;
    private static final String DATABASE_NAME = Contract.DATABASE_NAME;
    public static final String TABLE_LOGS = Contract.TABLE_LOGS;
    public static final String COLUMN_ID = Contract.COLUMN_ID;
    public static final String COLUMN_TIME = Contract.COLUMN_TIME;
    public static final String COLUMN_DATE = Contract.COLUMN_DATE;
    public static final String COLUMN_DISTANCE = Contract.COLUMN_DISTANCE;
    public static final String COLUMN_DURATION = Contract.COLUMN_DURATION;
    public static final String COLUMN_AVG_SPEED = Contract.COLUMN_AVG_SPEED;
    public static final String COLUMN_HIGH_SPEED = Contract.COLUMN_HIGH_SPEED;

    // define class constructor
    public DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("DatabaseManager", "onCreate()");
        String CREATE_LOGS_TABLE = "CREATE TABLE " + TABLE_LOGS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_DISTANCE + " TEXT, " +
                COLUMN_DURATION + " TEXT, " +
                COLUMN_AVG_SPEED + " TEXT, " +
                COLUMN_HIGH_SPEED +" TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_LOGS_TABLE);
    } // end of onCreate()

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("DatabaseManager", "onUpgrade()");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(sqLiteDatabase);
    } // end of onUpgrade()

    public void addRunLog(RunLogDetail runLog) {
        // create instance of ContentValues with Running Log details
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_ID, runLog.getId());
        values.put(Contract.COLUMN_TIME, runLog.getTime());
        values.put(Contract.COLUMN_DATE, runLog.getDate());
        values.put(Contract.COLUMN_DISTANCE, runLog.getDistance());
        values.put(Contract.COLUMN_DURATION, runLog.getDuration());
        values.put(Contract.COLUMN_AVG_SPEED, runLog.getAvgSpeed());
        values.put(Contract.COLUMN_HIGH_SPEED, runLog.getHighSpeed());

        // insert new Running Log into database
        contentResolver.insert(Contract.CONTENT_URI, values);
    } // end of addRunLog()

    public void deleteRunLog(int runID) {
        String selection = "id = \"" + runID + "\"";
        contentResolver.delete(MyContentProvider.CONTENT_URI, selection, null);
    } // end of deleteRunLog()()

} // end of class DatabaseManager.java