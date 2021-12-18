package com.kardabel.go4lunch.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;

public class GetNearbySearchResultsUseCase {

    public static final String RESTAURANT = "restaurant";
    public static final String RADIUS = "1000";
    public LiveData<NearbySearchResults> NearbySearchResultsLiveData;

    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public GetNearbySearchResultsUseCase(LocationRepository locationRepository,
                                         NearbySearchResponseRepository nearbySearchResponseRepository) {

        NearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
             String locationAsText = input.getLatitude() + "," + input.getLongitude();
             return nearbySearchResponseRepository.getRestaurantListLiveData(
                     RESTAURANT,
                    locationAsText,
                     RADIUS);

        });
    }
    public LiveData<NearbySearchResults> invoke() { return NearbySearchResultsLiveData; }
}
