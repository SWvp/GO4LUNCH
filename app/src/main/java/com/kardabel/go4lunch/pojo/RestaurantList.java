package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantList {

    // ADD EACH RESTAURANTDETAILS IN A LIST

    @SerializedName("results")
    private final List<RestaurantDetails> results;

    public RestaurantList(List<RestaurantDetails> results) { this.results = results; }

    public List<RestaurantDetails> getResults() { return results; }
}
