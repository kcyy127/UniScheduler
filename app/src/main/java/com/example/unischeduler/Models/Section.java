package com.example.unischeduler.Models;


import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class Section implements Parcelable {
    private Course course;
    private Quarter quarter;
    private Instructor instructor;
    private ArrayList<ScheduleEvent> schedules;

    public Section() {
        this.schedules = new ArrayList<>();
    }

    // NEW CONSTRUCTOR
    public Section(Course course, Quarter quarter, Instructor instructor) {
        this.course = course;
        this.quarter = quarter;
        this.instructor = instructor;
        this.schedules = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Section(Parcel in) {
        course = in.readParcelable(Course.class.getClassLoader());

        quarter = in.readParcelable(Quarter.class.getClassLoader());

        instructor = in.readParcelable(Instructor.class.getClassLoader());

        schedules = new ArrayList<>();
        in.readParcelableList(schedules, ScheduleEvent.class.getClassLoader());
    }

    public static final Creator<Section> CREATOR = new Creator<Section>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addSchedule(String meetingType, String building, String room, String dowString, LocalTime startTime, int durationMinutes) {

        boolean[] dowBool = boolArray(dowString);
        for (int i = 1; i<=7; i++) {
            if (dowBool[i-1]) {
                ScheduleEvent event = new ScheduleEvent(course.getCode_dep() + " " + course.getCode_num() + " " + meetingType,
                        building + " " + room, "");
                event.addTime(startTime);
                event.addDuration(durationMinutes);
                LocalDate startDate = nextOrSame(toDate(quarter.getStart()), i);
                event.addDatesByEnd(startDate, toDate(quarter.getEnd()), 604800L);
                this.schedules.add(event);
            }
        }

    }

    public static boolean[] boolArray(String s) {
        boolean[] arr = new boolean[7];
        Arrays.fill(arr, Boolean.FALSE);

        for (char digit : s.toCharArray()) {
            int i = Character.getNumericValue(digit);
            arr[i - 1] = true;
        }

        return arr;
    }

    public static LocalDate nextOrSame(LocalDate start, int dow) {
        int dowStart = start.getDayOfWeek();
        int difference = dow - dowStart;
        if (difference < 0) {
            difference += 7;
        }
        return start.plusDays(difference);
    }

    public static long toUnix(LocalDateTime localDateTime) {
        Instant instant = Instant.parse(localDateTime.toString());
        return instant.getMillis() / 1000;
    }

    public static LocalDate toDate(long unix) {
        Instant instant = Instant.ofEpochSecond(unix);
        return instant.toDateTime().toLocalDate();
    }

    public ArrayList<ScheduleEvent> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<ScheduleEvent> schedules) {
        this.schedules = schedules;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Quarter getQuarter() {
        return quarter;
    }

    public void setQuarter(Quarter quarter) {
        this.quarter = quarter;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Section> findByCourse(ArrayList<Section> sections, Course courseToMatch) {
        return sections.stream().filter(section -> courseToMatch.equals(section.getCourse())).collect(Collectors.toCollection(ArrayList::new));
    }

    public HashMap<String, HashMap<String, ArrayList<Integer>>> scheduleToHashMap() {
        HashMap<String, HashMap<String, ArrayList<Integer>>> result = new HashMap<>();
        for (ScheduleEvent event : this.schedules) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("k:mm");

            String type = event.getName().substring(event.getName().lastIndexOf(" ") + 1);
            LocalDateTime startDateTime = Instant.ofEpochSecond(event.getDates().get(0) + event.getTime()).toDateTime().toLocalDateTime();
            int dow = startDateTime.getDayOfWeek();
            String hours = formatter.print(startDateTime) + " - " + formatter.print(startDateTime.plusMinutes((int) (event.getDuration() / 60)));

            if (result.containsKey(type)) {
                HashMap<String, ArrayList<Integer>> typeValue = result.get(type);
                if (typeValue.containsKey(hours)) {
                    typeValue.get(hours).add(dow);
                } else {
                    // not sure about this
                    typeValue.put(hours, new ArrayList<>(Arrays.asList(dow)));
                }
            } else {
                HashMap<String, ArrayList<Integer>> newType = new HashMap<>();
                newType.put(hours, new ArrayList<>(Arrays.asList(dow)));
                result.put(type, newType);
            }
        }
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(getCourse(), section.getCourse()) && Objects.equals(getQuarter(), section.getQuarter()) && Objects.equals(getInstructor(), section.getInstructor()) && Objects.equals(getSchedules(), section.getSchedules());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourse(), getQuarter(), getInstructor(), getSchedules());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.course, 0);
        dest.writeParcelable(this.quarter, 0);
        dest.writeParcelable(this.instructor, 0);
        dest.writeParcelableList(this.schedules, 0);
    }
}
