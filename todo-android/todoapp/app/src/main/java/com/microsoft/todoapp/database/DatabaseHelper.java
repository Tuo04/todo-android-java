package com.microsoft.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "com.microsoft.todoapp.database";
    private static final int DB_VERSION = 1;

    private static final String TABLE = "tasks";
    private static final String ID = BaseColumns._ID;
    private static final String COL_TASK_TITLE = "title";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE + " ( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void saveTask(String task) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_TASK_TITLE, task);
            db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void deleteTask(String task) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.delete(TABLE, COL_TASK_TITLE + " = ?", new String[]{task});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
        }
    }

    public ArrayList<String> getTasks() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();
            cursor = db.query(TABLE, new String[]{ID, COL_TASK_TITLE}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                int idx = cursor.getColumnIndex(COL_TASK_TITLE);
                taskList.add(cursor.getString(idx));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return taskList;
    }
}