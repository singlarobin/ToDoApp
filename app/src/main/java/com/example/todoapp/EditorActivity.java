package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todoapp.data.WorkListContract.WorkListEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int loaderId=0;
    private EditText titleView;
    private Button dateButtonView;
    private static TextView dateTextView;
    private Button timeButtonView;
    private static TextView timeTextView;
    private EditText description1View;
    private EditText description2View;
    private EditText description3View;
    private int desc1checked=0,desc2checked=0,desc3checked=0;
    private ProgressBar progressBarView;
    private CheckBox level1_CheckBox;
    private CheckBox level2_CheckBox;
    private CheckBox level3_CheckBox;
    private int progressValue=0;

    int completed= WorkListEntry.no;
    Uri currentTaskUri;
    private static int dateYear,dateMonth,dateDay,timeHour,timeMinute;
    private boolean taskHasChanged=false;
    private View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            taskHasChanged=true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        progressBarView=findViewById(R.id.task_progress);
        Intent intent=getIntent();
        currentTaskUri=intent.getData();
        if(currentTaskUri==null){
            setTitle("Create Task");
            progressBarView.setVisibility(View.GONE);
        }
        else{
            setTitle("Edit the Task");
            getSupportLoaderManager().initLoader(loaderId,null,this);
        }

        titleView=findViewById(R.id.title_value);
        dateButtonView=findViewById(R.id.set_date_button);
        dateTextView=findViewById(R.id.set_date_text);
        timeButtonView=findViewById(R.id.set_time_button);
        timeTextView=findViewById(R.id.set_time_text);

        description1View=findViewById(R.id.level1_description);
        description2View=findViewById(R.id.level2_description);
        description3View=findViewById(R.id.level3_description);

        level1_CheckBox=findViewById(R.id.level1_checkbox);
        level2_CheckBox=findViewById(R.id.level2_checkbox);
        level3_CheckBox=findViewById(R.id.level3_checkbox);

        dateButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment= new DatePickerFragment();
                dateFragment.show(getSupportFragmentManager(),"Set Date");
            }
        });

        timeButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment=new TimePickerFragment();
                timeFragment.show(getSupportFragmentManager(),"Set Time");
            }
        });

        titleView.setOnTouchListener(onTouchListener);
        dateButtonView.setOnTouchListener(onTouchListener);
        timeButtonView.setOnTouchListener(onTouchListener);
        description1View.setOnTouchListener(onTouchListener);
        description2View.setOnTouchListener(onTouchListener);
        description3View.setOnTouchListener(onTouchListener);
        level1_CheckBox.setOnTouchListener(onTouchListener);
        level2_CheckBox.setOnTouchListener(onTouchListener);
        level3_CheckBox.setOnTouchListener(onTouchListener);
    }

    public void checkboxClicked(View view) {
        boolean checked= ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.level1_checkbox:
                if(checked){
                    progressValue++;
                    desc1checked=1;
                }
                else{
                    progressValue--;
                    desc1checked=0;
                }
                break;
            case R.id.level2_checkbox:
                if(checked){
                    progressValue++;
                    desc2checked=1;
                }
                else{
                    progressValue--;
                    desc2checked=0;
                }
                break;
            case R.id.level3_checkbox:
                if(checked){
                    progressValue++;
                    desc3checked=1;
                }
                else{
                    progressValue--;
                    desc3checked=0;
                }
                break;
        }
       setProgressValueInProgressBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.editor_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(currentTaskUri==null){
            MenuItem menuItem=menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                try {
                    saveTask();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete:
                showDeleteDialogForm();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showDeleteDialogForm(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do You want to Delete the Task?");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTask();
                finish();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void deleteTask() {
        int rowsDeleted=getContentResolver().delete(currentTaskUri,null,null);
        if(rowsDeleted==0){
            Toast.makeText(getApplicationContext(),R.string.task_deletion_failed,Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),R.string.task_deletion_successful,Toast.LENGTH_SHORT).show();
            cancelAlarm();
        }
    }

    private void saveTask() throws ParseException {

        ContentValues values=new ContentValues();
        String title=titleView.getText().toString().trim();
        String date=dateTextView.getText().toString();
        String time=timeTextView.getText().toString();
        String description1=description1View.getText().toString().trim();
        String description2=description2View.getText().toString().trim();
        String description3=description3View.getText().toString().trim();

        if(progressValue==3){
            completed=1;
        }

        if(TextUtils.isEmpty(title)){
            Toast.makeText(getApplicationContext(),R.string.title_required,Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(date) || TextUtils.isEmpty(time)){
            Toast.makeText(getApplicationContext(),"Task requires Date and Time!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(description1) || TextUtils.isEmpty(description2) || TextUtils.isEmpty(description3)){
            Toast.makeText(getApplicationContext(),R.string.task_description,Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date dateObj=simpleDateFormat.parse(date+" "+time);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(dateObj);

        values.put(WorkListEntry.columnTitle,title);
        values.put(WorkListEntry.columnDate,date);
        values.put(WorkListEntry.columnTime,time);
        values.put(WorkListEntry.columnCompleted,completed);
        values.put(WorkListEntry.columnTimeInMillis,calendar.getTimeInMillis());
        values.put(WorkListEntry.columnDescription1,description1);
        values.put(WorkListEntry.columnDescription2,description2);
        values.put(WorkListEntry.columnDescription3,description3);
        values.put(WorkListEntry.columnDesc1Checked,desc1checked);
        values.put(WorkListEntry.columnDesc2Checked,desc2checked);
        values.put(WorkListEntry.columnDesc3Checked,desc3checked);
        values.put(WorkListEntry.columnProgressValue,progressValue);

        if(currentTaskUri==null){
            Uri uri=getContentResolver().insert(WorkListEntry.contentUri,values);
            if(uri==null){
                Toast.makeText(getApplicationContext(),R.string.task_not_inserted,Toast.LENGTH_SHORT).show();
            }
            else{
                long id= ContentUris.parseId(uri);
                Toast.makeText(getApplicationContext(),R.string.task_inserted,Toast.LENGTH_SHORT).show();
                setAlarm(id);
            }
        }
        else{
            int rowsUpdated =getContentResolver().update(currentTaskUri,values,null,null);
            if(rowsUpdated==0){
                Toast.makeText(getApplicationContext(),R.string.task_not_updated,Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),R.string.task_updated,Toast.LENGTH_SHORT).show();
                if(progressValue==3){
                    cancelAlarm();
                }

            }
        }
        finish();
    }

    private void setAlarm(long id) {
        Intent notifyIntent = new Intent(this, SetNotificationForTaskAlarm.class);
        notifyIntent.putExtra(WorkListEntry.notificationDescription,titleView.getText().toString());
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, (int) id, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if(timeHour<=1){
            dateDay--;
            timeHour=timeHour+24;
        }
        calendar.set(dateYear,dateMonth-1,dateDay,timeHour-2,timeMinute,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),notifyPendingIntent);
    }
    private void cancelAlarm(){
        long id=ContentUris.parseId(currentTaskUri);
        Intent notifyIntent = new Intent(this, SetNotificationForTaskAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) id, notifyIntent,PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this,currentTaskUri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            int titleColumnIndex=data.getColumnIndex(WorkListEntry.columnTitle);
            int completedColumnIndex=data.getColumnIndex(WorkListEntry.columnCompleted);
            int dateColumnIndex=data.getColumnIndex(WorkListEntry.columnDate);
            int timeColumnIndex=data.getColumnIndex(WorkListEntry.columnTime);
            int description1ColumnIndex=data.getColumnIndex(WorkListEntry.columnDescription1);
            int description2ColumnIndex=data.getColumnIndex(WorkListEntry.columnDescription2);
            int description3ColumnIndex=data.getColumnIndex(WorkListEntry.columnDescription3);
            int desc1CheckedColumnIndex=data.getColumnIndex(WorkListEntry.columnDesc1Checked);
            int desc2CheckedColumnIndex=data.getColumnIndex(WorkListEntry.columnDesc2Checked);
            int desc3CheckedColumnIndex=data.getColumnIndex(WorkListEntry.columnDesc3Checked);
            int progressValueColumnIndex=data.getColumnIndex(WorkListEntry.columnProgressValue);

            String title=data.getString(titleColumnIndex);
            completed=data.getInt(completedColumnIndex);
            String date=data.getString(dateColumnIndex);
            String time=data.getString(timeColumnIndex);
            String description1=data.getString(description1ColumnIndex);
            String description2=data.getString(description2ColumnIndex);
            String description3=data.getString(description3ColumnIndex);
            desc1checked=data.getInt(desc1CheckedColumnIndex);
            desc2checked=data.getInt(desc2CheckedColumnIndex);
            desc3checked=data.getInt(desc3CheckedColumnIndex);
            progressValue=data.getInt(progressValueColumnIndex);

            titleView.setText(title);
            dateTextView.setText(date);
            timeTextView.setText(time);
            description1View.setText(description1);
            description2View.setText(description2);
            description3View.setText(description3);
            level1_CheckBox.setChecked(setCheckboxView(desc1checked));
            level2_CheckBox.setChecked(setCheckboxView(desc2checked));
            level3_CheckBox.setChecked(setCheckboxView(desc3checked));
            setProgressValueInProgressBar();
        }

    }
    private boolean setCheckboxView(int val){
        if(val==1){
            return true;
        }
        return false;
    }

    private void setProgressValueInProgressBar() {
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
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        titleView.setText("");
        dateTextView.setText("");
        dateTextView.setHint("DD-MM-YYYY");
        timeTextView.setText("");
        timeTextView.setHint("HH:MM:SS");
        description1View.setText("");
        description2View.setText("");
        description3View.setText("");
        level1_CheckBox.setChecked(false);
        level2_CheckBox.setChecked(false);
        level3_CheckBox.setChecked(false);
        progressValue=0;
        completed=0;
        setProgressValueInProgressBar();
    }

    @Override
    public void onBackPressed() {
        if(!taskHasChanged){
            super.onBackPressed();
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Discard the changes?");
        builder.setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    finish();
                }

            }
        });
        builder.setNegativeButton("KEEP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               if(dialog!=null){
                   dialog.dismiss();
               }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month++;
            //Log.d("date","year:"+String.valueOf(year)+", month:"+String.valueOf(month)+", day:"+String.valueOf(dayOfMonth));
            String date=dayOfMonth+"-"+month+"-"+year;
            dateTextView.setText(date);
            dateYear=year;
            dateMonth=month;
            dateDay=dayOfMonth;

        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            Log.d("time","hour:"+hourOfDay+", min:"+minute);
            String time=hourOfDay+":"+minute+":00";
            timeTextView.setText(time);
            timeHour=hourOfDay;
            timeMinute=minute;
        }
    }

}