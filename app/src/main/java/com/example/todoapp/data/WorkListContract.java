package com.example.todoapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class WorkListContract {
    public static String contentAuthority="com.example.todoapp";
    public static Uri baseContentUri=Uri.parse("content://"+contentAuthority);
    public static String pathWorkList="ToDo";
    public static class WorkListEntry implements BaseColumns {
        public static final Uri contentUri=Uri.withAppendedPath(baseContentUri,pathWorkList);
        public static final String contentListType= ContentResolver.CURSOR_DIR_BASE_TYPE+contentAuthority+pathWorkList;
        public static final String contentItemType=ContentResolver.CURSOR_ITEM_BASE_TYPE+contentAuthority+pathWorkList;

        public static final String tableName="TaskToDo";
        public static final String columnId=BaseColumns._ID;
        public static final String columnTitle ="title";
        public static final String columnCompleted="completed";
        public static final String columnDate="date";
        public static final String columnTime="time";
        public static final String columnTimeInMillis="timeInMillis";
        public static final String columnProgressValue="progressValue";
        public static final String columnDescription1="description1";
        public static final String columnDescription2="description2";
        public static final String columnDescription3="description3";
        public static final String columnDesc1Checked="desc1Checked";
        public static final String columnDesc2Checked="desc2Checked";
        public static final String columnDesc3Checked="desc3Checked";

        public static final String notificationDescription="Notification Content";

        public static final int no=0;
        public static final int yes=1;

    }
}
