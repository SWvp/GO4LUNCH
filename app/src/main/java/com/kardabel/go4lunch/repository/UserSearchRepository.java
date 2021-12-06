package com.kardabel.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class UserSearchRepository {

    private final MutableLiveData<String> searchViewResultLiveData = new MutableLiveData<>();

    public void usersSearch(String place_id) {

        searchViewResultLiveData.setValue(place_id);
    }

    public LiveData<String> getUsersSearchLiveData() {
        return searchViewResultLiveData;
    }
}
