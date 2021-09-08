package com.kardabel.go4lunch.ui.mapview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kardabel.go4lunch.pojo.RestaurantDetails;
import com.kardabel.go4lunch.pojo.RestaurantList;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.NearbyResponseRepository;

import java.util.ArrayList;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private LiveData<MapViewViewState> mapViewStatePoiLiveData;
    private LocationRepository locationRepository = new LocationRepository();
    private NearbyResponseRepository mNearbyResponseRepository = new NearbyResponseRepository();
    private Location userLocation;

    public MapViewViewModel() {


        // Transform a location in a request on web via nearby places
        LiveData<RestaurantList> restaurantListLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<RestaurantList>>() {
                    @Override
                    public LiveData<RestaurantList> apply(Location input) {
                        userLocation = input;
                        String locationAsText = input.getLatitude() + "," + input.getLongitude();
                        return mNearbyResponseRepository.getRestaurantListLiveData("AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8", "restaurant", locationAsText, "1000");
                    }
                }
        );

        // Transform the restaurant list in a mapViewState (it will expose the mapViewState to the view)
        mapViewStatePoiLiveData = Transformations.map(restaurantListLiveData, new Function<RestaurantList, MapViewViewState>() {
            @Override
            public MapViewViewState apply(RestaurantList input) {

                return map(input);
            }
        });
    }

    public MapViewViewState map(@NonNull RestaurantList restaurantList){
        List<Poi> poiList = new ArrayList<>();

        // MAKE A LIST WITH EACH RESULT
        for (int i = 0; i < restaurantList.getResults().size(); i++){
                String poiName = restaurantList.getResults().get(i).getRestaurantName();
                String poiPlaceId = restaurantList.getResults().get(i).getPlaceId();
                String poiAddress = restaurantList.getResults().get(i).getRestaurantAddress();
                LatLng latLng = new LatLng(restaurantList.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                                            restaurantList.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());

                poiList.add(new Poi(poiName, poiPlaceId, poiAddress, latLng));
        }



        return new MapViewViewState(poiList, userLocation);

    }

    public LiveData<MapViewViewState> getMapViewStatePoiMutableLiveData() {
        return mapViewStatePoiLiveData;

    }

}