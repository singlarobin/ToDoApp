package com.example.todoapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.todoapp.data.WorkListContract.WorkListEntry;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WorkListProvider extends ContentProvider {
    private DatabaseHandler mDbHandler;
    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static final int workList=100;
    private static final int workItemId=101;
    static {
        sUriMatcher.addURI(WorkListContract.contentAuthority,WorkListContract.pathWorkList,workList);
        sUriMatcher.addURI(WorkListContract.contentAuthority,WorkListContract.pathWorkList+"/#",workItemId);
    }
    @Override
    public boolean onCreate() {
        mDbHandler=new DatabaseHandler(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database=mDbHandler.getReadableDatabase();
        Cursor cursor;
        int match=sUriMatcher.match(uri);
        switch (match){
            case workList:
                cursor=database.query(
                        WorkListEntry.tableName,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case workItemId:
                selection=WorkListEntry.columnId+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=database.query(
                        WorkListEntry.tableName,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                cursor=null;
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match=sUriMatcher.match(uri);

        switch(match){
            case workList:
                return insertTask(uri,values);
        }
        return null;
    }

    private Uri insertTask(Uri uri, ContentValues values) {
        String title=values.getAsString(WorkListEntry.columnTitle);
        String date=values.getAsString(WorkListEntry.columnDate);
        String time=values.getAsString(WorkListEntry.columnTime);
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)){
            return null;
        }

        SQLiteDatabase database=mDbHandler.getWritableDatabase();
        long rowId=database.insert(WorkListEntry.tableName,null,values);
        if(rowId==-1){
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match=sUriMatcher.match(uri);
        int rowsDeleted=0;
        SQLiteDatabase database=mDbHandler.getWritableDatabase();
        switch (match){
            case workList:
                rowsDeleted= database.delete(WorkListEntry.tableName,selection,selectionArgs);
            case workItemId:
                selection=WorkListEntry.columnId+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted= database.delete(WorkListEntry.tableName,selection,selectionArgs);
        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match=sUriMatcher.match(uri);
        switch (match){
            case workItemId:
                selection=WorkListEntry.columnId+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTask(uri,values,selection,selectionArgs);
        }
        return 0;
    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String title=values.getAsString(WorkListEntry.columnTitle);
        String date=values.getAsString(WorkListEntry.columnDate);
        String time=values.getAsString(WorkListEntry.columnTime);
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)){
            return 0;
        }
        if(TextUtils.isEmpty(title)){
            return 0;
        }

        SQLiteDatabase database=mDbHandler.getWritableDatabase();
        int rowsUpdated=database.update(WorkListEntry.tableName,values,selection,selectionArgs);
        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
