package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpeningHours {

    @SerializedName("open_now")
    private final Boolean openNow;

    @SerializedName("periods")
    private final List<Periods> periods;

    @SerializedName("weekday_text")
    private final List<String> weekdayText;

    public OpeningHours(Boolean openNow, List<Periods> periods, List<String> weekdayText) {
        this.openNow = openNow;
        this.periods = periods;
        this.weekdayText = weekdayText;
    }

    public Boolean getOpenNow() { return openNow; }

    public List<Periods> getPeriods() { return periods; }

    public List<String> getWeekdayText() { return weekdayText; }

}
