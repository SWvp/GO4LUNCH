package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    public static final float ZOOM_FOCUS = 15F;

    private final MediatorLiveData<MapViewState> mapViewStatePoiLiveData = new MediatorLiveData<>();

    public MapViewModel(@NonNull LocationRepository locationRepository, @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase) {

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<NearbySearchResults> nearbySearchResultsLiveData = nearbySearchResultsUseCase.getNearbySearchResultsLiveData();

        mapViewStatePoiLiveData.addSource(locationLiveData, location -> combine(nearbySearchResultsLiveData.getValue(), location));
        mapViewStatePoiLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults -> combine(nearbySearchResults, locationLiveData.getValue()));

    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults, @Nullable Location location) {
        if (nearbySearchResults == null || location == null) {
            return;
        }
        List<Poi> poiList = new ArrayList<>();

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++) {
            String poiName = nearbySearchResults.getResults().get(i).getRestaurantName();
            String poiPlaceId = nearbySearchResults.getResults().get(i).getPlaceId();
            String poiAddress = nearbySearchResults.getResults().get(i).getRestaurantAddress();
            LatLng latLng = new LatLng(nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());

            poiList.add(
                    new Poi(
                            poiName,
                            poiPlaceId,
                            poiAddress,
                            latLng
                    )
            );

        }

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mapViewStatePoiLiveData.setValue(new MapViewState(poiList, new LatLng(userLocation.latitude, userLocation.longitude), ZOOM_FOCUS));
    }

    // LIVEDATA OBSERVED BY MAP FRAGMENT
    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewStatePoiLiveData;
    }
}