package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by stéphane Warin OCR on 02/09/2021.
 */
public class RestaurantViewport {

    @SerializedName("northeast")
    @Expose
    private final RestaurantLatLngLiteral mRestaurantLatLngLiteralNortheast;

    @SerializedName("southwest")
    @Expose
    private final RestaurantLatLngLiteral mRestaurantLatLngLiteralSouthwest;

    public RestaurantViewport(RestaurantLatLngLiteral restaurantLatLngLiteralNortheast, RestaurantLatLngLiteral restaurantLatLngLiteralSouthwest) {
        this.mRestaurantLatLngLiteralNortheast = restaurantLatLngLiteralNortheast;
        this.mRestaurantLatLngLiteralSouthwest = restaurantLatLngLiteralSouthwest;
    }

    public RestaurantLatLngLiteral getRestaurantLatLngLiteralNortheast() { return mRestaurantLatLngLiteralNortheast; }

    public RestaurantLatLngLiteral getRestaurantLatLngLiteralSouthwest() { return mRestaurantLatLngLiteralSouthwest; }
}
