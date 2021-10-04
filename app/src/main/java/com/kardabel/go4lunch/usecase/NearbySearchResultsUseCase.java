package com.kardabel.go4lunch.usecase;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;

public class NearbySearchResultsUseCase {

    public LiveData<NearbySearchResults> NearbySearchResultsLiveData;

    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public NearbySearchResultsUseCase(LocationRepository locationRepository, NearbySearchResponseRepository nearbySearchResponseRepository) {

        NearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbySearchResults>>() {
            @Override
            public LiveData<NearbySearchResults> apply(Location input) {
                 String locationAsText = input.getLatitude() + "," + input.getLongitude();
                 return nearbySearchResponseRepository.getRestaurantListLiveData(
                        "restaurant",
                        locationAsText,
                        "1000");

            }
        });
    }
    public LiveData<NearbySearchResults> getNearbySearchResultsLiveData() { return NearbySearchResultsLiveData; }
}
