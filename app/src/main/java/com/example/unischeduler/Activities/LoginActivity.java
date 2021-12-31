package com.example.unischeduler.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.unischeduler.Models.Quarter;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.Models.User;
import com.example.unischeduler.R;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.unischeduler.Constants.*;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;

    private String email, password;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        initialize();

        setFirebaseAuth();

        setListeners();
    }

    private void initialize() {
        binding.layoutInput1.tvChange.setText("New account");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
    }

    private void setFirebaseAuth() {
        if (currentUser != null) {
            login();
        }
    }

    private void toHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

    private void setListeners() {
        binding.layoutInput1.tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegisterActivity();
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                email = binding.layoutInput1.etFirst.getText().toString().trim();
                password = binding.layoutInput1.etSecond.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                // Login
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    login();
                                } else {
                                    Log.e(TAG, "Login not successful", task.getException());
                                    binding.progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        });

    }

    public void toRegisterActivity() {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    public void login() {
        this.currentUser = mAuth.getCurrentUser();
        ((UniSchedulerApplication) LoginActivity.this.getApplication()).setCurrentUser(currentUser);
        new UserInfoAsyncTask().execute();
        toHomeActivity();
    }

    public class UserInfoAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            getUserInfo();

            return null;
        }

        public void getUserInfo() {
            DocumentReference docRef = db.collection(KEY_COLLECTION_USERS).document(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User ourUser = document.toObject(User.class);
                            ((UniSchedulerApplication) LoginActivity.this.getApplication()).setOurUser(ourUser);
                            getUniPath(ourUser);
                        } else {
                            Log.i(TAG, "Document of current user doesn't exist");
                        }
                    } else {
                        Log.e(TAG, "Could not get document", task.getException());
                    }
                }
            });
        }

        private void getUniPath(User ourUser) {
            db.collection(KEY_COLLECTION_UNIVERSITIES).whereEqualTo("name", ourUser.getUniversity()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            Log.i(TAG, "Uni doc ID: " + docId);
                            ((UniSchedulerApplication) LoginActivity.this.getApplication()).setUniDocId(docId);
                            getCurrentQuarter(docId);
                        }
                    } else {
                        Log.e(TAG, "Could not get uni doc ID", task.getException());
                    }
                }
            });
        }

        private void getCurrentQuarter(String docId) {
            Long unix = Instant.parse(LocalDate.now().toString()).getMillis() / 1000;
            db.collection(KEY_COLLECTION_UNIVERSITIES).document(docId).collection(KEY_COLLECTION_QUARTERS).
                    whereGreaterThan("end", unix).orderBy("end", Query.Direction.ASCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snap : task.getResult()) {
                            Quarter quarter = snap.toObject(Quarter.class);
                            ((UniSchedulerApplication) LoginActivity.this.getApplication()).setCurrentQuarter(quarter);
                        }
                    } else {
                        Log.e(TAG, "Couldn't get current quarter", task.getException());
                    }
                }
            });
        }
    }
}