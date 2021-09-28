package com.kardabel.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.pojo.RestaurantDetails;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.retrofit.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyResponseRepository {

    private final GoogleMapsApi googleMapsApi;
    private List<PlaceDetailsResult> detailsList;
    private MutableLiveData<List<PlaceDetailsResult>> restaurantSubDetailsMutableLiveData = new MutableLiveData<>();

    public NearbyResponseRepository(){
        googleMapsApi = RetrofitBuilder.getInstance().getGoogleMapsApiFromRetrofitBuilder();

    }

    public LiveData<PlaceSearchResults> getRestaurantListLiveData(String key,
                                                                  String type,
                                                                  String location,
                                                                  String radius){

        detailsList = new ArrayList<>();
        MutableLiveData<PlaceSearchResults> placeSearchResultsMutableLiveData = new MutableLiveData<>();
        googleMapsApi.searchRestaurant(key, type, location, radius).enqueue(
                new Callback<PlaceSearchResults>() {
            @Override
            public void onResponse(Call<PlaceSearchResults> call, Response<PlaceSearchResults> response) {
                placeSearchResultsMutableLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<PlaceSearchResults> call, Throwable t) {
                t.printStackTrace();

            }
        });
        return placeSearchResultsMutableLiveData;

    }

    public void getRestaurantOpeningHours(String key,
                                          String place_id){

        googleMapsApi.searchRestaurantDetails(key, place_id).enqueue(new Callback<PlaceDetailsResult>() {
            @Override
            public void onResponse(Call<PlaceDetailsResult> call, Response<PlaceDetailsResult> response) {


            }

            @Override
            public void onFailure(Call<PlaceDetailsResult> call, Throwable t) {

            }
        });
    }

    public LiveData<List<PlaceDetailsResult>> getPlaceDetailResultLiveData(){
        return  restaurantSubDetailsMutableLiveData;

    }
}
