package com.example.unischeduler.Models;

public class University {
    private String name, calendar_type;

    public University() {
    }

    public University(String name) {
        this.name = name;
        this.calendar_type = "quarter";
    }

    public University(String name, String calendar_type) {
        this.name = name;
        this.calendar_type = calendar_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalendar_type() {
        return calendar_type;
    }

    public void setCalendar_type(String calendar_type) {
        this.calendar_type = calendar_type;
    }
}
