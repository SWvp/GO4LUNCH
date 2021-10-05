package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearchResults {

    // ADD EACH RESTAURANTS SEARCH IN A LIST
    @SerializedName("results")
    @Expose
    private final List<RestaurantSearch> results;

    public NearbySearchResults(List<RestaurantSearch> results) { this.results = results; }

    public List<RestaurantSearch> getResults() { return results; }

}