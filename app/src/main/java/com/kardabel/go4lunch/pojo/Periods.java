package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Periods {

    @SerializedName("close")
    @Expose
    private final Close close;

    @SerializedName("open")
    @Expose
    private final Open open;

    public Periods(Close close, Open open) {
        this.close = close;
        this.open = open;

    }

    public Close getClose() { return close; }

    public Open getOpen() { return open; }

}
