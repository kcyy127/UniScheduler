package com.example.unischeduler.Models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Comparator;
import java.util.Objects;

public class Course implements Comparable<Course>, Parcelable {
    private String name, code_dep, code_num;

    public Course() {
    }

    public Course(String name, String code_dep, String code_num) {
        this.name = name;
        this.code_dep = code_dep;
        this.code_num = code_num;
    }

    protected Course(Parcel in) {
        name = in.readString();
        code_dep = in.readString();
        code_num = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode_dep() {
        return code_dep;
    }

    public void setCode_dep(String code_dep) {
        this.code_dep = code_dep;
    }

    public String getCode_num() {
        return code_num;
    }

    public void setCode_num(String code_num) {
        this.code_num = code_num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(getName(), course.getName()) && Objects.equals(getCode_dep(), course.getCode_dep()) && Objects.equals(getCode_num(), course.getCode_num());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCode_dep(), getCode_num());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int compareTo(Course o) {
        return Comparator.comparing(Course::getCode_dep).thenComparing(Course::getCode_num).compare(this, o);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.code_dep);
        dest.writeString(this.code_num);
    }
}
