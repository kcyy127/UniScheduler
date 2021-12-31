package com.example.unischeduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.Activities.IndividualCourseActivity;
import com.example.unischeduler.Models.Course;
import com.example.unischeduler.Models.ScheduleEvent;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.databinding.ItemCourseBinding;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private ArrayList<Section> sections;
    private ArrayList<String> ids;

    public CourseAdapter(Context context, ArrayList<Section> sections, ArrayList<String> ids) {
        this.context = context;
        this.sections = sections;
        this.ids = ids;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCourseBinding binding = ItemCourseBinding.inflate(inflater, parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.bind(sections.get(position), ids.get(position));
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        ItemCourseBinding binding;
        Context context;

        public CourseViewHolder(@NonNull ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = CourseAdapter.this.context;
        }

        public void bind(Section section, String id) {
            Course course = section.getCourse();
            binding.tvCode.setText(course.getCode_dep() + " " + course.getCode_num());
            binding.tvName.setText(course.getName());
            binding.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, IndividualCourseActivity.class);
                    intent.putExtra("section", section);
                    intent.putExtra("id", id);
                    context.startActivity(intent);
                }
            });
        }
    }
}
