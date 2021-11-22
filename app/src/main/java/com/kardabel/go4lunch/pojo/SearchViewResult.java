package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;


public class SearchViewResult {

    @SerializedName("result")
    private final RestaurantSearch result;


    public SearchViewResult(RestaurantSearch result) { this.result = result; }

    public RestaurantSearch getSearchViewResult() { return result; }
}
