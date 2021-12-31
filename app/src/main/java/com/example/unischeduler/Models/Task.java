package com.example.unischeduler.Models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Task implements Parcelable {

    private static final int STATUS_UNSTARTED = 0;
    private static final int STATUS_IN_PROGRESS = 1;
    private static final int STATUS_COMPLETED = 2;

//    private List<Long> dates;
    private String name;
    private long date;
    private long time; // default is end of day
//    private List<Integer> status;
    private int status;

    public Task() {
        this.status = STATUS_UNSTARTED;
    }

    public Task(String name, LocalTime localTime) {
        this.name = name;
        this.time = (long) (localTime.getHourOfDay() * 60 + localTime.getMinuteOfHour()) * 60;
        this.status = 0;
    }

    public Task(String name, LocalDate localDate, LocalTime localTime) {
        this.name = name;
        this.date = toUnix(localDate);
        this.time = (long) (localTime.getHourOfDay() * 60 + localTime.getMinuteOfHour()) * 60;
        this.status = STATUS_UNSTARTED;
    }

    public Task(String name, long date, long time) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.status = STATUS_UNSTARTED;
    }

    protected Task(Parcel in) {
        name = in.readString();
        date = in.readLong();
        time = in.readLong();
        status = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Task> repeatsByDate(long start, long end, long repeat_interval) {
        // we have everything but date
        ArrayList<Task> result = new ArrayList<>();

        if (repeat_interval == 0 || start == end || end == 0) {
            this.date = start;
            result.add(this);
            return result;
        } else {
            List<Long> dateList = LongStream.range(start, end).filter(i -> (i - start) % repeat_interval == 0).boxed().collect(Collectors.toList());
            for (long date : dateList) {
                Task temp = new Task(this.name, date, this.time);
                result.add(temp);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Task> repeatsByDate(LocalDate startDate, LocalDate endDate, long repeat_interval) {
        return repeatsByDate(toUnix(startDate), toUnix(endDate), repeat_interval);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Task> repeatsByOccurrence(long start, int occurrences, long repeat_interval) {
        ArrayList<Task> result = new ArrayList<>();

        if (repeat_interval == 0 || occurrences == 0 || occurrences == -1) {
//            this.dates = new ArrayList<>(Arrays.asList(start));
            this.date = start;
            result.add(this);
            return result;
        } else {
            List<Long> dateList = LongStream.iterate(start, n -> n + repeat_interval).limit(occurrences).boxed().collect(Collectors.toList());
            for (long date : dateList) {
                Task temp = new Task(this.name, date, this.time);
                result.add(temp);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Task> repeatsByOccurrence(LocalDate start, int occurrences, long repeat_interval) {
        return repeatsByOccurrence(toUnix(start), occurrences, repeat_interval);
    }

    public static long toUnix(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.toLocalDateTime(new LocalTime(0, 0));
        Instant instant = Instant.parse(localDateTime.toString());
        return instant.getMillis() / 1000;
    }


    public static LocalDate nextOrSame(LocalDate start, int dow) {
        int dowStart = start.getDayOfWeek();
        int difference = dow - dowStart;
        if (difference < 0) {
            difference += 7;
        }
        return start.plusDays(difference);
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("time", time);
        map.put("name", name);
        map.put("status", status);
        return map;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(date);
        dest.writeLong(time);
        dest.writeInt(status);
    }
}
