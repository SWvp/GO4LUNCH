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

public class GetRestaurantDetailsResultsUseCase {

    private final MediatorLiveData<List<RestaurantDetailsResult>> placeDetailsMediatorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<RestaurantDetailsResult> placeDetailsResultMediatorLiveData = new MediatorLiveData<>();
    private final List<RestaurantDetailsResult> mListRestaurantDetailsResults = new ArrayList<>();
    public LiveData<NearbySearchResults> nearbySearchResultsLiveData;
    private final RestaurantDetailsResponseRepository mRestaurantDetailsResponseRepository;

    public GetRestaurantDetailsResultsUseCase(LocationRepository locationRepository,
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

        placeDetailsMediatorLiveData.addSource(
                nearbySearchResultsLiveData,
                nearbySearchResults -> combine(
                        nearbySearchResults,
                        placeDetailsResultMediatorLiveData.getValue()));

        placeDetailsMediatorLiveData.addSource(
                placeDetailsResultMediatorLiveData,
                restaurantDetailsResult -> combine(
                        nearbySearchResultsLiveData.getValue(),
                        restaurantDetailsResult));
    }

    private void combine(
            @Nullable NearbySearchResults nearbySearchResults,
            @Nullable RestaurantDetailsResult restaurantDetailsResult) {
        if (nearbySearchResults != null) {
            for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
                String placeId = nearbySearchResults.getResults().get(i).getRestaurantId();
                if (!mListRestaurantDetailsResults.contains(restaurantDetailsResult) || restaurantDetailsResult == null) {
                    placeDetailsResultMediatorLiveData.addSource(
                            mRestaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId),
                            restaurantDetailsResult1 -> {
                                mListRestaurantDetailsResults.add(restaurantDetailsResult1);
                                placeDetailsResultMediatorLiveData.setValue(restaurantDetailsResult1);

                            });
                }
            }
            placeDetailsMediatorLiveData.setValue(mListRestaurantDetailsResults);

        }
    }

    public LiveData<List<RestaurantDetailsResult>> getPlaceDetailsResultLiveData() {
        return placeDetailsMediatorLiveData;
    }

}