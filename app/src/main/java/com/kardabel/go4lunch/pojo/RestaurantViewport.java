package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by stéphane Warin OCR on 02/09/2021.
 */
public class RestaurantViewport {

    @SerializedName("northeast")
    private final RestaurantLatLngLiteral mRestaurantLatLngLiteralNortheast;

    @SerializedName("southwest")
    private final RestaurantLatLngLiteral mRestaurantLatLngLiteralSouthwest;

    public RestaurantViewport(RestaurantLatLngLiteral restaurantLatLngLiteralNortheast, RestaurantLatLngLiteral restaurantLatLngLiteralSouthwest) {
        this.mRestaurantLatLngLiteralNortheast = restaurantLatLngLiteralNortheast;
        this.mRestaurantLatLngLiteralSouthwest = restaurantLatLngLiteralSouthwest;
    }

    public RestaurantLatLngLiteral getRestaurantLatngLiteralNortheast() { return mRestaurantLatLngLiteralNortheast; }

    public RestaurantLatLngLiteral getRestaurantLatngLiteralSouthwest() { return mRestaurantLatLngLiteralSouthwest; }
}
