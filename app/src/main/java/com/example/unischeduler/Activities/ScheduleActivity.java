package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.unischeduler.Adapters.ScheduleFragmentAdapter;
import com.example.unischeduler.Adapters.WeekDateAdapter;
import com.example.unischeduler.Fragments.ScheduleFragment;
import com.example.unischeduler.Fragments.TasksFragment;
import com.example.unischeduler.Models.ScheduleEvent;
import com.example.unischeduler.Models.Task;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.WeekToScheduleInterface;
import com.example.unischeduler.databinding.ActivityScheduleBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import static com.example.unischeduler.Constants.*;


public class ScheduleActivity extends AppCompatActivity implements WeekToScheduleInterface {
    private static final String TAG = "ScheduleActivity";

    private ActivityScheduleBinding binding;

    private LocalDate selectedDate;
    private ArrayList<LocalDate> dates;
    private DateTimeFormatter dateTimeFormatter;

    private WeekDateAdapter dateAdapter;

    private String[] titles = new String[]{"Schedule", "Tasks"};
    private ScheduleFragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragments;

    private FirebaseFirestore db;
    private UniSchedulerApplication app;

    private ArrayList<ScheduleEvent> events;
    private ArrayList<Task> tasks;
    private ArrayList<String> taskIds;

    private DocumentReference ref;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule);

        initialize();

        setViewPager();

        getEventsFromDB();

        getTasksFromDB();

        setListeners();
    }

    private void initialize() {
        app = (UniSchedulerApplication) ScheduleActivity.this.getApplication();

        Intent intent = getIntent();
        selectedDate = app.getSelectedDate();

        db = FirebaseFirestore.getInstance();

        ref = db.collection(KEY_COLLECTION_USERS).document(app.getCurrentUser().getUid());

        events = new ArrayList<>();
        tasks = new ArrayList<>();
        taskIds = new ArrayList<>();


        dateTimeFormatter = DateTimeFormat.forPattern("E, MMM d");
        binding.tvDate.setText(dateTimeFormatter.print(selectedDate).substring(0, 4).toUpperCase() +
                dateTimeFormatter.print(selectedDate).substring(4));

        dates = new ArrayList<>();
        for (int i = (0 - selectedDate.getDayOfWeek()); i< 7-selectedDate.getDayOfWeek(); i++) {
            dates.add(selectedDate.plusDays(i));
        }
        dateAdapter = new WeekDateAdapter(this, dates, selectedDate, this);
        binding.rvWeekDates.setLayoutManager(new GridLayoutManager(this, 7));
        binding.rvWeekDates.setAdapter(dateAdapter);
    }

    // new events and refreshes fragments
    private void getEventsFromDB() {
        ref.collection(KEY_COLLECTION_SCHEDULES).whereArrayContains("dates", toUnix(selectedDate))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    events.clear();
                    events.addAll(task.getResult().toObjects(ScheduleEvent.class));
                    for (ScheduleEvent event : events) {
                        Log.i(TAG, event.getName());
                    }
                    ScheduleFragment newFragment = ScheduleFragment.newInstance(events);
                    fragmentAdapter.refreshFragment(0, newFragment);
                } else {
                    Log.i(TAG, "Could not get schedule events");
                }
            }
        });
    }

    private void getTasksFromDB() {
        ref.collection(KEY_COLLECTION_TASKS)
                .whereGreaterThanOrEqualTo("date", toUnix(selectedDate)).whereLessThan("date", toUnix(selectedDate.plusDays(7)))
                .orderBy("date")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    tasks.clear();
                    taskIds.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        tasks.add(document.toObject(Task.class));
                        taskIds.add(document.getId());
                    }

                    TasksFragment newFragment = TasksFragment.newInstance(tasks, taskIds);
                    fragmentAdapter.refreshFragment(1, newFragment);
                } else {
                    Log.e(TAG, "Could not get tasks", task.getException());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setViewPager() {
        fragments = new ArrayList<>();
        fragments.add(ScheduleFragment.newInstance(this.events));
        fragments.add(TasksFragment.newInstance(this.tasks, this.taskIds));

        fragmentAdapter = new ScheduleFragmentAdapter(this, fragments);
        binding.viewPager.setAdapter(fragmentAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(titles[position])).attach();
    }

    private void setListeners() {
        binding.btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScheduleActivity.this, AccountActivity.class));
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (binding.viewPager.getCurrentItem()) {
                    case 0:
                        startActivity(new Intent(ScheduleActivity.this, CreateEventActivity.class));
                        break;
                    case 1:
                        // change to create task activity
                        startActivity(new Intent(ScheduleActivity.this, CreateTaskActivity.class));
                        break;
                }
            }
        });
    }

    // WeekToScheduleInterface
    @Override
    public void startHomeActivity() {
        Intent intent = new Intent(ScheduleActivity.this, HomeActivity.class);
        intent.putExtra("from", "ScheduleActivity");
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void changeDate(LocalDate newDate) {
        updateAppSelectedDate(newDate);
        binding.tvDate.setText(dateTimeFormatter.print(selectedDate).substring(0, 4).toUpperCase() +
                dateTimeFormatter.print(selectedDate).substring(4));
        dateAdapter.notifyDataSetChanged();
    }

    private void updateAppSelectedDate(LocalDate date) {
        ((UniSchedulerApplication) ScheduleActivity.this.getApplication()).setSelectedDate(date);
        selectedDate = ((UniSchedulerApplication) ScheduleActivity.this.getApplication()).getSelectedDate();
        getEventsFromDB();
        getTasksFromDB();
    }

    private long toUnix(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.toLocalDateTime(new LocalTime(0, 0));
        Instant instant = Instant.parse(localDateTime.toString());
        return instant.getMillis() / 1000;
    }
}