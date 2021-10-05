package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private final LiveData<MapViewState> mapViewStatePoiLiveData;
    private Location userLocation;

    public MapViewModel(@Nullable LocationRepository locationRepository, @Nullable NearbySearchResultsUseCase nearbySearchResultsUseCase){

        mapViewStatePoiLiveData = Transformations.map(nearbySearchResultsUseCase.getNearbySearchResultsLiveData(), input -> {
            userLocation = locationRepository.getLocationLiveData().getValue();
            return map(input);

        });
    }
    // MAKE A LIST OF POI INFORMATION WITH EACH RESULT
    public MapViewState map(@NonNull NearbySearchResults nearbySearchResults){
        List<Poi> poiList = new ArrayList<>();

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++){
                String poiName = nearbySearchResults.getResults().get(i).getRestaurantName();
                String poiPlaceId = nearbySearchResults.getResults().get(i).getPlaceId();
                String poiAddress = nearbySearchResults.getResults().get(i).getRestaurantAddress();
                LatLng latLng = new LatLng(nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                                            nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());

                poiList.add(new Poi(
                        poiName,
                        poiPlaceId,
                        poiAddress,
                        latLng));

        }
        return new MapViewState(poiList, userLocation);

    }

    // LIVEDATA OBSERVED BY MAP FRAGMENT
    public LiveData<MapViewState> getMapViewStateLiveData() {
        return mapViewStatePoiLiveData;

    }
}