package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.unischeduler.Adapters.CourseFragmentAdapter;
import com.example.unischeduler.Fragments.GeneralFragment;
import com.example.unischeduler.Fragments.HWFragment;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityIndividualCourseBinding;
import com.example.unischeduler.databinding.DialogDeleteConfirmationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.unischeduler.Constants.*;

public class IndividualCourseActivity extends AppCompatActivity {

    private static final String TAG = "IndividualCourseActivity";

    private ActivityIndividualCourseBinding binding;

    private Section section;
    private String sectionId;

    private String[] titles = new String[]{"GENERAL", "HW", "EXAMS"};
    private ArrayList<Fragment> fragments;
    private CourseFragmentAdapter fragmentAdapter;

    private FirebaseFirestore db;

    private AlertDialog deleteDialog;

    private ArrayList<com.example.unischeduler.Models.Task> tasks;
    private ArrayList<String> taskIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_individual_course);
        super.onCreate(savedInstanceState);

        initialize();

        setViewPager();

        setDeleteButton();

        getTasksFromDB();
    }

    private void initialize() {
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        section = intent.getParcelableExtra("section");
        sectionId = intent.getStringExtra("id");
        Log.i(TAG, section.getCourse().getName());

        binding.tvHeading.setText(section.getCourse().getCode_dep() + " " + section.getCourse().getCode_num());
        binding.tvSubheading.setText(section.getCourse().getName());

        tasks = new ArrayList<>();
        taskIds = new ArrayList<>();
    }

    private void setViewPager() {
        fragments = new ArrayList<>();
        fragments.add(GeneralFragment.newInstance(section));
        fragments.add(HWFragment.newInstance(tasks, taskIds));
        fragments.add(new Fragment());

        fragmentAdapter = new CourseFragmentAdapter(IndividualCourseActivity.this, fragments);
        binding.viewPager.setAdapter(fragmentAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(titles[position])).attach();
    }

    private void setDeleteButton() {
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // alert dialog y/N?
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        if (deleteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(IndividualCourseActivity.this);
            DialogDeleteConfirmationBinding binding = DialogDeleteConfirmationBinding.inflate(getLayoutInflater(), null, false);
            builder.setView(binding.getRoot());

            deleteDialog = builder.create();

            if (deleteDialog.getWindow() != null) {
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            binding.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });

            binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteSection();
                }
            });

            deleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    deleteDialog = null;
                }
            });
        }
        deleteDialog.show();

    }

    private void deleteSection() {
        String uniName = ((UniSchedulerApplication) IndividualCourseActivity.this.getApplication()).getOurUser().getUniversity();
        String uid = ((UniSchedulerApplication) IndividualCourseActivity.this.getApplication()).getCurrentUser().getUid();

        db.collection(KEY_COLLECTION_USERS).document(uid).collection(uniName).document(sectionId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, section.getCourse().getCode_dep() + " " + section.getCourse().getCode_num() + " has been deleted");
                    Intent backToCourses = new Intent(IndividualCourseActivity.this, CoursesActivity.class);
                    backToCourses.putExtra("refresh", true);
                    startActivity(backToCourses);
                } else {
                    Log.e(TAG, "Could not delete course", task.getException());
                }
            }
        });
    }

    private void getTasksFromDB() {
        String uid = ((UniSchedulerApplication) IndividualCourseActivity.this.getApplication()).getCurrentUser().getUid();

        db.collection(KEY_COLLECTION_USERS).document(uid).collection(KEY_COLLECTION_TASKS)
                .whereEqualTo("course_affil", sectionId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    tasks.clear();
                    taskIds.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        tasks.add(document.toObject(com.example.unischeduler.Models.Task.class));
                        taskIds.add(document.getId());
                    }

                    HWFragment newFragment = HWFragment.newInstance(tasks, taskIds);
                    fragments.set(1, newFragment);
                    fragmentAdapter.notifyItemChanged(1);
                } else {
                    Log.e(TAG, "Could not get tasks affiliated with course", task.getException());
                }
            }
        });
    }
}