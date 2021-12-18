package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.SerializedName;

public class RestaurantDetailsResult {

    @SerializedName("result")
    private final RestaurantDetails result;


    public RestaurantDetailsResult(RestaurantDetails result) {
        this.result = result;
    }

    public RestaurantDetails getDetailsResult() {
        return result;
    }


}
