package com.kardabel.go4lunch.repository;


import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.SearchViewResult;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersSearchRepository {

    private final GoogleMapsApi googleMapsApi;
    private final String key = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";
    private static final String FIELDS = "place_id, name, vicinity, photos, geometry, opening_hours, rating, user_rating_total, permanently_closed, formatted_phone_number, website, formatted_phone_number,opening_hours,website,place_id";
    private final MutableLiveData<SearchViewResult> searchViewResultLiveData = new MutableLiveData<>();

    private final Map<String, SearchViewResult> cache = new HashMap<>(2000);

    public UsersSearchRepository(GoogleMapsApi googleMapsApi) {
        this.googleMapsApi = googleMapsApi;
    }

    public void usersSearch(String place_id) {

        SearchViewResult searchViewResult = cache.get(place_id);
        if (searchViewResult != null) {
            searchViewResultLiveData.setValue(searchViewResult);
        } else {
            Call<SearchViewResult> call = googleMapsApi.searchViewResult(key, place_id, FIELDS);
            call.enqueue(new Callback<SearchViewResult>() {
                @Override
                public void onResponse(@NonNull Call<SearchViewResult> call,
                                       @NonNull Response<SearchViewResult> response) {
                    if (response.body() != null) {
                        cache.put(place_id, response.body());
                        searchViewResultLiveData.setValue(response.body());

                    }else{
                        Log.d("Response errorBody", String.valueOf(response.errorBody()));
                    }
                }

                @Override
                public void onFailure(Call<SearchViewResult> call, Throwable t) {
                    Log.d("pipo", "Detail called issues");

                }
            });
        }
    }

    public LiveData<SearchViewResult> getUsersSearchLiveData() {
        return searchViewResultLiveData;
    }
}
