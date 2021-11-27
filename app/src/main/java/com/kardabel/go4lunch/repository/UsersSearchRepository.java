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

    private final MutableLiveData<String> searchViewResultLiveData = new MutableLiveData<>();

    public void usersSearch(String place_id) {

        searchViewResultLiveData.setValue(place_id);
    }

    public LiveData<String> getUsersSearchLiveData() {
        return searchViewResultLiveData;
    }
}
