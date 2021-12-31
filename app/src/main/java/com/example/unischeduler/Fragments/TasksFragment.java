package com.example.unischeduler.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unischeduler.Adapters.TaskAdapter;
import com.example.unischeduler.Models.Task;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.FragmentScheduleBinding;
import com.example.unischeduler.databinding.FragmentTasksBinding;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;

public class TasksFragment extends Fragment {
    private static final String TAG = "TasksFragment";
    private FragmentTasksBinding binding;

    private TaskAdapter upcomingAdapter;
    private ArrayList<Task> tasks;
    private ArrayList<String> taskIds;

    private UniSchedulerApplication app;

    public TasksFragment() {}

    public static TasksFragment newInstance(ArrayList<Task> tasks, ArrayList<String> taskIds) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("tasks", tasks);
        args.putStringArrayList("ids", taskIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        this.tasks = args.getParcelableArrayList("tasks");
        this.taskIds = args.getStringArrayList("ids");

        app = (UniSchedulerApplication) getActivity().getApplication();

        Log.i(TAG, String.valueOf(tasks.size()));

//        ArrayList<Task> tempList = new ArrayList<>(Arrays.asList(
//                new Task("Walk dog", LocalDate.now(), new LocalTime(23,59)),
//                new Task("Dishes", LocalDate.now(), new LocalTime(23,59)),
//                new Task("Assignment", LocalDate.now(), new LocalTime(23,59))
//        ));

        upcomingAdapter = new TaskAdapter(getActivity(), tasks, taskIds, app.getCurrentUser().getUid());
        binding.rvUpcoming.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvUpcoming.setAdapter(upcomingAdapter);

    }
}