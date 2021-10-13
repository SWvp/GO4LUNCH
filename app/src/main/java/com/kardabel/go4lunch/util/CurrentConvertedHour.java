package com.kardabel.go4lunch.util;

import java.time.LocalTime;

public class CurrentConvertedHour {
    int convertedHour;

    public int getCurrentConvertedHour() {


        int hour = LocalTime.now().getHour();
        int minutes = LocalTime.now().getMinute();


        convertedHour = hour * 100 + minutes;


        return convertedHour;
    }

}
