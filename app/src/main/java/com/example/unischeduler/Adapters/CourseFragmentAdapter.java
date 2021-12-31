package com.example.unischeduler.Adapters;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Optional;

public class CourseFragmentAdapter extends FragmentStateAdapter {
    private static final String TAG = "CourseFragmentAdapter";

    private FragmentActivity fragmentActivity;
    private ArrayList<Fragment> fragments;

    public CourseFragmentAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragments) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
        this.fragments = fragments;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public void refreshFragment(int position, Fragment fragment) {
        this.fragments.set(position, fragment);
        notifyItemChanged(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) fragments.get(position).hashCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean containsItem(long itemId) {
        Optional<Fragment> res = fragments.stream().filter(fragment -> (long) fragment.hashCode() == itemId).findAny();
        Log.i(TAG, String.valueOf(res.isPresent()));
        return res.isPresent();
    }
}
