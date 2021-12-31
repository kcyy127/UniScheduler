package com.example.unischeduler.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Instructor implements Parcelable {

    private String last_name, first_name, email;

    public Instructor() {
    }

    public Instructor(String last_name, String first_name, String email) {
        this.last_name = last_name;
        this.first_name = first_name;
        this.email = email;
    }

    public Instructor(String last_name, String first_name) {
        this.last_name = last_name;
        this.first_name = first_name;
        this.email = generateEmail();
    }

    protected Instructor(Parcel in) {
        last_name = in.readString();
        first_name = in.readString();
        email = in.readString();
    }

    public static final Creator<Instructor> CREATOR = new Creator<Instructor>() {
        @Override
        public Instructor createFromParcel(Parcel in) {
            return new Instructor(in);
        }

        @Override
        public Instructor[] newArray(int size) {
            return new Instructor[size];
        }
    };

    public String generateEmail() {
        String em = last_name.replace(" ", "_").toLowerCase() +
                 first_name.replace(" ", "_").toLowerCase().substring(0, 1) + "@gmail.com";
        return em;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instructor)) return false;
        Instructor that = (Instructor) o;
        return Objects.equals(getLast_name(), that.getLast_name()) && Objects.equals(getFirst_name(), that.getFirst_name()) && Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLast_name(), getFirst_name(), getEmail());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(last_name);
        dest.writeString(first_name);
        dest.writeString(email);
    }
}
