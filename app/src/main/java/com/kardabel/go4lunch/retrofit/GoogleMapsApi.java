package com.kardabel.go4lunch.retrofit;

import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.pojo.RestaurantDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApi {

    @GET("maps/api/place/nearbysearch/json")
    Call<PlaceSearchResults> searchRestaurant(
            @Query("key") String key,
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") String radius
    );

    @GET("maps/api/place/details/json")
    Call<PlaceDetailsResult> searchRestaurantDetails(
            @Query("key") String key,
            @Query("place_id") String place_id,
            @Query("fields") String fields
    );

}
