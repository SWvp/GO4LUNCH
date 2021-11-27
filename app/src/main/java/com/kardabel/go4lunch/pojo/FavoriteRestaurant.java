package com.kardabel.go4lunch.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FavoriteRestaurant {

    @SerializedName("place_id")
    @Expose
    private String restaurantId;

    @SerializedName("name")
    @Expose
    private String restaurantName;

    public FavoriteRestaurant() {}

    public FavoriteRestaurant(String restaurantId, String restaurantName) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public String getRestaurantId() { return restaurantId; }

    public String getRestaurantName() { return restaurantName; }

}
