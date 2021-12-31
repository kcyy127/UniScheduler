package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.example.unischeduler.Models.User;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";

    private final long MAX_IMAGE_SIZE = 1024 * 1024;

    private ActivityAccountBinding binding;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference profImageReference;

    private UniSchedulerApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account);

        initialize();

        getUserInfo();

        setListeners();
    }

    private void initialize() {
        app = (UniSchedulerApplication) AccountActivity.this.getApplication();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        String path = String.format("profile_images/%s.jpg", app.getCurrentUser().getUid());
        profImageReference = storage.getReference().child(path);

        if (app.getProfileImage()==null) {
            loadProfileImage();
        } else {
            binding.ivProfile.setImageBitmap(app.getProfileImage());
        }
    }

    private void getUserInfo() {
        User ourUser = app.getOurUser();
        binding.tvName.setText(ourUser.getFirst_name() + " " + ourUser.getLast_name());
        binding.tvUni.setText(ourUser.getUniversity());
    }

    private void setListeners() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, HomeActivity.class));
            }
        });

        binding.lytProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, UpdateProfileActivity.class));
            }
        });

        binding.lytLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            }
        });

        binding.lytCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, CoursesActivity.class));
            }
        });
    }

    private void loadProfileImage() {
        profImageReference.getBytes(MAX_IMAGE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                app.setProfileImage(bitmap);
                binding.ivProfile.setImageBitmap(bitmap);
            }
        });
    }
}