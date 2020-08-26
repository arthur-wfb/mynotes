package com.ururu2909.mynotes.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mynotes_db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static final String NOTES_TABLE_NAME = "notes";
    static final String NOTE_LISTS_TABLE_NAME = "lists";
    static final String STEPS_TABLE_NAME = "steps";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_IS_COMPLETE = "is_complete";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_DESCRIPTION = "description";
    static final String COLUMN_IMAGE = "image";
    static final String COLUMN_COMPLETION_DATE = "completion_date";
    static final String COLUMN_NOTIFICATION_DATE = "notification_date";
    static final String COLUMN_NOTE_LIST_ID = "note_list_id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_NOTE_ID = "note_id";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NOTES_TABLE_NAME + "("
                + COLUMN_ID + " INTEGER primary key autoincrement,"
                + COLUMN_IS_COMPLETE + " INTEGER,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IMAGE + " BLOB,"
                + COLUMN_COMPLETION_DATE + " TEXT,"
                + COLUMN_NOTIFICATION_DATE + " TEXT,"
                + COLUMN_NOTE_LIST_ID + " INTEGER" + ");");

        db.execSQL("CREATE TABLE " + NOTE_LISTS_TABLE_NAME + "("
                + COLUMN_ID + " INTEGER primary key autoincrement,"
                + COLUMN_NAME + " TEXT" + ");");

        db.execSQL("CREATE TABLE " + STEPS_TABLE_NAME + "("
                + COLUMN_ID + " INTEGER primary key autoincrement,"
                + COLUMN_IS_COMPLETE + " INTEGER,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_NOTE_ID + " INTEGER" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
