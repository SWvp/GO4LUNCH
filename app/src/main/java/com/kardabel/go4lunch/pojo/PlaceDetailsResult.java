package com.kardabel.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceDetailsResult {

    @SerializedName("result")
       private final RestaurantDetails result;


    public PlaceDetailsResult(RestaurantDetails result) { this.result = result; }

    public RestaurantDetails getDetailsResult() { return result; }



}
