package com.example.dell.initialstage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.example.dell.initialstage.QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME;
import static com.example.dell.initialstage.QuestionsDetails.FeedEntry.TABLE_NAME;

/**
 * Created by Dell on 24-Sep-16.
 */

public class QuestionsDetails extends SQLiteOpenHelper {

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "QuestionsDBTable";
        public static final String COLUMN_NAME_EVENT_NAME = "event_name";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_QUESTION_NO = "question_no";
        public static final String COLUMN_NAME_ANSWER = "answer";
        public static final String COLUMN_NAME_USER_ANSWER = "user_answer";

    }
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "QuestionDatabase.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    QuestionsDetails.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
                    QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION + TEXT_TYPE+ COMMA_SEP+
                    QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER + TEXT_TYPE + COMMA_SEP +
                    QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO + TEXT_TYPE + COMMA_SEP +
                    QuestionsDetails.FeedEntry.COLUMN_NAME_USER_ANSWER + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EVENT_DETAILS.FeedEntry.TABLE_NAME;

    private SQLiteDatabase db;

    QuestionsDetails(Context context) {
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

    public static void UpdateAnswer(SQLiteDatabase db, String value, Question question_object, String event_name){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_EVENT_NAME,event_name);
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION,question_object.getQuestion());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER,question_object.getAnswer());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO,question_object.getQuestion_no());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_USER_ANSWER,value);

         db.update(TABLE_NAME, values, QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME + " = ? AND "+QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO
                +" = ?",
                new String[] {event_name,question_object.getQuestion_no()} );

    }

    public static void UpdateQuestions(SQLiteDatabase db,Question question_object, String event_name){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_EVENT_NAME,event_name);
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION,question_object.getQuestion());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER,question_object.getAnswer());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO,question_object.getQuestion_no());

        db.update(TABLE_NAME, values, QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME + " = ? AND "+QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO
                        +" = ?",
                new String[] {event_name,question_object.getQuestion_no()} );
    }

    public static void ResetAnswer(SQLiteDatabase db,Question question_object, String event_name){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_EVENT_NAME,event_name);
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION,question_object.getQuestion());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER,question_object.getAnswer());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO,question_object.getQuestion_no());
        values.put(QuestionsDetails.FeedEntry.COLUMN_NAME_USER_ANSWER,"");

        db.update(TABLE_NAME, values, QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME + " = ? AND "+QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO
                        +" = ?",
                new String[] {event_name,question_object.getQuestion_no()} );
    }
}
