package com.example.todoapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.todoapp.data.WorkListContract.WorkListEntry;

public class WorklistCursorAdapter extends CursorAdapter {

    public WorklistCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int titleColumnIndex = cursor.getColumnIndex(WorkListEntry.columnTitle);
        int dateColumnIndex = cursor.getColumnIndex(WorkListEntry.columnDate);
        int timeColumnIndex = cursor.getColumnIndex(WorkListEntry.columnTime);
        int progressValueColumnIndex=cursor.getColumnIndex(WorkListEntry.columnProgressValue);
        int timeInMillisColumnIndex = cursor.getColumnIndex(WorkListEntry.columnTimeInMillis);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        int progressValue=cursor.getInt(progressValueColumnIndex);
        long timeInMillis = cursor.getLong(timeInMillisColumnIndex);

        TextView titleView = view.findViewById(R.id.title_text);
        TextView dateView = view.findViewById(R.id.date_text);
        TextView timeView = view.findViewById(R.id.time_text);
        TextView warningView = view.findViewById(R.id.warning_text);
        ProgressBar progressBarView=view.findViewById(R.id.task_progress);

        titleView.setText(title);
        dateView.setText(date);
        timeView.setText(time);
        switch(progressValue){
            case 0:
                progressBarView.setProgress(0);
                break;
            case 1:
                progressBarView.setProgress(33);
                break;
            case 2:
                progressBarView.setProgress(67);
                break;
            default:
                progressBarView.setProgress(100);
        }
        if(progressValue==3){
            progressBarView.setVisibility(View.GONE);
        }

        if (!checkTime(timeInMillis) || progressValue==3) {
            warningView.setVisibility(View.GONE);
        }

    }


    private boolean checkTime(long time) {
        if (System.currentTimeMillis() >= time) {
            return true;
        }
        return false;

    }
}
