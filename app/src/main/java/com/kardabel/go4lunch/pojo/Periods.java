package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

public class Periods {

    @SerializedName("close")
    private Close close;

    @SerializedName("open")
    private Open open;

    public Periods(Close close, Open open) {
        this.close = close;
        this.open = open;

    }

    public Close getClose() { return close; }

    public Open getOpen() { return open; }

}
