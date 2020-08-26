package com.ururu2909.mynotes.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ururu2909.mynotes.model.entities.NoteList;

import java.util.ArrayList;

final public class NoteListService {

    private static SQLiteDatabase sqLiteDB;

    public NoteListService(DBHelper dbHelper){
        sqLiteDB = dbHelper.getWritableDatabase();
    }

    static public ArrayList<NoteList> getNoteLists(){
        ArrayList<NoteList> noteLists = new ArrayList<>();
        Cursor cursor = sqLiteDB.query(DBHelper.NOTE_LISTS_TABLE_NAME, new String[]{DBHelper.COLUMN_ID,
                        DBHelper.COLUMN_NAME}, null, null, null, null,
                DBHelper.COLUMN_NAME + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            NoteList noteList = new NoteList(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID)),
                                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            noteLists.add(noteList);
            cursor.moveToNext();
        }
        cursor.close();
        return noteLists;
    }

    static public String getNoteListName(int id){
        String noteListName;
        Cursor cursor = sqLiteDB.query(DBHelper.NOTE_LISTS_TABLE_NAME, new String[]{DBHelper.COLUMN_NAME},
                DBHelper.COLUMN_ID + " = ?", new String[]{Integer.toString(id)},
                null, null, null);
        cursor.moveToFirst();
        noteListName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
        cursor.close();
        return noteListName;
    }

    static public void insertNoteList(NoteList noteList){
        ContentValues noteListContentValues = new ContentValues();
        noteListContentValues.put(DBHelper.COLUMN_NAME, noteList.getName());
        sqLiteDB.insert(DBHelper.NOTE_LISTS_TABLE_NAME, null, noteListContentValues);
    }

    static public void updateListName(int id, String newName){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NAME, newName);
        sqLiteDB.update(DBHelper.NOTE_LISTS_TABLE_NAME, contentValues,
                DBHelper.COLUMN_ID + " = ?", new String[]{Integer.toString(id)});
    }
}
