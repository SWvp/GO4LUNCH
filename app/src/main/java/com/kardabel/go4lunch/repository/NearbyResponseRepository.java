package com.kardabel.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.RestaurantList;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class NearbyResponseRepository {

    private final GoogleMapsApi googleMapsApi;

    public NearbyResponseRepository(){
        googleMapsApi = RetrofitBuilder.getInstance().getGoogleMapsApiFromRetrofitBuilder();

    }

    public LiveData<RestaurantList> getRestaurantListLiveData(String key,
                                                              String type,
                                                              String location,
                                                              String radius){
        MutableLiveData<RestaurantList> restaurantListMutableLiveData = new MutableLiveData<>();
        googleMapsApi.searchRestaurant(key, type, location, radius).enqueue(new Callback<RestaurantList>() {
            @Override
            public void onResponse(Call<RestaurantList> call, Response<RestaurantList> response) {
                restaurantListMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<RestaurantList> call, Throwable t) {

            }
        });
        return restaurantListMutableLiveData;

    }

}
