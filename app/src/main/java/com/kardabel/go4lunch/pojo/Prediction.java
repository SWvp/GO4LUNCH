package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prediction {

    @SerializedName("description")
    @Expose
    private final String description;
    @SerializedName("structured_formatting")
    @Expose
    private final PlaceAutocompleteStructuredFormat structuredFormatting;
    @SerializedName("place_id")
    @Expose
    private final String placeId;



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
