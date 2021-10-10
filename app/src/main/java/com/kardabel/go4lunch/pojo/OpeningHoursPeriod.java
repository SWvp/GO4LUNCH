package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * à virer si inutile !!!
 */
public class OpeningHoursPeriod {

    @SerializedName("day")
    @Expose
    private final Integer day;

    @SerializedName("time")
    @Expose
    private final String time;

    public OpeningHoursPeriod(Integer day, String time) {
        this.day = day;
        this.time = time;

    }

    public Integer getDay() { return day; }

    public String getTime() { return time; }
}
