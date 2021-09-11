package com.kardabel.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.NearbyResults;
import com.kardabel.go4lunch.retrofit.GoogleMapsApi;
import com.kardabel.go4lunch.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyResponseRepository {

    private final GoogleMapsApi googleMapsApi;

    public NearbyResponseRepository(){
        googleMapsApi = RetrofitBuilder.getInstance().getGoogleMapsApiFromRetrofitBuilder();

    }

    public LiveData<NearbyResults> getRestaurantListLiveData(String key,
                                                             String type,
                                                             String location,
                                                             String radius){
        MutableLiveData<NearbyResults> restaurantListMutableLiveData = new MutableLiveData<>();
        googleMapsApi.searchRestaurant(key, type, location, radius).enqueue(new Callback<NearbyResults>() {
            @Override
            public void onResponse(Call<NearbyResults> call, Response<NearbyResults> response) {
                restaurantListMutableLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<NearbyResults> call, Throwable t) {
                t.printStackTrace();

            }
        });
        return restaurantListMutableLiveData;

    }
}
