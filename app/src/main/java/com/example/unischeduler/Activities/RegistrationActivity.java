package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.unischeduler.Fragments.InputField1Fragment;
import com.example.unischeduler.Fragments.InputField2Fragment;
import com.example.unischeduler.Models.User;
import com.example.unischeduler.R;
import com.example.unischeduler.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    private ActivityRegistrationBinding binding;

    private RegistrationFragmentStateAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<String> unis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);

        setFirebase();

        setViewPager();

        setListeners();
    }

    private void setFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        unis = new ArrayList<>();

        DocumentReference docRef = db.collection("general").document("university_list");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    unis.addAll((List<String>) doc.get("universities"));
                } else {
                    Log.e(TAG, "Could not get university_list", task.getException());
                }
            }
        });
    }

    private void setListeners() {

    }

    public class RegistrationFragmentStateAdapter extends FragmentStateAdapter {

        InputField1Fragment page1;
        InputField2Fragment page2;

        public RegistrationFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
//                    page1 = new RegistrationInputFragment(RegistrationActivity.this, RegistrationInputFragment.INPUT_TYPE_1);
                    page1 = new InputField1Fragment(RegistrationActivity.this);
                    return page1;
                case 1:
//                    page2 = new RegistrationInputFragment(RegistrationActivity.this, RegistrationInputFragment.INPUT_TYPE_2);
                    page2 = new InputField2Fragment(RegistrationActivity.this, unis);
                    return page2;
            }
            return new InputField1Fragment(RegistrationActivity.this);
        }

        @Override
        public int getItemCount() {
            return 2;
        }

    }

    private void setViewPager() {
        adapter = new RegistrationFragmentStateAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setUserInputEnabled(false);


        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.viewPager.getCurrentItem() == 0) {
                    binding.viewPager.setCurrentItem(1);
                    binding.btnNext.setImageResource(R.drawable.icon_checkmark);
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);

                    Log.i(TAG, "trying to register");
                    String email = adapter.page1.etFirst.getText().toString().trim();
                    String password = adapter.page1.etSecond.getText().toString().trim();
                    String[] name = adapter.page2.etFirst.getText().toString().trim().split(" "); //name[0] last name
                    String uni = adapter.page2.etSecond.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty() || name[0].isEmpty() || name[name.length-1].isEmpty() || uni.isEmpty()) {
                        binding.viewPager.setCurrentItem(0);
                        binding.btnNext.setImageResource(R.drawable.icon_next);

                        Toast.makeText(RegistrationActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }

                    User userToDB = new User(email, password, name[1], name[0], uni);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser authUser = mAuth.getCurrentUser();
                                        // add user to database
                                        db.collection("users").document(authUser.getUid()).set(userToDB);
                                        // to login activity
                                        toLoginActivity();
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                        binding.viewPager.setCurrentItem(0);
                                        binding.btnNext.setImageResource(R.drawable.icon_next);
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        return;
                                    }
                                }
                            });
                }
            }
        });
    }

    private void toLoginActivity() {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }
}