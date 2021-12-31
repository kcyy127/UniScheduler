package com.example.unischeduler.Activities;

import static com.example.unischeduler.Constants.KEY_COLLECTION_SCHEDULES;
import static com.example.unischeduler.Constants.KEY_COLLECTION_TASKS;
import static com.example.unischeduler.Constants.KEY_COLLECTION_USERS;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.unischeduler.Models.Quarter;
import com.example.unischeduler.Models.ScheduleEvent;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityCreateTaskBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CreateTaskActivity extends AppCompatActivity {
    
    private static final String TAG = "CreateTaskActivity";
    
    private ActivityCreateTaskBinding binding;

    private ArrayList<Section> currentSections;
    private ArrayList<String> currentIds;

    private FirebaseFirestore db;

    private UniSchedulerApplication app;

    private ArrayList<String> courseCodes;
    private ArrayAdapter<String> courseAdapter, intervalAdapter;

    private DateTimeFormatter dateFormatter, timeFormatter;
    private HashMap<Integer, Integer> dows;

    private MaterialDatePicker<Long> datePicker;
    private MaterialTimePicker timePicker;

    private static final int TYPE_START_DATE = 63;
    private static final int TYPE_END_DATE = 87;

    private int coursePosition;

    private String taskName;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private String course_affil = "";

    private long interval;
    private LocalDate endDate;
    private int occurrences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_task);
        
        initialize();
        
        setListeners();
        
        setSpinners();

        if (currentSections.size() == 0) {
            getSectionsFromDB();
        } else {
            for (Section section : currentSections) {
                courseCodes.add(section.getCourse().getCode_dep() + " " + section.getCourse().getCode_num());
            }
        }
        
    }

    private void initialize() {
        db = FirebaseFirestore.getInstance();
        app = (UniSchedulerApplication) CreateTaskActivity.this.getApplication();

        currentSections = app.getCurrentSections();
        currentIds = app.getCurrentIds();

        courseCodes = new ArrayList<>(Arrays.asList("None"));

        dateFormatter = DateTimeFormat.forPattern("M/dd/YYYY");
        timeFormatter = DateTimeFormat.forPattern("k:mm");

        dows = new HashMap<>();
        dows.put(R.id.chip0, 7);
        dows.put(R.id.chip1, 1);
        dows.put(R.id.chip2, 2);
        dows.put(R.id.chip3, 3);
        dows.put(R.id.chip4, 4);
        dows.put(R.id.chip5, 5);
        dows.put(R.id.chip6, 6);
    }

    private void getSectionsFromDB() {

        Log.i(TAG, "getSectionsFromDB");
        ArrayList<Section> pastSections = new ArrayList<>();
        ArrayList<String> pastIds = new ArrayList<>();

        db.collection(KEY_COLLECTION_USERS).document(app.getCurrentUser().getUid()).collection(app.getOurUser().getUniversity()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    currentSections.clear();
                    currentIds.clear();
                    pastSections.clear();
                    pastIds.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Section section = document.toObject(Section.class);
                        if (section.getQuarter().equals(app.getCurrentQuarter())) {
                            currentSections.add(section);
                            currentIds.add(document.getId());
                        } else {
                            pastSections.add(section);
                            pastIds.add(document.getId());
                        }
                    }

                    app.setCurrentSections(currentSections);
                    app.setCurrentIds(currentIds);
                    app.setPastSections(pastSections);
                    app.setPastIds(pastIds);

                    for (Section section : currentSections) {
                        courseCodes.add(section.getCourse().getCode_dep() + " " + section.getCourse().getCode_num());
                    }

                    courseAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Could not get user's courses", task.getException());
                }
            }
        });
    }


    private void setListeners() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.cbRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.layoutInputRepeat1.setVisibility(View.VISIBLE);
                    binding.layoutInputRepeat2.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutInputRepeat1.setVisibility(View.GONE);
                    binding.layoutInputRepeat2.setVisibility(View.GONE);
                }
            }
        });

        binding.inputDate.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(TYPE_START_DATE);
            }
        });

        binding.inputEndDate.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(TYPE_END_DATE);
            }
        });

        binding.inputTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // 1. Name
                taskName = binding.etName.getText().toString().trim();
                if (taskName.isEmpty()) {
                    Toast.makeText(CreateTaskActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Due date
                try {
                    dueDate = LocalDate.parse(binding.etDate.getText().toString().trim(), dateFormatter);
                } catch (Exception e) {
                    Toast.makeText(CreateTaskActivity.this, "Could not get date", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Could not get date", e);
                    return;
                }

                // 3. (Optional) Due time
                String rawTime = binding.etTime.getText().toString().trim();
                if (!rawTime.isEmpty()) {
                    try {
                        dueTime = LocalTime.parse(rawTime, timeFormatter);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not parse due time", e);
                    }
                } else {
                    dueTime = new LocalTime(23, 59);
                }

                // 4. Course affiliation
                if (coursePosition!= 0) {
                    course_affil = currentIds.get(coursePosition - 1);
                }

                ArrayList<com.example.unischeduler.Models.Task> tasks = new ArrayList<>();

                if (binding.cbRepeat.isChecked()) {

                    // 5. Repeat interval
                    try {
                        String rawInterval = binding.etInterval.getText().toString().trim();
                        switch (rawInterval) {
                            case "Weekly":
                                interval = 604800L;
                                break;
                            case "Biweekly":
                                interval = 604800L * 2;
                                break;
                            case "Triweekly":
                                interval = 604800L * 3;
                                break;
                            default:
                                interval = 0;
                                break;
                        }

                    } catch (Exception e) {
                        Toast.makeText(CreateTaskActivity.this, "Could not get interval", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Could not get interval", e);
                        return;
                    }

                    // 6-1. End date
                    String rawEndDate = binding.etEndDate.getText().toString().trim();
                    if (rawEndDate.isEmpty()) {
                        endDate = dueDate;
                    } else {
                        try {
                            endDate = LocalDate.parse(rawEndDate, dateFormatter);
                        } catch (Exception e) {
                            Log.e(TAG, "End date format incorrect", e);
                        }
                    }

                    // 6-2. Occurrence
                    String rawOccurrence = binding.etOccurence.getText().toString().trim();
                    if (rawOccurrence.isEmpty()) {
                        occurrences = -1;
                    } else {
                        occurrences = Integer.valueOf(rawOccurrence);
                    }

                    if (endDate.isEqual(dueDate) && occurrences == -1) {
                        Toast.makeText(CreateTaskActivity.this, "Repeat end not defined", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (binding.chipGroup.getCheckedChipIds().size() == 0) {
                        Toast.makeText(CreateTaskActivity.this, "Must choose repeat days", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    for (int chipId : binding.chipGroup.getCheckedChipIds()) {
                        // for each dow we need a scheduleevent
                        LocalDate toStart = com.example.unischeduler.Models.Task.nextOrSame(dueDate, dows.get(chipId));
                        Log.i(TAG, "dow: " + String.valueOf(dows.get(chipId)));

                        com.example.unischeduler.Models.Task temp = new com.example.unischeduler.Models.Task(taskName, dueTime);

                        if (occurrences == -1) {
//                            Log.i(TAG, "addDatesByEnd: " + dateFormatter.print(endDate) + String.valueOf(interval));
                            tasks.addAll(temp.repeatsByDate(toStart, endDate, interval));
                        } else {
//                            Log.i(TAG, "addDatesByOccurrences");
                            tasks.addAll(temp.repeatsByOccurrence(toStart, occurrences, interval));
                        }
                    }
                } else {
                    com.example.unischeduler.Models.Task temp = new com.example.unischeduler.Models.Task(taskName, dueDate, dueTime);
                    tasks.add(temp);
                }

                createTasks(tasks);

            }
        });
    }

    private void setSpinners() {


        courseAdapter = new ArrayAdapter<>(CreateTaskActivity.this, R.layout.item_spinner, R.id.item, courseCodes);
        binding.etCourse.setAdapter(courseAdapter);
        binding.etCourse.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.background_spinner_list));
        coursePosition = 0;
        binding.etCourse.setText(courseCodes.get(coursePosition), false);
        binding.etCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                coursePosition = position;
            }
        });

        ArrayList<String> intervals = new ArrayList<>(Arrays.asList("Weekly", "Biweekly", "Triweekly"));
        intervalAdapter = new ArrayAdapter<>(CreateTaskActivity.this, R.layout.item_spinner, R.id.item, intervals);
        binding.etInterval.setAdapter(intervalAdapter);
        binding.etInterval.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.background_spinner_list));
    }

    private void showDatePicker(int type) {
        if (datePicker == null) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setTheme(R.style.MaterialCalendar_UniScheduler);

            datePicker = builder.build();

            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    DateTimeZone here = DateTimeZone.getDefault();
                    long offset = here.getOffset(Instant.ofEpochMilli(datePicker.getSelection()));

                    Long selectedMilli = datePicker.getSelection() - offset;
                    LocalDate localDate = Instant.ofEpochMilli(selectedMilli).toDateTime().toLocalDate();

                    switch (type) {
                        case TYPE_START_DATE:
                            binding.etDate.setText(dateFormatter.print(localDate));
                            break;
                        case TYPE_END_DATE:
                            binding.etEndDate.setText(dateFormatter.print(localDate));
                            break;
                    }

//                    Toast.makeText(CreateEventActivity.this, datePicker.getSelection().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            datePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    datePicker = null;
                }
            });
        }

        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    private void showTimePicker() {
        if (timePicker == null) {
            MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder().setTheme(R.style.MaterialTimePicker_UniScheduler);
            timePicker = builder.build();

            timePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    timePicker = null;
                }
            });

            timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();

                    binding.etTime.setText(timeFormatter.print(new LocalTime(hour, minute)));
                }
            });


        }

        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    private void createTasks(ArrayList<com.example.unischeduler.Models.Task> tasks) {
        WriteBatch batch = db.batch();

        for (com.example.unischeduler.Models.Task task : tasks) {
            HashMap<String, Object> extra = task.toHashMap();
            extra.put("course_affil", course_affil);
            batch.set(db.collection(KEY_COLLECTION_USERS).document(app.getCurrentUser().getUid()).collection(KEY_COLLECTION_TASKS)
                    .document(), extra);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "batch commit successful");
                    onBackPressed();
                } else {
                    Log.e(TAG, "batch commit failed", task.getException());
                }
            }
        });
    }
}