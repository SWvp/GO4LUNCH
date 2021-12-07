package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearchResults {

    // ADD EACH RESTAURANTS SEARCH IN A LIST
    @SerializedName("results")
    @Expose
    private final List<Restaurant> results;

    public NearbySearchResults(List<Restaurant> results) { this.results = results; }

    public List<Restaurant> getResults() { return results; }

}
