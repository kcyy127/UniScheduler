package com.example.unischeduler.Models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ScheduleEvent implements Parcelable {
    private List<Long> dates;
    private long time, duration;
    private String location, name, course_affil;

    public ScheduleEvent() {
        this.dates = new ArrayList<>();
    }

    public ScheduleEvent(String name, String location, String course_affil) {
        this.location = location;
        this.name = name;
        this.course_affil = course_affil;
        this.dates = new ArrayList<>();
    }

    public ScheduleEvent(String name, List<Long> dates, long time, long duration, String location, String course_affil) {
        this.dates = dates;
        this.time = time;
        this.duration = duration;
        this.location = location;
        this.name = name;
        this.course_affil = course_affil;
    }

    protected ScheduleEvent(Parcel in) {
        dates = new ArrayList<>();

        time = in.readLong();
        duration = in.readLong();
        location = in.readString();
        name = in.readString();
        in.readList(this.dates, List.class.getClassLoader());
        course_affil = in.readString();
    }

    public static final Creator<ScheduleEvent> CREATOR = new Creator<ScheduleEvent>() {
        @Override
        public ScheduleEvent createFromParcel(Parcel in) {
            return new ScheduleEvent(in);
        }

        @Override
        public ScheduleEvent[] newArray(int size) {
            return new ScheduleEvent[size];
        }


    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addDatesByEnd(long start, long end, long repeat_interval) {
        if (repeat_interval == 0 || start == end || end == 0) {
            this.dates = new ArrayList<>(Arrays.asList(start));
        } else {
            this.dates = LongStream.range(start, end).filter(i -> (i - start) % repeat_interval == 0).boxed().collect(Collectors.toList());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addDatesByEnd(LocalDate startDate, LocalDate endDate, long repeat_interval) {
        this.addDatesByEnd(toUnix(startDate), toUnix(endDate), repeat_interval);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addDatesByOccurrences(long start, long repeat_interval, int occurrences) {
        if (repeat_interval == 0 || occurrences == 0 || occurrences == -1) {
            this.dates = new ArrayList<>(Arrays.asList(start));
        } else {
            this.dates = LongStream.iterate(start, n -> n + repeat_interval).limit(occurrences).boxed().collect(Collectors.toList());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addDatesByOccurrences(LocalDate startDate, long repeat_interval, int occurrences) {
        addDatesByOccurrences(toUnix(startDate), repeat_interval, occurrences);
    }

    public void addTime(LocalTime localTime) {
        this.time = (long) (localTime.getHourOfDay() * 60 + localTime.getMinuteOfHour()) * 60;
    }

    public void addDuration(int minutes) {
        this.duration = (long) minutes * 60;
    }


    public static long toUnix(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.toLocalDateTime(new LocalTime(0, 0));
        Instant instant = Instant.parse(localDateTime.toString());
        return instant.getMillis() / 1000;
    }

    public static LocalDateTime toLocalDateTime(long unix) {
        Instant instant = Instant.ofEpochSecond(unix);
        return instant.toDateTime().toLocalDateTime();
    }

    public static LocalDate toLocalDate(long unix) {
        Instant instant = Instant.ofEpochSecond(unix);
        return instant.toDateTime().toLocalDate();
    }

    public static LocalDate nextOrSame(LocalDate start, int dow) {
        int dowStart = start.getDayOfWeek();
        int difference = dow - dowStart;
        if (difference < 0) {
            difference += 7;
        }
        return start.plusDays(difference);
    }

    public List<Long> getDates() {
        return dates;
    }

    public void setDates(List<Long> dates) {
        this.dates = dates;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse_affil() {
        return course_affil;
    }

    public void setCourse_affil(String course_affil) {
        this.course_affil = course_affil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleEvent)) return false;
        ScheduleEvent event = (ScheduleEvent) o;
        return getTime() == event.getTime() && getDuration() == event.getDuration() && Objects.equals(getDates(), event.getDates()) && Objects.equals(getLocation(), event.getLocation()) && Objects.equals(getName(), event.getName()) && Objects.equals(getCourse_affil(), event.getCourse_affil());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDates(), getTime(), getDuration(), getLocation(), getName(), getCourse_affil());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.time);
        dest.writeLong(this.duration);
        dest.writeString(this.location);
        dest.writeString(this.name);
        dest.writeList(this.dates);
        dest.writeString(this.course_affil);
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("dates", dates);
        map.put("duration", duration);
        map.put("location", location);
        map.put("name", name);
        map.put("time", time);
        return map;
    }


}
