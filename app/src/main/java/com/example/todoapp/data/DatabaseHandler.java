package com.example.todoapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.todoapp.data.WorkListContract.WorkListEntry;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DatabaseVersion=1;
    private static final String DatabaseName="todolist.db";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DatabaseName, null, DatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+ WorkListEntry.tableName + "("
                + WorkListEntry.columnId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WorkListEntry.columnTitle + " TEXT NOT NULL,"
                + WorkListEntry.columnCompleted + " INTEGER NOT NULL DEFAULT 0, "
                + WorkListEntry.columnDate + " TEXT NOT NULL,"
                + WorkListEntry.columnTime + " TEXT NOT NULL,"
                + WorkListEntry.columnTimeInMillis + " INTEGER NOT NULL,"
                + WorkListEntry.columnDescription1 + " TEXT NOT NULL,"
                + WorkListEntry.columnDescription2 + " TEXT NOT NULL, "
                + WorkListEntry.columnDescription3 + " TEXT NOT NULL,"
                + WorkListEntry.columnDesc1Checked + " INTEGER NOT NULL DEFAULT 0,"
                + WorkListEntry.columnDesc2Checked + " INTEGER NOT NULL DEFAULT 0,"
                +WorkListEntry.columnDesc3Checked + " INTEGER NOT NULL DEFAULT 0,"
                + WorkListEntry.columnProgressValue +" INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TableName);
//
//        // Create tables again
//        onCreate(db);
    }

}
