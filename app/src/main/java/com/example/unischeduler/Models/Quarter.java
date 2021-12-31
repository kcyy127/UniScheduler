package com.example.unischeduler.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.Objects;

public class Quarter implements Parcelable {
    private String season;
    private int year;
    private long start, end;

    public Quarter() {
    }

    public Quarter(int year, String season, LocalDate startDate, LocalDate endDate) {
        this.year = year;
        this.season = season;
        this.start = toUnix(startDate);
        this.end = toUnix(endDate.plusDays(1));
    }

    protected Quarter(Parcel in) {
        season = in.readString();
        year = in.readInt();
        start = in.readLong();
        end = in.readLong();
    }

    public static final Creator<Quarter> CREATOR = new Creator<Quarter>() {
        @Override
        public Quarter createFromParcel(Parcel in) {
            return new Quarter(in);
        }

        @Override
        public Quarter[] newArray(int size) {
            return new Quarter[size];
        }
    };

    public static long toUnix(LocalDate localDate) {
        Instant instant = Instant.parse(localDate.toString());
        return instant.getMillis() / 1000;
    }

    public static LocalDate toDate(long unix) {
        Instant instant = Instant.ofEpochSecond(unix);
        return instant.toDateTime().toLocalDate();
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quarter)) return false;
        Quarter quarter = (Quarter) o;
        return getYear() == quarter.getYear() && getStart() == quarter.getStart() && getEnd() == quarter.getEnd() && Objects.equals(getSeason(), quarter.getSeason());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSeason(), getYear(), getStart(), getEnd());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.season);
        dest.writeInt(this.year);
        dest.writeLong(this.start);
        dest.writeLong(this.end);
    }
}
