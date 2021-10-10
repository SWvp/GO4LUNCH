package com.kardabel.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailsResponseRepository {

    private final GoogleMapsApi googleMapsApi;
    private final String key = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";
    private static final String FIELDS = "formatted_phone_number,opening_hours,website,place_id";

    private final Map<String, PlaceDetailsResult> cache = new HashMap<>(2000);

    public PlaceDetailsResponseRepository(GoogleMapsApi googleMapsApi) {
        this.googleMapsApi = googleMapsApi;
    }

    public LiveData<PlaceDetailsResult> getRestaurantDetailsLiveData(String place_id) {

        MutableLiveData<PlaceDetailsResult> placeDetailsResultMutableLiveData = new MutableLiveData<>();
        PlaceDetailsResult placeDetailsResult = cache.get(place_id);
        if (placeDetailsResult != null) {
            placeDetailsResultMutableLiveData.setValue(placeDetailsResult);
        } else {
            Call<PlaceDetailsResult> call = googleMapsApi.searchRestaurantDetails(key, place_id, FIELDS);
            call.enqueue(new Callback<PlaceDetailsResult>() {
                @Override
                public void onResponse(@NonNull Call<PlaceDetailsResult> call,
                                       @NonNull Response<PlaceDetailsResult> response) {
                    if (response.body() != null) {
                        cache.put(place_id, response.body());
                        placeDetailsResultMutableLiveData.setValue(response.body());

                    }else{
                        Log.d("Response errorBody", String.valueOf(response.errorBody()));
                    }
                }

                @Override
                public void onFailure(Call<PlaceDetailsResult> call, Throwable t) {
                    Log.d("pipo", "Detail called issues");

                }
            });
        }
        return placeDetailsResultMutableLiveData;

    }
}
