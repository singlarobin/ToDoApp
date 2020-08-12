package com.example.todoapp;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new TodoListFragment();
        }
        else{
            return new CompletedListFragment();
        }
    }

    public String getPageTitle(int position){
        if(position==0){
            return "Incomplete";
        }
        else{
            return "Completed";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
