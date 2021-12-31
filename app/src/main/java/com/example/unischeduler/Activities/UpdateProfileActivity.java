package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.unischeduler.Constants.*;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 43;

    private static final String TAG = "UpdateProfileActivity";

    private ActivityUpdateProfileBinding binding;

    private UniSchedulerApplication app;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference profImageReference;
    private DocumentReference userReference;
    private String uid;

    private ArrayList<String> unis;

    private String firstName, lastName, uni;
    private Uri selectedImage;
    private boolean imageChanged;

    private Bitmap tempBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);

        initialize();

        setSpinner();

        setListeners();
    }

    private void initialize() {
        app = (UniSchedulerApplication) UpdateProfileActivity.this.getApplication();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        uid = app.getCurrentUser().getUid();

        String path = String.format("profile_images/%s.jpg", uid);
        profImageReference = storage.getReference().child(path);
        userReference = db.collection(KEY_COLLECTION_USERS).document(uid);

        imageChanged = false;

        unis = new ArrayList<>();

        binding.etFirst.setText(app.getOurUser().getFirst_name());
        binding.etLast.setText(app.getOurUser().getLast_name());
        binding.etUniversity.setText(app.getOurUser().getUniversity());
        binding.ivProfile.setImageBitmap(app.getProfileImage());
    }

    private void setSpinner() {
        ArrayAdapter<String> uniAdapter = new ArrayAdapter<>(UpdateProfileActivity.this, R.layout.item_spinner, R.id.item, unis);
        binding.etUniversity.setAdapter(uniAdapter);
        binding.etUniversity.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.background_spinner_list));

        db.collection(KEY_COLLECTION_GENERAL).document(KEY_DOCUMENT_UNIVERSITY_LIST).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    unis.addAll((List<String>) document.get("universities"));
                    uniAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Could not get unis", task.getException());
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

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = binding.etFirst.getText().toString().trim();
                if (firstName.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "First name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                lastName = binding.etLast.getText().toString().trim();
                if (lastName.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                uni = binding.etUniversity.getText().toString().trim();
                if (uni.isEmpty() || !unis.contains(uni)) {
                    Toast.makeText(UpdateProfileActivity.this, "University field error", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> updates = new HashMap<>();
                updates.put("first_name", firstName);
                updates.put("last_name", lastName);
                updates.put("university", uni);

                binding.progressBar.setVisibility(View.VISIBLE);

                if (imageChanged) {
                    // we changed the image
                    profImageReference.putFile(selectedImage)
                            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return profImageReference.getDownloadUrl();
                        }
                    }).continueWithTask(new Continuation<Uri, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<Uri> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return userReference.update(updates);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                app.getOurUser().setFirst_name(firstName);
                                app.getOurUser().setLast_name(lastName);
                                app.getOurUser().setUniversity(uni);

                                app.setProfileImage(tempBitmap);

                                startActivity(new Intent(UpdateProfileActivity.this, AccountActivity.class));

                            } else {
                                Log.e(TAG, "Could not update", task.getException());
                            }
                        }
                    });

                } else {
                    userReference.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                app.getOurUser().setFirst_name(firstName);
                                app.getOurUser().setLast_name(lastName);
                                app.getOurUser().setUniversity(uni);

                                startActivity(new Intent(UpdateProfileActivity.this, AccountActivity.class));
                            } else {
                                Log.e(TAG, "Could not update", task.getException());
                            }
                        }
                    });

                }


            }
        });

        binding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            try {
                tempBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                binding.ivProfile.setImageBitmap(tempBitmap);
                imageChanged = true;

                Log.i(TAG, "We have the image");
            } catch (Exception e) {
                Log.e(TAG, "Could not get image", e);
            }
        }
    }
}