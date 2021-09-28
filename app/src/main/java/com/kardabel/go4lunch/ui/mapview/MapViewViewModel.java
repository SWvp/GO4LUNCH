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
import com.kardabel.go4lunch.usecase.NearbyResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class MapViewViewModel extends ViewModel {

    private LiveData<MapViewViewState> mapViewStatePoiLiveData;
    private Location userLocation;

//  public MapViewViewModel(@NonNull Application application, @Nullable LocationRepository locationRepository, @Nullable NearbyResponseRepository nearbyResponseRepository) {
//      super();

//      // TRANSFORM A LOCATION IN A REQUEST ON WEB WITH NEARBY PLACES
//      LiveData<NearbyResults> nearbyResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbyResults>>() {
//                  @Override
//                  public LiveData<NearbyResults> apply(Location input) {
//                      // CREATE NEARBY READABLE LOCATION
//                      userLocation = input;
//                      String locationAsText = input.getLatitude() + "," + input.getLongitude();
//                      return nearbyResponseRepository.getRestaurantListLiveData("AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8", "restaurant", locationAsText, "1000");
//                  }
//              }
//      );

    public MapViewViewModel(@Nullable NearbyResultsUseCase nearbyResultsUseCase){

        mapViewStatePoiLiveData = Transformations.map(nearbyResultsUseCase.getPlaceSearchResultsLiveData(), new Function<PlaceSearchResults, MapViewViewState>() {
            @Override
            public MapViewViewState apply(PlaceSearchResults input) {
                userLocation = nearbyResultsUseCase.getLocationUseCase().getValue();
                return map(input);
            }
        });

//      // UPDATE LIVEDATA WITH MAP FUNCTION
//      mapViewStatePoiLiveData = Transformations.map(nearbyResultsLiveData, new Function<NearbyResults, MapViewViewState>() {
//          @Override
//          public MapViewViewState apply(NearbyResults input) {

//              return map(input);
//          }
//      });
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

                poiList.add(new Poi(poiName, poiPlaceId, poiAddress, latLng));

        }
        return new MapViewViewState(poiList, userLocation);

    }

    // LIVEDATA OBSERVED BY MAPVIEWFRAGMENT
    public LiveData<MapViewViewState> getMapViewStatePoiMutableLiveData() {
        return mapViewStatePoiLiveData;

    }
}