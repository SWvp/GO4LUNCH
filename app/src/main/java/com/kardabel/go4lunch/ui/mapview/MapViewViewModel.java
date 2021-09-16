package com.kardabel.go4lunch.ui.mapview;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.pojo.NearbyResults;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbyResponseRepository;

import java.util.ArrayList;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private LiveData<MapViewViewState> mapViewStatePoiLiveData;
    private Location userLocation;

    public MapViewViewModel(@NonNull Application application, @Nullable LocationRepository locationRepository, @Nullable NearbyResponseRepository nearbyResponseRepository) {
        super();

        // TRANSFORM A LOCATION IN A REQUEST ON WEB WITH NEARBY PLACES
        LiveData<NearbyResults> nearbyResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbyResults>>() {
                    @Override
                    public LiveData<NearbyResults> apply(Location input) {
                        // CREATE NEARBY READABLE LOCATION
                        userLocation = input;
                        String locationAsText = input.getLatitude() + "," + input.getLongitude();
                        return nearbyResponseRepository.getRestaurantListLiveData("AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8", "restaurant", locationAsText, "1000");
                    }
                }
        );

        // UPDATE LIVEDATA WITH MAP FUNCTION
        mapViewStatePoiLiveData = Transformations.map(nearbyResultsLiveData, new Function<NearbyResults, MapViewViewState>() {
            @Override
            public MapViewViewState apply(NearbyResults input) {

                return map(input);
            }
        });
    }
    // MAKE A LIST OF POI INFORMATION WITH EACH RESULT
    public MapViewViewState map(@NonNull NearbyResults nearbyResults){
        List<Poi> poiList = new ArrayList<>();

        for (int i = 0; i < nearbyResults.getResults().size(); i++){
                String poiName = nearbyResults.getResults().get(i).getRestaurantName();
                String poiPlaceId = nearbyResults.getResults().get(i).getPlaceId();
                String poiAddress = nearbyResults.getResults().get(i).getRestaurantAddress();
                LatLng latLng = new LatLng(nearbyResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                                            nearbyResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());

                poiList.add(new Poi(poiName, poiPlaceId, poiAddress, latLng));

        }
        return new MapViewViewState(poiList, userLocation);

    }

    // LIVEDATA OBSERVED BY MAPVIEWFRAGMENT
    public LiveData<MapViewViewState> getMapViewStatePoiMutableLiveData() {
        return mapViewStatePoiLiveData;

    }
}