package com.example.unischeduler.Adapters;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.example.unischeduler.Activities.ScheduleActivity;
import com.example.unischeduler.Fragments.ScheduleFragment;
import com.example.unischeduler.Fragments.TasksFragment;
import com.example.unischeduler.Models.ScheduleEvent;
import com.example.unischeduler.Models.Task;
import com.example.unischeduler.R;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ScheduleFragmentAdapter extends FragmentStateAdapter {

    private static final String TAG = "ScheduleFragmentAdapter";

    private FragmentActivity fragmentActivity;

    private ArrayList<Fragment> fragments;

    public ScheduleFragmentAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragments) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
        this.fragments = fragments;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i(TAG, "createFragment called");
        switch (position) {
            case 0:
                return fragments.get(0);
            case 1:
                return fragments.get(1);
        }
        return fragments.get(0);
    }

    public void refreshFragment(int position, Fragment fragment) {
        this.fragments.set(position, fragment);
        notifyItemChanged(position);
    }


    @Override
    public int getItemCount() {
        return fragments.size();
    }


    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return (long) fragments.get(position).hashCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean containsItem(long itemId) {
//        return super.containsItem(itemId);
        Optional<Fragment> res = fragments.stream().filter(fragment -> (long) fragment.hashCode() == itemId).findAny();
        Log.i(TAG, String.valueOf(res.isPresent()));
        return res.isPresent();
    }
}
