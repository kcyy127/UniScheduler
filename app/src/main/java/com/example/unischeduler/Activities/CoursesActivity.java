package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.unischeduler.Adapters.CourseAdapter;
import com.example.unischeduler.Models.Course;
import com.example.unischeduler.Models.Quarter;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.Models.User;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityCoursesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.unischeduler.Constants.*;

public class CoursesActivity extends AppCompatActivity {

    private static final String TAG = "CoursesActivity";

    private ActivityCoursesBinding binding;

    private ArrayList<Section> currentSections, pastSections;
    private ArrayList<String> currentIds, pastIds;

    private CourseAdapter currentAdapter, pastAdapter;

    private UniSchedulerApplication app;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private User ourUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_courses);

        initialize();

        setListeners();

        setAdapters();

        getFromDB();
    }

    private void initialize() {
        app = (UniSchedulerApplication) CoursesActivity.this.getApplication();

        currentSections = app.getCurrentSections();
        pastSections = app.getPastSections();
        currentIds = app.getCurrentIds();
        pastIds = app.getPastIds();

        db = FirebaseFirestore.getInstance();
        firebaseUser = app.getCurrentUser();
        ourUser = app.getOurUser();

        if (currentSections.size() == 0 && pastSections.size() == 0) {
            getFromDB();
        }

        Intent intent = getIntent();
        if (intent!=null) {
            if (intent.getBooleanExtra("refresh", false)) {
                getFromDB();
            }
        }
    }

    private void setListeners() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoursesActivity.this, AddCourseActivity.class));
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoursesActivity.this, AccountActivity.class));
            }
        });
    }

    private void setAdapters() {

        currentAdapter = new CourseAdapter(CoursesActivity.this, currentSections, currentIds);
        binding.rvCurrent.setLayoutManager(new LinearLayoutManager(CoursesActivity.this));
        binding.rvCurrent.setAdapter(currentAdapter);

        pastAdapter = new CourseAdapter(CoursesActivity.this, pastSections, pastIds);
        binding.rvPast.setLayoutManager(new LinearLayoutManager(CoursesActivity.this));
        binding.rvPast.setAdapter(pastAdapter);
    }

    private void getFromDB() {
        Quarter currentQtr = ((UniSchedulerApplication) CoursesActivity.this.getApplication()).getCurrentQuarter();

        db.collection(KEY_COLLECTION_USERS).document(firebaseUser.getUid()).collection(ourUser.getUniversity()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    currentSections.clear();
                    currentIds.clear();
                    pastSections.clear();
                    pastIds.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Section section = document.toObject(Section.class);
                        if (section.getQuarter().equals(currentQtr)) {
                            currentSections.add(section);
                            currentIds.add(document.getId());
                        } else {
                            pastSections.add(section);
                            pastIds.add(document.getId());
                        }
                    }
                    currentAdapter.notifyDataSetChanged();
                    pastAdapter.notifyDataSetChanged();

                    app.setCurrentSections(currentSections);
                    app.setCurrentIds(currentIds);
                    app.setPastSections(pastSections);
                    app.setPastIds(pastIds);
                } else {
                    Log.e(TAG, "Could not get user's courses", task.getException());
                }
            }
        });
    }
}