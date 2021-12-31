package com.example.unischeduler.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unischeduler.Adapters.TaskAdapter;
import com.example.unischeduler.Models.Task;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.FragmentGeneralBinding;
import com.example.unischeduler.databinding.FragmentHwBinding;

import java.util.ArrayList;

public class HWFragment extends Fragment {

    private FragmentHwBinding binding;

    private TaskAdapter adapter;
    private ArrayList<Task> tasks;
    private ArrayList<String> taskIds;

    public HWFragment() {
    }

    public static HWFragment newInstance(ArrayList<Task> tasks, ArrayList<String> taskIds) {
        HWFragment fragment = new HWFragment();
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
        binding = FragmentHwBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        this.tasks = args.getParcelableArrayList("tasks");
        this.taskIds = args.getStringArrayList("ids");

        String uid = ((UniSchedulerApplication) getActivity().getApplication()).getCurrentUser().getUid();

        adapter = new TaskAdapter(getActivity(), tasks, taskIds, uid);
        binding.rvHW.setAdapter(adapter);
        binding.rvHW.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}