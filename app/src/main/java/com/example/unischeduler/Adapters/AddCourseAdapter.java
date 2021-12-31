package com.example.unischeduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.AddCourseInterface;
import com.example.unischeduler.Models.Course;
import com.example.unischeduler.databinding.ItemCourseBinding;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class AddCourseAdapter extends RecyclerView.Adapter<AddCourseAdapter.AddCourseViewHolder> {


    private Context context;
    private ArrayList<Course> courses;
    private AddCourseInterface iface;
    private HashMap<Integer, Integer> selected; // course_position : section_position

    public AddCourseAdapter(Context context, ArrayList<Course> courses, AddCourseInterface iface) {
        this.context = context;
        this.courses = courses;
        this.iface = iface;
        this.selected = new HashMap<>();
    }

    @NonNull
    @Override
    public AddCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCourseBinding binding = ItemCourseBinding.inflate(inflater, parent, false);
        return new AddCourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddCourseViewHolder holder, int position) {
        Course course = courses.get(position);

        if (selected.containsKey(position)) {
            holder.bindSelected(course);
        } else {
            holder.bindUnselected(course);
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void addSelected(int course, int section) {
        selected.put(course, section);
        notifyItemChanged(course);
    }

    public HashMap<Integer, Integer> getSelected() {
        return selected;
    }

    public class AddCourseViewHolder extends RecyclerView.ViewHolder {

        ItemCourseBinding binding;

        public AddCourseViewHolder(@NonNull ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindSelected(Course course) {
            binding.tvCode.setText(course.getCode_dep() + " " + course.getCode_num());
            binding.tvName.setText(course.getName());

            binding.tvSection.setVisibility(View.VISIBLE);
            binding.tvSection.setText("SECTION: " +
                    String.valueOf(selected.get(getAdapterPosition())));

            binding.cvContainer.setStrokeWidth(8);

            binding.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iface.onCourseClicked(getAdapterPosition());
                }
            });

        }

        public void bindUnselected(Course course) {
            binding.tvCode.setText(course.getCode_dep() + " " + course.getCode_num());
            binding.tvName.setText(course.getName());



            binding.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iface.onCourseClicked(getAdapterPosition());
                }
            });

        }
    }
}
