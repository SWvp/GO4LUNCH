package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceDetailsResult {

    @SerializedName("place_id")
    private final String detailsPlaceId;

    @SerializedName("opening_hours")
    private final OpeningHours detailsOpeningHours;

    public PlaceDetailsResult(String detailsPlaceId, OpeningHours detailsOpeningHours) {
        this.detailsPlaceId = detailsPlaceId;
        this.detailsOpeningHours = detailsOpeningHours;
    }

    public String getDetailsPlaceId() { return detailsPlaceId; }

    public OpeningHours getDetailsOpeningHours() { return detailsOpeningHours; }

}
