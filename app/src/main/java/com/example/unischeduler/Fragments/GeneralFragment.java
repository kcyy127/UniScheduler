package com.example.unischeduler.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unischeduler.Adapters.GeneralFragmentTimesAdapter;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.R;
import com.example.unischeduler.databinding.FragmentGeneralBinding;

public class GeneralFragment extends Fragment {

    private static final String TAG = "GeneralFragment";

    private FragmentGeneralBinding binding;

    public GeneralFragment() {}

    public static GeneralFragment newInstance(Section section) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putParcelable("section", section);
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
        binding = FragmentGeneralBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        Section section = args.getParcelable("section");
        Log.i(TAG, section.getCourse().getName());

        binding.tvInstructorName.setText(section.getInstructor().getLast_name() + ", " + section.getInstructor().getFirst_name());
        binding.tvInstructorEmail.setText(section.getInstructor().getEmail());

        GeneralFragmentTimesAdapter adapter = new GeneralFragmentTimesAdapter(getActivity(), section);
        binding.rvTimes.setAdapter(adapter);
        binding.rvTimes.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }
}