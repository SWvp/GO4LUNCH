package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by stéphane Warin OCR on 02/09/2021.
 */
public class Geometry {

    @SerializedName("location")
    private final RestaurantLatLngLiteral mRestaurantLatLngLiteral;

    @SerializedName("viewport")
    private final RestaurantViewport restaurantViewport;

    public Geometry(RestaurantLatLngLiteral restaurantLatLngLiteral, RestaurantViewport restaurantViewport) {
        mRestaurantLatLngLiteral = restaurantLatLngLiteral;
        this.restaurantViewport = restaurantViewport;
    }

    public RestaurantLatLngLiteral getRestaurantLatLngLiteral() { return mRestaurantLatLngLiteral; }

    public RestaurantViewport getRestaurantViewport() { return restaurantViewport; }
}
