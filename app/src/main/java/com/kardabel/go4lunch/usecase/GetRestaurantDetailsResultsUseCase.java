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

    public static final String RESTAURANT = "restaurant";
    public static final String RADIUS = "1000";

    private final MediatorLiveData<List<RestaurantDetailsResult>> restaurantsDetailsMediatorLiveData =
            new MediatorLiveData<>();
    private final MediatorLiveData<RestaurantDetailsResult> restaurantDetailsMediatorLiveData =
            new MediatorLiveData<>();
    private final List<RestaurantDetailsResult> restaurantDetailsList =
            new ArrayList<>();
    public LiveData<NearbySearchResults> nearbySearchResultsLiveData;
    private final RestaurantDetailsResponseRepository restaurantDetailsResponseRepository;

    public GetRestaurantDetailsResultsUseCase(LocationRepository locationRepository,
                                              NearbySearchResponseRepository nearbySearchResponseRepository,
                                              RestaurantDetailsResponseRepository restaurantDetailsResponseRepository) {

        this.restaurantDetailsResponseRepository = restaurantDetailsResponseRepository;

        // GET THE NEARBY SEARCH RESULT WITH USER LOCATION AS TRIGGER
        nearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
            String locationAsText = input.getLatitude() + "," + input.getLongitude();
            return nearbySearchResponseRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    RADIUS);

        });

        // THE COMBINE METHOD ALLOW NULL ARGS, SO LETS NEARBY TRIGGER THE COMBINE,
        // THE DETAILS WILL BE TRIGGER BY ADDSOURCE IN COMBINE
        restaurantsDetailsMediatorLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults ->
                combine(
                        nearbySearchResults,
                        restaurantDetailsMediatorLiveData.getValue()));

        restaurantsDetailsMediatorLiveData.addSource(restaurantDetailsMediatorLiveData, restaurantDetailsResult ->
                combine(
                        nearbySearchResultsLiveData.getValue(),
                        restaurantDetailsResult));

    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable RestaurantDetailsResult restaurantDetailsResult) {

        if (nearbySearchResults != null) {

            for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {

                String placeId = nearbySearchResults.getResults().get(i).getRestaurantId();

                if (!restaurantDetailsList.contains(restaurantDetailsResult) || restaurantDetailsResult == null) {

                    restaurantDetailsMediatorLiveData.addSource(
                            restaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId),
                            restaurantDetailsResult1 -> {
                                restaurantDetailsList.add(restaurantDetailsResult1);
                                restaurantDetailsMediatorLiveData.setValue(restaurantDetailsResult1);

                            });
                }
            }
            restaurantsDetailsMediatorLiveData.setValue(restaurantDetailsList);

        }
    }

    public LiveData<List<RestaurantDetailsResult>> invoke() {
        return restaurantsDetailsMediatorLiveData;

    }
}