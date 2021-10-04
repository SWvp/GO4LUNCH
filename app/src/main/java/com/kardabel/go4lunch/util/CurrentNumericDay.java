package com.kardabel.go4lunch.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class CurrentNumericDay {

    // TRANSLATE CURRENT DAY IN NUMERIC (FROM SUNDAY == 0)
    public int getCurrentNumericDay(){
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();
        int currentNumericDay = -1;
        switch (currentDay){
            case SUNDAY:
                currentNumericDay = 0;
                break;
            case MONDAY:
                currentNumericDay = 1;
                break;
            case TUESDAY:
                currentNumericDay = 2;
                break;
            case WEDNESDAY:
                currentNumericDay = 3;
                break;
            case THURSDAY:
                currentNumericDay = 4;
                break;
            case FRIDAY:
                currentNumericDay = 5;
                break;
            case SATURDAY:
                currentNumericDay = 6;
                break;

        }
        return currentNumericDay;

    }
}
