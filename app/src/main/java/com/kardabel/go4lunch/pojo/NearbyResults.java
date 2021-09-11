package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyResults {

    // ADD EACH RESTAURANTS DETAILS IN A LIST

    @SerializedName("results")
    private final List<RestaurantDetails> results;

    public NearbyResults(List<RestaurantDetails> results) { this.results = results; }

    public List<RestaurantDetails> getResults() { return results; }
}
