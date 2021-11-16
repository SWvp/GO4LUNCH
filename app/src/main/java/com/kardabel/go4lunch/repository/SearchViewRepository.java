package com.kardabel.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewRepository {


    private final GoogleMapsApi googleMapsApi;
    private final String key = "AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8";

    private Map<String, NearbySearchResults> cache = new HashMap<>(2000);

    public SearchViewRepository(GoogleMapsApi googleMapsApi){
        this.googleMapsApi = googleMapsApi;

    }

    public LiveData<NearbySearchResults> getSearchViewListLiveData(String location,
                                                                   String keyword){

        String type = "restaurant";
        String radius = "1000";

        MutableLiveData<NearbySearchResults> NearbySearchResultsMutableLiveData = new MutableLiveData<>();
        NearbySearchResults nearbySearchResults = cache.get(location);
        if (nearbySearchResults != null) {
            NearbySearchResultsMutableLiveData.setValue(nearbySearchResults);
        } else {
            googleMapsApi.searchRestaurantWithSearchView(key, type, location, radius, keyword).enqueue(
                    new Callback<NearbySearchResults>() {
                        @Override
                        public void onResponse(Call<NearbySearchResults> call, Response<NearbySearchResults> response) {
                            if (response.body() != null) {
                                cache.put(location, response.body());
                                NearbySearchResultsMutableLiveData.setValue(response.body());

                            }
                        }

                        @Override
                        public void onFailure(Call<NearbySearchResults> call, Throwable t) {
                            t.printStackTrace();

                        }
                    });
        }
        return NearbySearchResultsMutableLiveData;

    }
}
