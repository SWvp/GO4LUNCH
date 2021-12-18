package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceAutocompleteStructuredFormat {

    @SerializedName("main_text")
    @Expose
    private final String name;


    public PlaceAutocompleteStructuredFormat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
