package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.unischeduler.Models.Course;
import com.example.unischeduler.Models.Instructor;
import com.example.unischeduler.Models.Quarter;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.MonthToHomeInterface;
import com.example.unischeduler.Adapters.MonthAdapter;
import com.example.unischeduler.CenterSmoothScroller;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityHomeBinding;
import static com.example.unischeduler.Constants.*;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements MonthToHomeInterface {
    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;

    public static final String INTENT_KEY_SELECTED_DATE = "selected_date";

    public MutableLiveData<LocalDate> selectedDate;

    private DateTimeFormatter dateTimeFormatter;
    private LinearLayoutManager linearLayoutManager;
    private MonthAdapter monthAdapter;
    private ArrayList<LocalDate> months;
    private int oldAdapterPosition;
    private RecyclerView.OnScrollListener endlessListener;

    private Intent launchIntent;

    private FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        initialize();

        createCalendar();

        setListeners();
    }

    private void initialize() {

        oldAdapterPosition = 0;

        launchIntent = getIntent();

        db = FirebaseFirestore.getInstance();

        new PopulateUniDBAsyncTask().execute();
    }

    public void createCalendar() {
        dateTimeFormatter = DateTimeFormat.forPattern("E, MMM d");

        months = new ArrayList<>();
        LocalDate startDate = new LocalDate(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear(), 1);
        for (int i = -3; i <= 3; i++) {
            months.add(startDate.plusMonths(i));
        }

        monthAdapter = new MonthAdapter(this, months, this);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.rvCalendar.setLayoutManager(linearLayoutManager);
        binding.rvCalendar.setAdapter(monthAdapter);

        selectedDate = new MutableLiveData<>();
        selectedDate.observe(this, new Observer<LocalDate>() {
            @Override
            public void onChanged(LocalDate localDate) {
                binding.tvDate.setText(dateTimeFormatter.print(selectedDate.getValue()).substring(0, 4).toUpperCase() +
                        dateTimeFormatter.print(selectedDate.getValue()).substring(4));
                monthAdapter.changeSelectedDateFromActivity(selectedDate.getValue());
                //old value position
                monthAdapter.notifyItemChanged(oldAdapterPosition);
                int presentPosition = months.indexOf(new LocalDate(selectedDate.getValue().getYear(), selectedDate.getValue().getMonthOfYear(), 1));
                monthAdapter.notifyItemChanged(presentPosition);
                oldAdapterPosition = presentPosition;
            }
        });
        selectedDate.setValue(((UniSchedulerApplication) HomeActivity.this.getApplication()).getSelectedDate());

        if (launchIntent.getStringExtra("from") != null && launchIntent.getStringExtra("from").equals("ScheduleActivity")) {
            binding.rvCalendar.scrollToPosition(getPosition(selectedDate.getValue()));
        } else {
//            CenterSmoothScroller smoothScroller = getSmoothScroller(selectedDate.getValue());
//            linearLayoutManager.startSmoothScroll(smoothScroller);
            binding.rvCalendar.scrollToPosition(getPosition(selectedDate.getValue()));
        }

        endlessListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int posTop = linearLayoutManager.findFirstVisibleItemPosition();
                int posBottom = linearLayoutManager.findLastVisibleItemPosition();

                if (posTop == 0) {
                    Log.i(TAG, "at the top");
                    LocalDate origStart = months.get(0);
                    for (int i = -1; i > -4; i--) {
                        months.add(0, origStart.plusMonths(i));
                    }
                    binding.rvCalendar.post(new Runnable() {
                        @Override
                        public void run() {
                            monthAdapter.notifyItemRangeInserted(0, 3);
                        }
                    });
                }

                if (posBottom == months.size() - 1) {
                    Log.i(TAG, "at the bottom");
                    LocalDate origStart = months.get(months.size() - 1);
                    for (int i = 1; i < 4; i++) {
                        months.add(origStart.plusMonths(i));
                    }
                    binding.rvCalendar.post(new Runnable() {
                        @Override
                        public void run() {
                            monthAdapter.notifyItemRangeInserted(months.size() - 4, 3);
                        }
                    });
                }
            }
        };

        binding.rvCalendar.addOnScrollListener(endlessListener);

    }

    private void setListeners() {
        binding.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppSelectedDate(LocalDate.now());
                CenterSmoothScroller smoothScroller = getSmoothScroller(selectedDate.getValue());
                linearLayoutManager.startSmoothScroll(smoothScroller);
                Log.i(TAG, "tvDate clicked");
            }
        });

        binding.btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AccountActivity.class));
            }
        });
    }

    public int getPosition(LocalDate date) {
        return months.indexOf(
                new LocalDate(date.getYear(), date.getMonthOfYear(), 1));
    }

    public CenterSmoothScroller getSmoothScroller(LocalDate date) {
        CenterSmoothScroller smoothScroller = new CenterSmoothScroller(binding.rvCalendar.getContext());
        smoothScroller.setTargetPosition(getPosition(date));
        return smoothScroller;
    }

    private void updateAppSelectedDate(LocalDate date) {
        ((UniSchedulerApplication) HomeActivity.this.getApplication()).setSelectedDate(date);
        selectedDate.setValue(((UniSchedulerApplication)HomeActivity.this.getApplication()).getSelectedDate());
    }

    // MonthToHomeInterface
    @Override
    public void changeSelectedDate(LocalDate newDate) {
//        this.selectedDate = selectedDate;
//        this.selectedDate.setValue(selectedDate);
        updateAppSelectedDate(newDate);
        this.oldAdapterPosition = months.indexOf(new LocalDate(newDate.getYear(), newDate.getMonthOfYear(), 1));
    }

    @Override
    public void startScheduleActivity() {
        Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
//        intent.putExtra("selected_date", selectedDate.getValue());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    private class PopulateUniDBAsyncTask extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected Void doInBackground(Void... voids) {
//            addUnis();

//            addQuarters();

//            addCourses();

//            addInstructors();

//            addSections();

            return null;
        }

        private void addUnis() {
//            String[] uniNames = new String[]{"University of California, Santa Barbara",
//                    "Pomona College", "Harvey Mudd College", "Swarthmore College", "Lafayette College"};
//            db.collection("universities").add(new University("University of California, San Diego", "quarter"));
//            db.collection("universities").document().set(new University("University of Washington", "quarter"));
//            WriteBatch batch = db.batch();
//            DocumentReference docRef = db.collection("universities").document();
//            for (int i = 0; i< uniNames.length; i++) {
//                batch.set(db.collection("universities").document(), new University(uniNames[i]));
//            }
//            batch.set(db.collection("universities").document(), new University("University of California, San Diego", "quarter"));
//            batch.set(db.collection("universities").document(), new University("San Diego State University", "semester"));
//            batch.commit();
        }

        private void addQuarters() {
            String ucsd_id = "5J53UPI3xix9WL0KXDSu";
            WriteBatch batch = db.batch();
            ArrayList<Quarter> quarters = new ArrayList<>();
            quarters.add(new Quarter(2021, "fall",
                    new LocalDate(2021, 9, 23),
                    new LocalDate(2021, 12, 3)));
            quarters.add(new Quarter(2022, "winter",
                    new LocalDate(2022, 1, 3),
                    new LocalDate(2022, 3, 11)));
            quarters.add(new Quarter(2022, "spring",
                    new LocalDate(2022, 3, 28),
                    new LocalDate(2022, 6, 3)));
            quarters.add(new Quarter(2022, "summer_i",
                    new LocalDate(2022, 6, 27),
                    new LocalDate(2022, 7, 29)));
            quarters.add(new Quarter(2022, "summer_ii",
                    new LocalDate(2022, 8, 1),
                    new LocalDate(2022, 9, 2)));

            for (int i = 0; i< quarters.size(); i++) {
                batch.set(db.collection("universities").document(ucsd_id).collection("quarters").document(),
                        quarters.get(i));
            }
            batch.commit();
        }

        private void addCourses() {

            ArrayList<Course> courses = new ArrayList<Course>();
            courses.add(new Course("Honors Linear Algebra", "MATH", "31AH"));
            courses.add(new Course("Honors Multivariable Calculus", "MATH", "31BH"));
            courses.add(new Course("Honors Vector Calculus", "MATH", "31CH"));
            courses.add(new Course("Introduction to Differential Equations", "MATH", "20D"));
            courses.add(new Course("Introduction to Programming and Computational Problem-Solving I",
                    "CSE", "8A"));
            courses.add(new Course("Introduction to Programming and Computational Problem-Solving II",
                    "CSE", "8B"));
            courses.add(new Course("Basic Data Structures and Object-Oriented Design", "CSE", "12"));
            courses.add(new Course("Computer Organization and Systems Programming", "CSE", "30"));
            courses.add(new Course("Debating Multiculturalism: Race, Ethnicity, and Class in American Societies", "ANTH", "23"));
            courses.add(new Course("Perspectives on a Changing Planet", "SYN", "1"));
            courses.add(new Course("Explorations of a Changing Planet", "SYN", "2"));
            courses.add(new Course("Financial Accounting", "MGT", "4"));
            courses.add(new Course("Managerial Accounting", "MGT", "5"));

            WriteBatch batch = db.batch();

//            for (int i = 0; i < courses.size(); i++) {
//                batch.set(db.collection(KEY_COLLECTION_UNIVERSITIES).document(KEY_UCSD_DOC_ID).collection(KEY_COLLECTION_COURSES).document(), courses.get(i));
//            }

            batch.commit();
        }

        private void addInstructors() {

            ArrayList<Instructor> instructors = new ArrayList<>();
            instructors.add(new Instructor("Horseman", "Butterscotch"));
            instructors.add(new Instructor("Carson", "Penny"));
            instructors.add(new Instructor("Fuzzyface", "Meow Meow"));
            instructors.add(new Instructor("Stilton", "Ralph"));
            instructors.add(new Instructor("Sugarman", "Honey"));

            WriteBatch batch = db.batch();

//            for (int i = 0; i < instructors.size(); i++) {
//                batch.set(db.collection(KEY_COLLECTION_UNIVERSITIES).document(KEY_UCSD_DOC_ID).collection(KEY_COLLECTION_INSTRUCTORS).document(),
//                        instructors.get(i));
//            }

            batch.commit();
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        private void addSections() {
            Log.i(TAG, "addSections running");

            Random random = new Random();


            String uniDocId = null;
            while (uniDocId == null) {
                uniDocId = ((UniSchedulerApplication) HomeActivity.this.getApplication()).getUniDocId();
            }
            Log.i(TAG, "unidocid is " + uniDocId);

            Quarter winter22 = new Quarter(2022, "winter",
                    new LocalDate(2022, 1, 3), new LocalDate(2022, 3, 11));

            ArrayList<Course> courses = new ArrayList<>();
            courses.add(new Course("Honors Linear Algebra", "MATH", "31AH"));
            courses.add(new Course("Honors Multivariable Calculus", "MATH", "31BH"));
            courses.add(new Course("Honors Vector Calculus", "MATH", "31CH"));
            courses.add(new Course("Introduction to Differential Equations", "MATH", "20D"));
            courses.add(new Course("Introduction to Programming and Computational Problem-Solving I",
                    "CSE", "8A"));
            courses.add(new Course("Introduction to Programming and Computational Problem-Solving II",
                    "CSE", "8B"));
            courses.add(new Course("Basic Data Structures and Object-Oriented Design", "CSE", "12"));
            courses.add(new Course("Computer Organization and Systems Programming", "CSE", "30"));
            courses.add(new Course("Debating Multiculturalism: Race, Ethnicity, and Class in American Societies", "ANTH", "23"));
            courses.add(new Course("Perspectives on a Changing Planet", "SYN", "1"));
            courses.add(new Course("Explorations of a Changing Planet", "SYN", "2"));
            courses.add(new Course("Financial Accounting", "MGT", "4"));
            courses.add(new Course("Managerial Accounting", "MGT", "5"));

            ArrayList<Instructor> instructors = new ArrayList<>();
            instructors.add(new Instructor("Horseman", "Butterscotch"));
            instructors.add(new Instructor("Carson", "Penny"));
            instructors.add(new Instructor("Fuzzyface", "Meow Meow"));
            instructors.add(new Instructor("Stilton", "Ralph"));
            instructors.add(new Instructor("Sugarman", "Honey"));

            ArrayList<Section> sections = new ArrayList<>();

            ArrayList<String> buildings = new ArrayList<>(Arrays.asList("PETER", "CENTR", "APM", "GH", "MANDE", "MOS", "WLH", "WFH", "EBU3B"));
            ArrayList<String> rooms = new ArrayList<>(Arrays.asList("101", "110", "120", "105", "20", "402", "B412"));

            Log.i(TAG, "done initializing");


            for (int i = 0; i < 15; i++) {

                double a = Math.random();
                double b = Math.random();
                double c = Math.random();
                double d = Math.random();
                double e = Math.random();
                double f = Math.random();

                Section section = new Section(courses.get((int) (a * courses.size())), winter22, instructors.get((int) (b * instructors.size())));
                section.addSchedule("LE", buildings.get((int) (c * buildings.size())), rooms.get((int) (d * rooms.size())),
                        "135", new LocalTime(random.nextInt(18 - 8) + 8, 0), 50);
                section.addSchedule("DI", buildings.get((int) (e * buildings.size())), rooms.get((int) (f * rooms.size())),
                        String.valueOf(random.nextInt(6 - 1) + 1),
                        new LocalTime(random.nextInt(18 - 8) + 8, 0), 50);

                sections.add(section);
            }

            Log.i(TAG, "first part done");

            for (int i = 0; i < 5; i++) {

                double a = Math.random();
                double b = Math.random();
                double c = Math.random();
                double d = Math.random();
                double e = Math.random();
                double f = Math.random();

                Section section = new Section(courses.get((int) (a * courses.size())), winter22, instructors.get((int) (b * instructors.size())));
                section.addSchedule("LE", buildings.get((int) (c * buildings.size())), rooms.get((int) (d * rooms.size())),
                        "24", new LocalTime(random.nextInt(18 - 8) + 8, 0), 80);
                section.addSchedule("DI", buildings.get((int) (e * buildings.size())), rooms.get((int) (f * rooms.size())),
                        String.valueOf(random.nextInt(6 - 1) + 1),
                        new LocalTime(random.nextInt(18 - 8) + 8, 0), 50);
                sections.add(section);
            }

            Log.i(TAG, "second part done");
            Log.i(TAG, String.valueOf(sections.size()));

            WriteBatch batch = db.batch();
            for (int j = 0; j < sections.size(); j++) {
                batch.set(db.collection(KEY_COLLECTION_UNIVERSITIES).document(uniDocId).collection(KEY_COLLECTION_SECTIONS).document(),
                        sections.get(j));
            }
            batch.commit();
        }
    }


}