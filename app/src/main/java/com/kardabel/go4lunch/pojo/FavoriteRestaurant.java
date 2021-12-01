package com.kardabel.go4lunch.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteRestaurant that = (FavoriteRestaurant) o;
        return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(restaurantName, that.restaurantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, restaurantName);
    }

    @Override
    public String toString() {
        return "FavoriteRestaurant{" +
                "restaurantId='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                '}';
    }
}
