package com.example.todoapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.todoapp.data.WorkListContract.WorkListEntry;

public class CompletedListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private int loaderId=1;
    private WorklistCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView= inflater.inflate(R.layout.fragment_completed_list, container, false);

        View emptyView=fragmentView.findViewById(R.id.completed_empty_view);
        ListView listView=fragmentView.findViewById(R.id.list_view);
        listView.setEmptyView(emptyView);
        adapter=new WorklistCursorAdapter(getContext(),null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialogForm(id);
            }
        });
        getActivity().getSupportLoaderManager().initLoader(loaderId,null,this);
        return fragmentView;
    }

    private void showDeleteDialogForm(final long id){
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage("Do You want to Delete the Task?");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTask(id);
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void deleteTask(long id) {
        Uri currentTaskUri= ContentUris.withAppendedId(WorkListEntry.contentUri,id);
        int rowsDeleted=getContext().getContentResolver().delete(currentTaskUri,null,null);
        if(rowsDeleted==0){
            Toast.makeText(getContext(),R.string.task_deletion_failed,Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),R.string.task_deletion_successful,Toast.LENGTH_SHORT).show();
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection={
                WorkListEntry.columnId,
                WorkListEntry.columnTitle,
                WorkListEntry.columnDate,
                WorkListEntry.columnTime,
                WorkListEntry.columnProgressValue,
                WorkListEntry.columnTimeInMillis
        };
        String selection= WorkListEntry.columnCompleted+"=?";
        String[] selectionArgs= new String[] {"1"};
        return new CursorLoader(getContext(),WorkListEntry.contentUri,projection,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data!=null){
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}