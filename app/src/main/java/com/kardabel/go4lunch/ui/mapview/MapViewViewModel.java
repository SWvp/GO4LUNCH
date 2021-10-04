package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.PlaceSearchResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private LiveData<MapViewViewState> mapViewStatePoiLiveData;
    private Location userLocation;

    public MapViewViewModel(@Nullable LocationRepository locationRepository, @Nullable PlaceSearchResultsUseCase placeSearchResultsUseCase){

        mapViewStatePoiLiveData = Transformations.map(placeSearchResultsUseCase.getPlaceSearchResultsLiveData(), input -> {
            userLocation = locationRepository.getLocationLiveData().getValue();
            return map(input);

        });
    }
    // MAKE A LIST OF POI INFORMATION WITH EACH RESULT
    public MapViewViewState map(@NonNull PlaceSearchResults placeSearchResults){
        List<Poi> poiList = new ArrayList<>();

        for (int i = 0; i < placeSearchResults.getResults().size(); i++){
                String poiName = placeSearchResults.getResults().get(i).getRestaurantName();
                String poiPlaceId = placeSearchResults.getResults().get(i).getPlaceId();
                String poiAddress = placeSearchResults.getResults().get(i).getRestaurantAddress();
                LatLng latLng = new LatLng(placeSearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                                            placeSearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());

                poiList.add(new Poi(
                        poiName,
                        poiPlaceId,
                        poiAddress,
                        latLng));

        }
        return new MapViewViewState(poiList, userLocation);

    }

    // LIVEDATA OBSERVED BY MAPVIEWFRAGMENT
    public LiveData<MapViewViewState> getMapViewStatePoiMutableLiveData() {
        return mapViewStatePoiLiveData;

    }
}