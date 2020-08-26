package com.ururu2909.mynotes.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ururu2909.mynotes.model.entities.Step;

import java.util.ArrayList;

public class StepService {

    private static SQLiteDatabase sqLiteDB;

    public StepService(DBHelper dbHelper){
        sqLiteDB = dbHelper.getWritableDatabase();
    }

    static public ArrayList<Step> getStepsByNoteId(int noteId){
        ArrayList<Step> steps = new ArrayList<>();

        Cursor cursor = sqLiteDB.query(DBHelper.STEPS_TABLE_NAME, new String[]{
                        DBHelper.COLUMN_ID,
                        DBHelper.COLUMN_NAME,
                        DBHelper.COLUMN_IS_COMPLETE,
                        DBHelper.COLUMN_NOTE_ID,
                }, DBHelper.COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)},
                null, null , DBHelper.COLUMN_ID );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Step step = getStepFromCursor(cursor);
            steps.add(step);
            cursor.moveToNext();
        }
        cursor.close();
        return steps;
    }

    static public void insertStep(Step step){
        ContentValues stepContentValues = new ContentValues();
        stepContentValues.put(DBHelper.COLUMN_NAME, step.getTitle());
        stepContentValues.put(DBHelper.COLUMN_IS_COMPLETE, step.isComplete());
        stepContentValues.put(DBHelper.COLUMN_NOTE_ID, step.getNoteId());
        sqLiteDB.insert(DBHelper.STEPS_TABLE_NAME, null, stepContentValues);
    }

    static public void updateStepCompletion(int id, boolean isComplete){
        ContentValues stepContentValues = new ContentValues();
        stepContentValues.put(DBHelper.COLUMN_IS_COMPLETE, isComplete ? 1 : 0);
        sqLiteDB.update(DBHelper.STEPS_TABLE_NAME, stepContentValues,
                DBHelper.COLUMN_ID + " = ?", new String[]{Integer.toString(id)});
    }

    static public void setNoteIdForNewSteps(int id){
        ContentValues stepContentValues = new ContentValues();
        stepContentValues.put(DBHelper.COLUMN_NOTE_ID, id);
        sqLiteDB.update(DBHelper.STEPS_TABLE_NAME, stepContentValues,
                DBHelper.COLUMN_NOTE_ID + " = ?", new String[]{"-1"});
    }

    static public void deleteExcessSteps(){
        sqLiteDB.delete(DBHelper.STEPS_TABLE_NAME, DBHelper.COLUMN_NOTE_ID + " = ?", new String[]{"-1"});
    }

    static private Step getStepFromCursor(Cursor cursor){
        return new Step(
                cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_IS_COMPLETE)),
                cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_NOTE_ID))
        );
    }
}
