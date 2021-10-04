package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    private final RestaurantLatLngLiteral mRestaurantLatLngLiteral;


    public Geometry(RestaurantLatLngLiteral restaurantLatLngLiteral) {
        mRestaurantLatLngLiteral = restaurantLatLngLiteral;

    }

    public RestaurantLatLngLiteral getRestaurantLatLngLiteral() { return mRestaurantLatLngLiteral; }
}

