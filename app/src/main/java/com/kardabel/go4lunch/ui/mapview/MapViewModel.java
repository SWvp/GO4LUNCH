package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.model.WorkmateWithFavoriteRestaurant;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.WorkmatesRepository;
import com.kardabel.go4lunch.usecase.GetNearbySearchResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    public static final float ZOOM_FOCUS = 15F;

    private final MediatorLiveData<MapViewState> mapViewStatePoiMediatorLiveData = new MediatorLiveData<>();

    public MapViewModel(@NonNull LocationRepository locationRepository,
                        @NonNull GetNearbySearchResultsUseCase getNearbySearchResultsUseCase,
                        @NonNull WorkmatesRepository workmatesRepository) {

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<NearbySearchResults> nearbySearchResultsLiveData = getNearbySearchResultsUseCase.getNearbySearchResultsLiveData();
        LiveData<List<WorkmateWithFavoriteRestaurant>> favoriteRestaurantsLiveData = workmatesRepository.getWorkmatesWithFavoriteRestaurant();

        // OBSERVERS

        mapViewStatePoiMediatorLiveData.addSource(locationLiveData, location -> MapViewModel.this.combine(
                nearbySearchResultsLiveData.getValue(),
                location,
                favoriteRestaurantsLiveData.getValue()));
        mapViewStatePoiMediatorLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults -> combine(
                nearbySearchResults,
                locationLiveData.getValue(),
                favoriteRestaurantsLiveData.getValue()));
        mapViewStatePoiMediatorLiveData.addSource(favoriteRestaurantsLiveData, userWithFavoriteRestaurants -> combine(
                nearbySearchResultsLiveData.getValue(),
                locationLiveData.getValue(),
                userWithFavoriteRestaurants));

    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable Location location,
                         @Nullable List<WorkmateWithFavoriteRestaurant> workmateWithFavoriteRestaurant) {
        if (nearbySearchResults != null && location != null) {
            mapViewStatePoiMediatorLiveData.setValue(map(nearbySearchResults, location, workmateWithFavoriteRestaurant));
        }


    }

    private MapViewState map(NearbySearchResults nearbySearchResults, Location location, List<WorkmateWithFavoriteRestaurant> workmateWithFavoriteRestaurant){
        List<Poi> poiList = new ArrayList<>();
        List<String> restaurantAsFavoriteId = new ArrayList<>();

        if (workmateWithFavoriteRestaurant != null) {
            for (int i = 0; i < workmateWithFavoriteRestaurant.size(); i++) {
                restaurantAsFavoriteId.add(workmateWithFavoriteRestaurant.get(i).getRestaurantId());

            }
        }

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
            boolean isFavorite = false;
            String poiName = nearbySearchResults.getResults().get(i).getRestaurantName();
            String poiPlaceId = nearbySearchResults.getResults().get(i).getRestaurantId();
            String poiAddress = nearbySearchResults.getResults().get(i).getRestaurantAddress();
            LatLng latLng = new LatLng(nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            if(workmateWithFavoriteRestaurant != null && restaurantAsFavoriteId.contains(poiPlaceId)){
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