package com.example.unischeduler.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.Models.Task;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ItemCourseBinding;
import com.example.unischeduler.databinding.ItemTaskBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import static com.example.unischeduler.Constants.*;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private static final String TAG = "TaskAdapter";

    private static final int STATUS_UNSTARTED = 0;
    private static final int STATUS_IN_PROGRESS = 1;
    private static final int STATUS_COMPLETED = 2;

    private Context context;
    private ArrayList<Task> tasks;
    private ArrayList<String> taskIds;

    private DateTimeFormatter formatter;

    private FirebaseFirestore db;
    private String uid;

    public TaskAdapter(Context context, ArrayList<Task> tasks, ArrayList<String> taskIds, String uid) {
        this.context = context;
        this.tasks = tasks;
        this.taskIds = taskIds;
        this.uid = uid;
        formatter = DateTimeFormat.forPattern("M/d/YYYY k:mm");
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTaskBinding binding = ItemTaskBinding.inflate(inflater, parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position), taskIds.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        ItemTaskBinding binding;

        public TaskViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task, String id) {
            binding.tvName.setText(task.getName());
            LocalDateTime localDateTime = Instant.ofEpochSecond(task.getDate() + task.getTime()).toDateTime().toLocalDateTime();
            binding.tvDesc.setText(formatter.print(localDateTime));
            binding.cbStatus.setState(task.getStatus());

            DocumentReference ref = db.collection(KEY_COLLECTION_USERS).document(uid).collection(KEY_COLLECTION_TASKS).document(id);

            binding.cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    switch (binding.cbStatus.getState()) {
                        case STATUS_UNSTARTED:
                            Log.i(TAG, id + " changed to in progress");
                            binding.cbStatus.setState(STATUS_IN_PROGRESS);
                            ref.update("status", STATUS_IN_PROGRESS);
                            break;
                        case STATUS_IN_PROGRESS:
                            Log.i(TAG, id + " changed to completed");
                            binding.cbStatus.setState(STATUS_COMPLETED);
                            ref.update("status", STATUS_COMPLETED);
                            break;
                        case STATUS_COMPLETED:
                            Log.i(TAG, id + " back to unstarted");
                            binding.cbStatus.setState(STATUS_UNSTARTED);
                            ref.update("status", STATUS_UNSTARTED);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
}
