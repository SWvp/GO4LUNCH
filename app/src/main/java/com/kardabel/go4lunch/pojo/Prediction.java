package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Prediction {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("structured_formatting")
    @Expose
    private PlaceAutocompleteStructuredFormat structuredFormatting;
    @SerializedName("place_id")
    @Expose
    private String placeId;



    public Prediction(String description,
                      String placeId,
                      PlaceAutocompleteStructuredFormat structuredFormatting) {
        this.description = description;
        this.placeId = placeId;
        this.structuredFormatting =structuredFormatting;
    }

    public String getDescription() {
        return description;
    }

    public String getPlaceId() {
        return placeId;
    }

    public PlaceAutocompleteStructuredFormat getStructuredFormatting() { return structuredFormatting; }
}
