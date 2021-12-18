package com.kardabel.go4lunch.retrofit;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.Predictions;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApi {

    @GET("maps/api/place/nearbysearch/json")
    Call<NearbySearchResults> searchRestaurant(
            @Query("key") String key,
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") String radius
    );

    @GET("maps/api/place/details/json")
    Call<RestaurantDetailsResult> searchRestaurantDetails(
            @Query("key") String key,
            @Query("place_id") String place_id,
            @Query("fields") String fields
    );

    @GET("maps/api/place/autocomplete/json")
    Call<Predictions> autocompleteResult(
            @Query("key") String key,
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("input") String input
    );



}
