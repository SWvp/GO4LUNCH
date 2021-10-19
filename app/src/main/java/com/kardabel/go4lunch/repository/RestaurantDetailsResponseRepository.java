package com.kardabel.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsResponseRepository {

    private final GoogleMapsApi googleMapsApi;
    private final String key = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";
    private static final String FIELDS = "formatted_phone_number,opening_hours,website,place_id";

    private final Map<String, RestaurantDetailsResult> cache = new HashMap<>(2000);

    public RestaurantDetailsResponseRepository(GoogleMapsApi googleMapsApi) {
        this.googleMapsApi = googleMapsApi;
    }

    public LiveData<RestaurantDetailsResult> getRestaurantDetailsLiveData(String place_id) {

        MutableLiveData<RestaurantDetailsResult> placeDetailsResultMutableLiveData = new MutableLiveData<>();
        RestaurantDetailsResult restaurantDetailsResult = cache.get(place_id);
        if (restaurantDetailsResult != null) {
            placeDetailsResultMutableLiveData.setValue(restaurantDetailsResult);
        } else {
            Call<RestaurantDetailsResult> call = googleMapsApi.searchRestaurantDetails(key, place_id, FIELDS);
            call.enqueue(new Callback<RestaurantDetailsResult>() {
                @Override
                public void onResponse(@NonNull Call<RestaurantDetailsResult> call,
                                       @NonNull Response<RestaurantDetailsResult> response) {
                    if (response.body() != null) {
                        cache.put(place_id, response.body());
                        placeDetailsResultMutableLiveData.setValue(response.body());

                    }else{
                        Log.d("Response errorBody", String.valueOf(response.errorBody()));
                    }
                }

                @Override
                public void onFailure(Call<RestaurantDetailsResult> call, Throwable t) {
                    Log.d("pipo", "Detail called issues");

                }
            });
        }
        return placeDetailsResultMutableLiveData;

    }
}
