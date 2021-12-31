package com.example.unischeduler;

import static com.example.unischeduler.Constants.KEY_COLLECTION_USERS;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unischeduler.Activities.CoursesActivity;
import com.example.unischeduler.Models.Quarter;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.LocalDate;

import java.util.ArrayList;


public class UniSchedulerApplication extends Application {
    private LocalDate selectedDate;

    private FirebaseUser currentUser;
    private User ourUser;
    private String uniDocId;

    private Quarter currentQuarter;
    private static UniSchedulerApplication singleton;

    private ArrayList<Section> currentSections, pastSections;
    private ArrayList<String> currentIds, pastIds;

    private Bitmap profileImage;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        selectedDate = LocalDate.now();

        currentSections = new ArrayList<>();
        currentIds = new ArrayList<>();

        pastSections = new ArrayList<>();
        pastIds = new ArrayList<>();
    }



    public static UniSchedulerApplication getInstance() {
        return singleton;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public User getOurUser() {
        return ourUser;
    }

    public void setOurUser(User ourUser) {
        this.ourUser = ourUser;
    }

    public String getUniDocId() {
        return uniDocId;
    }

    public void setUniDocId(String uniDocId) {
        this.uniDocId = uniDocId;
    }

    public Quarter getCurrentQuarter() {
        return currentQuarter;
    }

    public void setCurrentQuarter(Quarter currentQuarter) {
        this.currentQuarter = currentQuarter;
    }

    public ArrayList<Section> getCurrentSections() {
        return currentSections;
    }

    public void setCurrentSections(ArrayList<Section> currentSections) {
        this.currentSections = currentSections;
    }

    public ArrayList<String> getCurrentIds() {
        return currentIds;
    }

    public void setCurrentIds(ArrayList<String> currentIds) {
        this.currentIds = currentIds;
    }

    public ArrayList<Section> getPastSections() {
        return pastSections;
    }

    public void setPastSections(ArrayList<Section> pastSections) {
        this.pastSections = pastSections;
    }

    public ArrayList<String> getPastIds() {
        return pastIds;
    }

    public void setPastIds(ArrayList<String> pastIds) {
        this.pastIds = pastIds;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }
}
