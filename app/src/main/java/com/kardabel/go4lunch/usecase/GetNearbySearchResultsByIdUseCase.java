package com.kardabel.go4lunch.usecase;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.pojo.Restaurant;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;

public class GetNearbySearchResultsByIdUseCase {

    public static final String RESTAURANT = "restaurant";
    //public static final String RADIUS = "1000";
    private final LocationRepository locationRepository;
    private final NearbySearchResponseRepository nearbySearchResponseRepository;
    private final Application application;


    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public GetNearbySearchResultsByIdUseCase(
            LocationRepository locationRepository,
            NearbySearchResponseRepository nearbySearchResponseRepository,
            Application application) {

        this.locationRepository = locationRepository;
        this.nearbySearchResponseRepository = nearbySearchResponseRepository;
        this.application = application;

    }


    public LiveData<Restaurant> invoke(String placeId) {
        return Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
            String locationAsText = input.getLatitude() + "," + input.getLongitude();
            return Transformations.map(nearbySearchResponseRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius)),
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
