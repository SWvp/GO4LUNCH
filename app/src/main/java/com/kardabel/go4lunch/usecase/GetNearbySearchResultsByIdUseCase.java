package com.kardabel.go4lunch.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.Restaurant;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;

public class GetNearbySearchResultsByIdUseCase {

    private final LocationRepository locationRepository;
    private final NearbySearchResponseRepository nearbySearchResponseRepository;

    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public GetNearbySearchResultsByIdUseCase(
            LocationRepository locationRepository,
            NearbySearchResponseRepository nearbySearchResponseRepository) {

        this.locationRepository = locationRepository;
        this.nearbySearchResponseRepository = nearbySearchResponseRepository;

    }


    public LiveData<Restaurant> invoke(String placeId) {
        return Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
            String locationAsText = input.getLatitude() + "," + input.getLongitude();
            return Transformations.map(nearbySearchResponseRepository.getRestaurantListLiveData(
                    "restaurant",
                    locationAsText,
                    "1000"),
                    nearbySearchResults -> {
                        for (Restaurant restaurant : nearbySearchResults.getResults()) {
                            if (restaurant.getRestaurantId().equals(placeId)) {
                                return restaurant;

                            }
                        }
                        return null;
                    });
        });
    }
}
