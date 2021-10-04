package com.kardabel.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceSearchResponseRepository {

    private final GoogleMapsApi googleMapsApi;
    private final String key = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";

    private Map<String, PlaceSearchResults> cache = new HashMap<>(2000);

    public PlaceSearchResponseRepository(GoogleMapsApi googleMapsApi){
        this.googleMapsApi = googleMapsApi;

    }

    public LiveData<PlaceSearchResults> getRestaurantListLiveData(String type,
                                                                  String location,
                                                                  String radius){

        MutableLiveData<PlaceSearchResults> placeSearchResultsMutableLiveData = new MutableLiveData<>();
        PlaceSearchResults placeSearchResults = cache.get(location);
        if (placeSearchResults != null) {
            placeSearchResultsMutableLiveData.setValue(placeSearchResults);
        } else {
            googleMapsApi.searchRestaurant(key, type, location, radius).enqueue(
                    new Callback<PlaceSearchResults>() {
                        @Override
                        public void onResponse(Call<PlaceSearchResults> call, Response<PlaceSearchResults> response) {
                            if (response.body() != null) {
                                cache.put(location, response.body());
                                placeSearchResultsMutableLiveData.setValue(response.body());

                            }
                        }

                        @Override
                        public void onFailure(Call<PlaceSearchResults> call, Throwable t) {
                            t.printStackTrace();

                        }
                    });
        }
        return placeSearchResultsMutableLiveData;

    }
}
