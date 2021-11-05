package com.kardabel.go4lunch.usecase;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.RestaurantDetailsResponseRepository;
import com.kardabel.go4lunch.repository.NearbySearchResponseRepository;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsResultsUseCase {

    private final MediatorLiveData<List<RestaurantDetailsResult>> placeDetailsMediatorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<RestaurantDetailsResult> mPlaceDetailsResultMediatorLiveData = new MediatorLiveData<>();
    private final List<RestaurantDetailsResult> mListRestaurantDetailsResults = new ArrayList<>();
    public LiveData<NearbySearchResults> nearbySearchResultsLiveData;
    private final RestaurantDetailsResponseRepository mRestaurantDetailsResponseRepository;

    public RestaurantDetailsResultsUseCase(LocationRepository locationRepository,
                                           NearbySearchResponseRepository nearbySearchResponseRepository,
                                           RestaurantDetailsResponseRepository restaurantDetailsResponseRepository) {
        this.mRestaurantDetailsResponseRepository = restaurantDetailsResponseRepository;


       nearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
           String locationAsText = input.getLatitude() + "," + input.getLongitude();
           return nearbySearchResponseRepository.getRestaurantListLiveData(
                   "restaurant",
                   locationAsText,
                   "1000");

       });

        placeDetailsMediatorLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults -> combine(nearbySearchResults, mPlaceDetailsResultMediatorLiveData.getValue()));

        placeDetailsMediatorLiveData.addSource(mPlaceDetailsResultMediatorLiveData, restaurantDetailsResult -> combine(nearbySearchResultsLiveData.getValue(), restaurantDetailsResult));
    }

    private void combine(NearbySearchResults nearbySearchResults, @Nullable RestaurantDetailsResult restaurantDetailsResult) {
        if (nearbySearchResults != null) {
            for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
                String placeId = nearbySearchResults.getResults().get(i).getPlaceId();
                if(!mListRestaurantDetailsResults.contains(restaurantDetailsResult) || restaurantDetailsResult == null){
                    mPlaceDetailsResultMediatorLiveData.addSource(mRestaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId), restaurantDetailsResult1 -> {
                        mListRestaurantDetailsResults.add(restaurantDetailsResult1);
                        mPlaceDetailsResultMediatorLiveData.setValue(restaurantDetailsResult1);

                    });
                }
            }
            placeDetailsMediatorLiveData.setValue(mListRestaurantDetailsResults);

        }
    }

    public LiveData <List<RestaurantDetailsResult>> getPlaceDetailsResultLiveData() {
        return placeDetailsMediatorLiveData;
    }

}