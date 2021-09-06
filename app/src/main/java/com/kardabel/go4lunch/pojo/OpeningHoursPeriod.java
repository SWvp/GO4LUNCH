package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by stéphane Warin OCR on 02/09/2021.
 */
public class OpeningHoursPeriod {

    @SerializedName("day")
    private final Integer day;

    @SerializedName("time")
    private final String time;

    public OpeningHoursPeriod(Integer day, String time) {
        this.day = day;
        this.time = time;

    }

    public Integer getDay() { return day; }

    public String getTime() { return time; }
}
