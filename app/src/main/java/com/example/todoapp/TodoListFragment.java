package com.example.todoapp;

import android.content.ContentUris;
import android.content.Intent;
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

import com.example.todoapp.data.WorkListContract.WorkListEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TodoListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private int loaderId=0;
    private WorklistCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView= inflater.inflate(R.layout.fragment_todo_list, container, false);
        FloatingActionButton addButton=fragmentView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), EditorActivity.class);
                startActivity(intent);

            }
        });
        ListView listView=fragmentView.findViewById(R.id.list_view);
        View emptyView=fragmentView.findViewById(R.id.todo_empty_view);
        listView.setEmptyView(emptyView);

        adapter=new WorklistCursorAdapter(getContext(),null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),EditorActivity.class);
                Uri currentTaskUri= ContentUris.withAppendedId(WorkListEntry.contentUri,id);
                intent.setData(currentTaskUri);
                startActivity(intent);
            }
        });
        getActivity().getSupportLoaderManager().initLoader(loaderId,null,this);
        return fragmentView;
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
        String[] selectionArgs= new String[] {"0"};
        String sortOrder=WorkListEntry.columnTimeInMillis;
        return new CursorLoader(getContext(),WorkListEntry.contentUri,projection,selection,selectionArgs,sortOrder+ " ASC");
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