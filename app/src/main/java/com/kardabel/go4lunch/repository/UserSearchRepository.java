package com.kardabel.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class UserSearchRepository {

    private final MutableLiveData<String> searchViewResultLiveData = new MutableLiveData<>();

    public void usersSearch(String restaurantId) {

        searchViewResultLiveData.setValue(restaurantId);
    }

    public LiveData<String> getUsersSearchLiveData() {
        return searchViewResultLiveData;
    }
}
