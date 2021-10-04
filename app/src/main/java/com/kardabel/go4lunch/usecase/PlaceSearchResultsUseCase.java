package com.kardabel.go4lunch.usecase;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.PlaceSearchResponseRepository;

public class PlaceSearchResultsUseCase {

    public LiveData<PlaceSearchResults> placeSearchResultsLiveData;

    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public PlaceSearchResultsUseCase(LocationRepository locationRepository, PlaceSearchResponseRepository placeSearchResponseRepository) {

        placeSearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<PlaceSearchResults>>() {
            @Override
            public LiveData<PlaceSearchResults> apply(Location input) {
                 String locationAsText = input.getLatitude() + "," + input.getLongitude();
                 return placeSearchResponseRepository.getRestaurantListLiveData(
                        "restaurant",
                        locationAsText,
                        "1000");

            }
        });
    }
    public LiveData<PlaceSearchResults> getPlaceSearchResultsLiveData() { return placeSearchResultsLiveData; }
}
