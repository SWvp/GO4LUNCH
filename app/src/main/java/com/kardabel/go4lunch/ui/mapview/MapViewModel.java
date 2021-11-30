package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.model.WorkmateWhoMadeRestaurantChoice;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.UsersSearchRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.repository.WorkmatesWhoMadeRestaurantChoiceRepository;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    public static final float ZOOM_FOCUS = 15F;

    private final MediatorLiveData<MapViewState> mapViewStatePoiMediatorLiveData = new MediatorLiveData<>();

    public MapViewModel(@NonNull LocationRepository locationRepository,
                        @NonNull GetNearbySearchResultsUseCase getNearbySearchResultsUseCase,
                        @NonNull WorkmatesWhoMadeRestaurantChoiceRepository workmatesWhoMadeRestaurantChoiceRepository,
                        @NonNull UsersSearchRepository usersSearchRepository) {

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<NearbySearchResults> nearbySearchResultsLiveData = getNearbySearchResultsUseCase.getNearbySearchResultsLiveData();
        LiveData<List<WorkmateWhoMadeRestaurantChoice>> favoriteRestaurantsLiveData = workmatesWhoMadeRestaurantChoiceRepository.getWorkmatesWhoMadeRestaurantChoice();
        LiveData<String> usersSearchLiveData = usersSearchRepository.getUsersSearchLiveData();

        // OBSERVERS

        mapViewStatePoiMediatorLiveData.addSource(locationLiveData, location -> combine(
                nearbySearchResultsLiveData.getValue(),
                location,
                favoriteRestaurantsLiveData.getValue(),
                usersSearchLiveData.getValue()));
        mapViewStatePoiMediatorLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults -> combine(
                nearbySearchResults,
                locationLiveData.getValue(),
                favoriteRestaurantsLiveData.getValue(),
                usersSearchLiveData.getValue()));
        mapViewStatePoiMediatorLiveData.addSource(favoriteRestaurantsLiveData, userWithFavoriteRestaurants -> combine(
                nearbySearchResultsLiveData.getValue(),
                locationLiveData.getValue(),
                userWithFavoriteRestaurants,
                usersSearchLiveData.getValue()));
        mapViewStatePoiMediatorLiveData.addSource(usersSearchLiveData, usersSearch -> combine(
                nearbySearchResultsLiveData.getValue(),
                locationLiveData.getValue(),
                favoriteRestaurantsLiveData.getValue(),
                usersSearch));

    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable Location location,
                         @Nullable List<WorkmateWhoMadeRestaurantChoice> workmateWhoMadeRestaurantChoice,
                         @Nullable String usersSearch) {
        if (usersSearch != null && usersSearch.length() > 0) {
            mapViewStatePoiMediatorLiveData.setValue(mapUsersSearch(
                    nearbySearchResults,
                    location,
                    workmateWhoMadeRestaurantChoice,
                    usersSearch));

        } else if (nearbySearchResults != null && location != null) {
            mapViewStatePoiMediatorLiveData.setValue(map(nearbySearchResults, location, workmateWhoMadeRestaurantChoice));
        }
    }

    // MAP WITH USER'S SEARCH ONLY
    private MapViewState mapUsersSearch(
            NearbySearchResults nearbySearchResults,
            Location location,
            List<WorkmateWhoMadeRestaurantChoice> workmateWhoMadeRestaurantChoice,
            String usersSearch
    ) {
        List<Poi> poiList = new ArrayList<>();
        List<String> restaurantAsFavoriteId = new ArrayList<>();

        if (workmateWhoMadeRestaurantChoice != null) {
            for (int i = 0; i < workmateWhoMadeRestaurantChoice.size(); i++) {
                restaurantAsFavoriteId.add(workmateWhoMadeRestaurantChoice.get(i).getRestaurantId());

            }
        }

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
            if (nearbySearchResults.getResults().get(i).getRestaurantName().contains(usersSearch)) {
                boolean isFavorite = false;
                String poiName = nearbySearchResults.getResults().get(i).getRestaurantName();
                String poiPlaceId = nearbySearchResults.getResults().get(i).getRestaurantId();
                String poiAddress = nearbySearchResults.getResults().get(i).getRestaurantAddress();
                LatLng latLng = new LatLng(
                        nearbySearchResults
                                .getResults()
                                .get(i)
                                .getRestaurantGeometry()
                                .getRestaurantLatLngLiteral()
                                .getLat(),
                        nearbySearchResults
                                .getResults()
                                .get(i)
                                .getRestaurantGeometry()
                                .getRestaurantLatLngLiteral()
                                .getLng());
                if (
                        workmateWhoMadeRestaurantChoice != null
                                && restaurantAsFavoriteId.contains(poiPlaceId)) {
                    isFavorite = true;
                }

                poiList.add(
                        new Poi(
                                poiName,
                                poiPlaceId,
                                poiAddress,
                                latLng,
                                isFavorite
                        )
                );
            }
        }

        LatLng userLocation = new LatLng(
                location.getLatitude(),
                location.getLongitude());

        return new MapViewState(
                poiList,
                new LatLng(
                        userLocation.latitude,
                        userLocation.longitude),
                ZOOM_FOCUS);

    }

    private MapViewState map(
            NearbySearchResults nearbySearchResults,
            Location location,
            List<WorkmateWhoMadeRestaurantChoice> workmateWhoMadeRestaurantChoice) {

        List<Poi> poiList = new ArrayList<>();
        List<String> restaurantWithWorkmate = new ArrayList<>();

        if (workmateWhoMadeRestaurantChoice != null) {
            for (int i = 0; i < workmateWhoMadeRestaurantChoice.size(); i++) {
                restaurantWithWorkmate.add(workmateWhoMadeRestaurantChoice.get(i).getRestaurantId());

            }
        }

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
            boolean isFavorite = false;
            String poiName = nearbySearchResults.getResults().get(i).getRestaurantName();
            String poiPlaceId = nearbySearchResults.getResults().get(i).getRestaurantId();
            String poiAddress = nearbySearchResults.getResults().get(i).getRestaurantAddress();
            LatLng latLng = new LatLng(
                    nearbySearchResults
                            .getResults()
                            .get(i)
                            .getRestaurantGeometry()
                            .getRestaurantLatLngLiteral()
                            .getLat(),
                    nearbySearchResults
                            .getResults()
                            .get(i)
                            .getRestaurantGeometry()
                            .getRestaurantLatLngLiteral()
                            .getLng());
            if (
                    workmateWhoMadeRestaurantChoice != null
                            && restaurantWithWorkmate.contains(poiPlaceId)) {
                isFavorite = true;
            }

            poiList.add(
                    new Poi(
                            poiName,
                            poiPlaceId,
                            poiAddress,
                            latLng,
                            isFavorite
                    )
            );

        }

        LatLng userLocation = new LatLng(
                location.getLatitude(),
                location.getLongitude());

        return new MapViewState(
                poiList,
                new LatLng(
                        userLocation.latitude,
                        userLocation.longitude),
                ZOOM_FOCUS);

    }

    // LIVEDATA OBSERVED BY MAP FRAGMENT
    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewStatePoiMediatorLiveData;
    }
}