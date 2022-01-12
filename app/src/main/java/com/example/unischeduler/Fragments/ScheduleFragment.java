package com.example.unischeduler.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.unischeduler.Activities.ScheduleActivity;
import com.example.unischeduler.Adapters.HourAdapter;
import com.example.unischeduler.Models.ScheduleEvent;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.FragmentScheduleBinding;
import com.example.unischeduler.databinding.ItemEventBinding;

import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private static final String TAG = "ScheduleFragment";

    private List<ScheduleEvent> events;
    private ArrayList<String> eventIds;

    private FragmentScheduleBinding binding;
    float density;

    private FirebaseFirestore db;
    private String userId;

    private DateTimeFormatter dateTimeFormatter;

    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(ArrayList<ScheduleEvent> events, ArrayList<String> eventIds) {


        if (events.size() == 0) {
            Log.i(TAG, "newInstance: events is 0");
        } else {
            for (ScheduleEvent event : events) {
                Log.i(TAG, "newInstance: " + event.getName());
            }
        }

        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("events", events);
        args.putStringArrayList("ids", eventIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        userId = ((UniSchedulerApplication) getActivity().getApplication()).getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated called");
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        this.events = args.getParcelableArrayList("events");
        this.eventIds = args.getStringArrayList("ids");

        if (this.events.size() == 0) {
            Log.i(TAG, "events size is 0");
        } else {
            for (ScheduleEvent event : events) {
                Log.i(TAG, "onViewCreated: " + event.getName());
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;

        dateTimeFormatter = DateTimeFormat.forPattern("k:mm");

        setHours();

        drawEvents();
    }

    private void drawEvents() {
//        int scrollTo = binding.container.getBottom();
        for (int i = 0; i < events.size(); i++) {
            ScheduleEvent event = events.get(i);
            int position = i;

            Log.i(TAG, "drawing " + event.getName());
            ItemEventBinding eventBinding = ItemEventBinding.inflate(getLayoutInflater(), binding.container, false);
            eventBinding.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Clicked " + event.getName());
                }
            });

            eventBinding.cvContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((ScheduleActivity) getActivity()).showDeleteEventDialog(position);
                    return true;
                }
            });

            eventBinding.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ScheduleActivity) getActivity()).showEventDetails(position);
                }
            });

            int categoryColor;
            if (event.getCourse_affil().equals("")) {
                categoryColor = ContextCompat.getColor(getActivity(), R.color.my_blue);
            } else {
                categoryColor = ContextCompat.getColor(getActivity(), R.color.my_red);
            }

            eventBinding.ivColor.setColorFilter(categoryColor);
            eventBinding.tvHeading.setText(event.getName());
            eventBinding.tvDesc.setText(formatTime(event.getTime())+ " | " + formatDuration(event.getDuration()) + " | " + event.getLocation());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1600, (int) Math.ceil(80 * event.getDuration() / 3600.0 * density));
            params.leftMargin = 140;

            int topPosition = (int) Math.ceil((80 * (event.getTime() / 3600.0 + 1)  - 10) * density);
            params.topMargin = topPosition;
//            if (topPosition < scrollTo) {
//                scrollTo = topPosition;
//            }

            binding.canvas.addView(eventBinding.getRoot(), params);

        }
//        int finalScrollTo = scrollTo;
        binding.container.post(new Runnable() {
            @Override
            public void run() {
                binding.container.scrollTo(0, (int) (80 * 7 * density));
            }
        });
    }

    private void setHours() {

        ArrayList<String> hours = new ArrayList<>();
        hours.add("12 PM");
        String[] ampm = new String[]{"AM", "PM"};
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j <= 12; j++) {
                hours.add(j + " " + ampm[i]);
            }
        }

        HourAdapter adapter = new HourAdapter(getContext(), hours);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvHours.setLayoutManager(layoutManager);
        binding.rvHours.setAdapter(adapter);

    }

    private String formatDuration(long unixMinutes) {
        int minutes = (int) unixMinutes / 60;
        int hours = minutes / 60;
        int minutes_left = minutes % 60;
        if (hours == 0) {
            return String.valueOf(minutes_left) + " m";
        } else if (minutes_left == 0) {
            return String.valueOf(hours) + " hr";
        } else {
            return String.valueOf(hours) + " hr " + String.valueOf(minutes_left) + " m";
        }

    }

    private String formatTime(long unix) {
        int minutes = (int) (unix / 60);
        int hour = minutes / 60;
        int minutes_left = minutes % 60;
        return String.format("%d:%02d", hour, minutes_left);
    }
}