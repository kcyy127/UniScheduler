package com.example.unischeduler;

import org.joda.time.LocalDate;

public interface MonthToHomeInterface {
    void changeSelectedDate(LocalDate selectedDate);
    void startScheduleActivity();
}
