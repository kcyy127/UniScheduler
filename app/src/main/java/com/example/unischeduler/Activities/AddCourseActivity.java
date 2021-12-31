package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.unischeduler.Adapters.AddCourseAdapter;
import com.example.unischeduler.Adapters.AddSectionAdapter;
import com.example.unischeduler.AddCourseInterface;
import static com.example.unischeduler.Constants.*;
import com.example.unischeduler.Models.Course;
import com.example.unischeduler.Models.Quarter;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.Models.User;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityAddCourseBinding;
import com.example.unischeduler.databinding.DialogSectionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AddCourseActivity extends AppCompatActivity implements AddCourseInterface {

    private static final String TAG = "AddCourseActivity";

    private ActivityAddCourseBinding binding;

    private FirebaseFirestore db;
    private String userId;
    private User ourUser;

    private HashMap<Section, String> sectionIds;
    private ArrayList<Course> courses;
    private ArrayList<Section> sections;

    private AddCourseAdapter courseAdapter;

    private AddSectionAdapter sectionAdapter;
    private AlertDialog sectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_course);

        initialize();

        populateRV();

        setListeners();
    }

    private void initialize() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ourUser = ((UniSchedulerApplication) AddCourseActivity.this.getApplication()).getOurUser();
        sectionIds = new HashMap<>();
        courses = new ArrayList<>();
        sections = new ArrayList<>();
        courseAdapter = new AddCourseAdapter(AddCourseActivity.this, courses, this);
        binding.rvCourses.setAdapter(courseAdapter);
        binding.rvCourses.setLayoutManager(new LinearLayoutManager(AddCourseActivity.this));
    }

    private void populateRV() {
        String uniDocId = ((UniSchedulerApplication)this.getApplication()).getUniDocId();
        LocalDate today = LocalDate.now();
        long unix = Instant.parse(today.toString()).getMillis() / 1000;
        // get id of ucsd doc

//        db.collection(KEY_COLLECTION_UNIVERSITIES).document(uniDocId).collection(KEY_COLLECTION_QUARTERS)
//                .whereGreaterThan("end", unix).orderBy("end", Query.Direction.ASCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    Quarter currentQuarter = null;
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        currentQuarter = document.toObject(Quarter.class);
//                        Log.i(TAG, "quarter doc Id: " + document.getId());
//                    }
//
//
//
//                } else {
//                    Log.e(TAG, "Could not get current quarter", task.getException());
//                }
//            }
//        });
        Quarter currentQuarter = ((UniSchedulerApplication) AddCourseActivity.this.getApplication()).getCurrentQuarter();
        db.collection(KEY_COLLECTION_UNIVERSITIES).document(uniDocId).collection(KEY_COLLECTION_SECTIONS)
                .whereEqualTo("quarter", currentQuarter).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashSet<Course> courseSet = new HashSet<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Section section = document.toObject(Section.class);
                        sectionIds.put(section, document.getId());
                        courseSet.add(section.getCourse());
                    }
                    courses.addAll(courseSet);
                    sections.addAll(sectionIds.keySet());
                    Collections.sort(courses);
                    courseAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Could not get sections", task.getException());
                }
            }
        });
    }

    private void setListeners() {
        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to create course activity
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // get sections -> then find their ids
                HashMap<Integer, Integer> adapterInfo = courseAdapter.getSelected();
                ArrayList<String> selectedSectionIds = new ArrayList<>();
                Map<String, Object> themSections = new HashMap<>();

                for (Map.Entry entry : adapterInfo.entrySet()) {
                    ArrayList<Section> tempSections = Section.findByCourse(sections, courses.get((Integer) entry.getKey())); // sections of course
                    Section selectedSection = tempSections.get((Integer) entry.getValue()); // section of sections

                    selectedSectionIds.add(sectionIds.get(selectedSection)); // append id of section
                    Log.i(TAG, "Selected course ID: " + sectionIds.get(selectedSection));

                    // trying something
                    themSections.put(sectionIds.get(selectedSection), selectedSection);

                }

                WriteBatch batch = db.batch();
                for (Map.Entry entry : themSections.entrySet()) {
                    batch.set(db.collection(KEY_COLLECTION_USERS).document(userId).collection(ourUser.getUniversity()).document((String) entry.getKey()), entry.getValue());
                }
                batch.commit();
                // back to account activity
                startActivity(new Intent(AddCourseActivity.this, CoursesActivity.class));
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                Log.i(TAG, keyword);
                
            }
        });


    }

    private void showSectionDialog(int coursePosition, ArrayList<Section> displaySections) {

        if (sectionDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddCourseActivity.this);
            DialogSectionBinding sectionBinding = DialogSectionBinding.inflate(getLayoutInflater(), null, false);
            builder.setView(sectionBinding.getRoot());
            sectionDialog = builder.create();

            if (sectionDialog.getWindow()!=null) {
                sectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            sectionAdapter = new AddSectionAdapter(AddCourseActivity.this, displaySections);
            sectionBinding.rvSections.setAdapter(sectionAdapter);
            sectionBinding.rvSections.setLayoutManager(new LinearLayoutManager(AddCourseActivity.this));

            sectionBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get selected position
                    int selectedPosition = sectionAdapter.getSelectedPosition();
                    if (selectedPosition == RecyclerView.NO_POSITION) {
                        Toast.makeText(AddCourseActivity.this, "Must select section", Toast.LENGTH_SHORT).show();
                    } else {
                        sectionDialog.dismiss();
                        // change appearance of selected course w/ section
                        courseAdapter.addSelected(coursePosition, selectedPosition);
                    }
                }
            });

            sectionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    sectionDialog = null;
                    sectionAdapter = null;
                }
            });
        }

        sectionDialog.show();

    }

    // for adapter
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCourseClicked(int position) {
        Course selectedCourse = courses.get(position);
        Log.i(TAG, selectedCourse.getName());
        ArrayList<Section> courseSections = Section.findByCourse(sections, selectedCourse);

//        HashMap<Section, String> courseSections2 = new HashMap<>();
//        for (Map.Entry entry : sectionIds.entrySet()) {
//            if (((Section) entry.getKey()).getCourse() == selectedCourse) {
//                // add entry
//            }
//        }
//
//        for (Section cs : courseSections) {
//            Log.i(TAG, "Section: " + cs.getInstructor().getLast_name());
//        }

        showSectionDialog(position, courseSections);
    }


}