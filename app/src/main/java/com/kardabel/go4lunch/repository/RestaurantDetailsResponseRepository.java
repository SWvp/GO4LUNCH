package com.kardabel.go4lunch.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.BuildConfig;
import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailsResponseRepository {

    private final GoogleMapsApi googleMapsApi;
    private final  Application application;

    // THIS MAP WILL ALLOW DEVICE TO CACHE DATA TO AVOID USELESS DATA CALL
    private final Map<String, RestaurantDetailsResult> cache = new HashMap<>(2000);

    public RestaurantDetailsResponseRepository(GoogleMapsApi googleMapsApi, Application application) {
        this.googleMapsApi = googleMapsApi;
        this.application = application;
    }

    public LiveData<RestaurantDetailsResult> getRestaurantDetailsLiveData(String restaurantId) {

        String key = BuildConfig.GOOGLE_PLACES_KEY;
        String FIELDS = application.getString(R.string.restaurant_details_fields);

        MutableLiveData<RestaurantDetailsResult> placeDetailsResultMutableLiveData =
                new MutableLiveData<>();

        RestaurantDetailsResult restaurantDetailsResult = cache.get(restaurantId);
        if (restaurantDetailsResult != null) {

            placeDetailsResultMutableLiveData.setValue(restaurantDetailsResult);

        } else {

            Call<RestaurantDetailsResult> call = googleMapsApi.searchRestaurantDetails(key, restaurantId, FIELDS);

            call.enqueue(new Callback<RestaurantDetailsResult>() {
                @Override
                public void onResponse(@NonNull Call<RestaurantDetailsResult> call,
                                       @NonNull Response<RestaurantDetailsResult> response) {
                    if (response.body() != null) {
                        cache.put(restaurantId, response.body());
                        placeDetailsResultMutableLiveData.setValue(response.body());

                    }else{
                        Log.d("Response errorBody", String.valueOf(response.errorBody()));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RestaurantDetailsResult> call, @NonNull Throwable t) {
                    Log.d("pipo", "Detail called issues");

                }
            });
        }
        return placeDetailsResultMutableLiveData;

    }
}
