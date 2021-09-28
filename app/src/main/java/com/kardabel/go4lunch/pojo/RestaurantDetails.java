package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

public class RestaurantDetails {

    @SerializedName("place_id")
    private final String placeId;

    @SerializedName("opening_hours")
    private final OpeningHours openingHours;

    public RestaurantDetails(String placeId, OpeningHours openingHours) {
        this.placeId = placeId;
        this.openingHours = openingHours;
    }

    public String getPlaceId() { return placeId; }

    public OpeningHours getOpeningHours() { return openingHours; }

}
