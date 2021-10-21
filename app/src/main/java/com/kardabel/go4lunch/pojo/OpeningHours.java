package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private final Boolean openNow;

    @SerializedName("periods")
    @Expose
    private final List<Periods> periods;


    public OpeningHours(Boolean openNow, List<Periods> periods) {
        this.openNow = openNow;
        this.periods = periods;

    }

    public Boolean getOpenNow() { return openNow; }

    public List<Periods> getPeriods() { return periods; }



}
