package com.example.dell.initialstage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.example.dell.initialstage.QuestionsDetails.FeedEntry.TABLE_NAME;

/**
 * Created by Dell on 23-Sep-16.
 */


public class EVENT_DETAILS extends SQLiteOpenHelper{

    /* Inner class that defines the table contents */
        public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "EventsTable";
        public static final String COLUMN_NAME_EVENT_NAME = "event_name";
        public static final String COLUMN_NAME_PASSCODE = "passcode";
        public static final String COLUMN_NAME_EVENT_DATE = "event_date";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";

    }
        // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EventsDatabase.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_PASSCODE + TEXT_TYPE+ COMMA_SEP+
                    FeedEntry.COLUMN_NAME_EVENT_DATE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_START_TIME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_END_TIME + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private SQLiteDatabase db;

    EVENT_DETAILS(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void updateEventDetails(SQLiteDatabase db,String event_name,String event_date,
                                          String passcode,String start_time,String end_time){
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_EVENT_NAME,event_name);
        values.put(FeedEntry.COLUMN_NAME_EVENT_DATE,event_date);
        values.put(FeedEntry.COLUMN_NAME_START_TIME,start_time);
        values.put(FeedEntry.COLUMN_NAME_END_TIME,end_time);
        values.put(FeedEntry.COLUMN_NAME_PASSCODE,passcode);

        db.update(FeedEntry.TABLE_NAME, values, FeedEntry.COLUMN_NAME_EVENT_NAME + " = ?",
                new String[] {event_name} );
    }
}
