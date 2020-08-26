package com.ururu2909.mynotes.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.ururu2909.mynotes.model.entities.Note;
import com.ururu2909.mynotes.presentation.mainscreen.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

final public class NoteService {

    private static SQLiteDatabase sqLiteDB;
    private static String[] noteTableColumns = new String[]{
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_IS_COMPLETE,
            DBHelper.COLUMN_TITLE,
            DBHelper.COLUMN_DESCRIPTION,
            DBHelper.COLUMN_IMAGE,
            DBHelper.COLUMN_COMPLETION_DATE,
            DBHelper.COLUMN_NOTIFICATION_DATE,
            DBHelper.COLUMN_NOTE_LIST_ID
    };

    public NoteService(DBHelper dbHelper){
        sqLiteDB = dbHelper.getWritableDatabase();
    }

    static public ArrayList<Note> getNotesByListId(int id){
        ArrayList<Note> notes = new ArrayList<>();

        String selection;
        String[] args;
        if (id == MainActivity.PLANED_LIST_ID){
            selection = DBHelper.COLUMN_COMPLETION_DATE + " != ?";
            args = new String[]{""};
        } else {
            selection = DBHelper.COLUMN_NOTE_LIST_ID + " = ?";
            args = new String[]{Integer.toString(id)};
        }

        Cursor cursor = sqLiteDB.query(DBHelper.NOTES_TABLE_NAME, noteTableColumns,
                selection, args, null, null , DBHelper.COLUMN_ID );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Note note = getNoteFromCursor(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        cursor.close();

        for (Note note : notes){
            note.setAllSteps((int) DatabaseUtils.queryNumEntries(sqLiteDB, DBHelper.STEPS_TABLE_NAME,
                    DBHelper.COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(note.getId())}));
            note.setCompletedSteps((int) DatabaseUtils.queryNumEntries(sqLiteDB, DBHelper.STEPS_TABLE_NAME,
                    DBHelper.COLUMN_NOTE_ID + " = ? AND " + DBHelper.COLUMN_IS_COMPLETE + " = 1",
                    new String[]{Integer.toString(note.getId())}));
        }
        return notes;
    }

    static public Note getNoteById(int id){
        Cursor cursor = sqLiteDB.query(DBHelper.NOTES_TABLE_NAME, noteTableColumns,
                DBHelper.COLUMN_ID + " = ?", new String[]{Integer.toString(id)},
                null, null , null );
        cursor.moveToFirst();
        return getNoteFromCursor(cursor);
    }

    static public long insertNote(Note note){
        ContentValues noteContentValues = fillContentValuesWithNote(note);
        return sqLiteDB.insert(DBHelper.NOTES_TABLE_NAME, null, noteContentValues);
    }

    static public void updateNote(Note note){
        ContentValues noteContentValues = fillContentValuesWithNote(note);
        sqLiteDB.update(DBHelper.NOTES_TABLE_NAME, noteContentValues,
                DBHelper.COLUMN_ID + " = ?", new String[]{Integer.toString(note.getId())});
    }

    static public void updateNoteCompletion(int id, boolean isComplete){
        ContentValues noteContentValues = new ContentValues();
        noteContentValues.put(DBHelper.COLUMN_IS_COMPLETE, isComplete ? 1 : 0);
        sqLiteDB.update(DBHelper.NOTES_TABLE_NAME, noteContentValues,
                DBHelper.COLUMN_ID + " = ?", new String[]{Integer.toString(id)});
    }

    static public void setNoteImage(int id, Bitmap image){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] data = outputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_IMAGE, data);
        sqLiteDB.update(DBHelper.NOTES_TABLE_NAME, contentValues,
                DBHelper.COLUMN_ID + " = ?", new String[]{Integer.toString(id)});
    }

    static public void deleteNoteById(int id){
        sqLiteDB.delete(DBHelper.NOTES_TABLE_NAME, DBHelper.COLUMN_ID + " = ?",
                new String[]{Integer.toString(id)});
    }

    static private Note getNoteFromCursor(Cursor cursor){
        return new Note(
                cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_IS_COMPLETE)),
                cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_IMAGE)),
                cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COMPLETION_DATE)),
                cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOTIFICATION_DATE)),
                0, 0,
                cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_NOTE_LIST_ID))
        );
    }

    static private ContentValues fillContentValuesWithNote(Note note){
        ContentValues noteContentValues = new ContentValues();
        noteContentValues.put(DBHelper.COLUMN_TITLE, note.getTitle());
        noteContentValues.put(DBHelper.COLUMN_DESCRIPTION, note.getExplanationText());
        noteContentValues.put(DBHelper.COLUMN_IS_COMPLETE, note.isComplete());
        noteContentValues.put(DBHelper.COLUMN_IMAGE, note.getImage());
        noteContentValues.put(DBHelper.COLUMN_COMPLETION_DATE, note.getCompletionDate());
        noteContentValues.put(DBHelper.COLUMN_NOTIFICATION_DATE, note.getNotificationDate());
        noteContentValues.put(DBHelper.COLUMN_NOTE_LIST_ID, note.getNoteListId());
        return noteContentValues;
    }
}
