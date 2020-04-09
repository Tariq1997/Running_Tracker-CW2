package com.example.khfy6tme.runningtracker.utilities;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.content.UriMatcher;
import android.text.TextUtils;
import android.util.Log;

/* THIS CLASS WAS DERIVED FROM LAB 5 (COMP3040) */

public class MyContentProvider extends ContentProvider {


    private static final String AUTHORITY = Contract.AUTHORITY;
    private static final String TABLE_LOGS = Contract.TABLE_LOGS;
    public static final Uri CONTENT_URI = Contract.CONTENT_URI;
    public static final int LOGS = 1;
    public static final int LOG_ID = 2;
    private DatabaseManager databaseManager;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, TABLE_LOGS, LOGS);
        sURIMatcher.addURI(AUTHORITY, TABLE_LOGS + "/#", LOG_ID);
    }

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = databaseManager.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case LOGS:
                rowsDeleted = sqlDB.delete(DatabaseManager.TABLE_LOGS,
                        selection, selectionArgs);
                break;
            case LOG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DatabaseManager.TABLE_LOGS,
                            DatabaseManager.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(DatabaseManager.TABLE_LOGS,
                            DatabaseManager.COLUMN_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("debug", "insert");
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = databaseManager.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case LOGS:
                id = sqlDB.insert(DatabaseManager.TABLE_LOGS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(TABLE_LOGS + "/" + id);
    }

    @Override
    public boolean onCreate() {
        Log.d("MyContentProvider", "onCreate()");
        databaseManager = new DatabaseManager(getContext(), null, null, 1);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseManager.TABLE_LOGS);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LOG_ID:
                queryBuilder.appendWhere(DatabaseManager.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case LOGS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        Cursor cursor = queryBuilder.query(databaseManager.getReadableDatabase(), projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = databaseManager.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case LOGS:
                rowsUpdated = sqlDB.update(DatabaseManager.TABLE_LOGS, values,
                        selection, selectionArgs);
                break;
            case LOG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DatabaseManager.TABLE_LOGS, values,
                            DatabaseManager.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(DatabaseManager.TABLE_LOGS, values,
                            DatabaseManager.COLUMN_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}