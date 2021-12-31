package com.example.unischeduler.Models;

import android.net.Uri;

public class User {
    private String email, password, last_name, first_name, university;

    public User() {
    }

    public User(String email, String password, String last_name, String first_name, String university) {
        this.email = email;
        this.password = password;
        this.last_name = last_name;
        this.first_name = first_name;
        this.university = university;
    }

    public User(String email, String password, String last_name, String first_name, String university, Uri profile_image) {
        this.email = email;
        this.password = password;
        this.last_name = last_name;
        this.first_name = first_name;
        this.university = university;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

}
