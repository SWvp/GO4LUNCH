package com.kardabel.go4lunch.retrofit;

import com.kardabel.go4lunch.pojo.NearbyResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApi {

    @GET("/maps/api/place/nearbysearch/json")
    Call<NearbyResults> searchRestaurant(
            @Query("key") String key,
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") String radius
    );

}
