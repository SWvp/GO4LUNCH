package com.kardabel.go4lunch.ui.listview;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.usecase.NearbyResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class ListViewViewModel extends ViewModel {

    private LiveData<RestaurantsWrapperViewState> listViewViewStateLiveData;
    private Location userLocation;
    private NearbyResultsUseCase nearbyResultsUseCase;

 //public ListViewViewModel(@NonNull Application application, @Nullable LocationRepository locationRepository, @Nullable NearbyResponseRepository nearbyResponseRepository) {
 //    super();

 //    // TRANSFORM A LOCATION IN A REQUEST ON WEB WITH NEARBY PLACES
 //    assert locationRepository != null;
 //    LiveData<NearbyResults> nearbyResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), new Function<Location, LiveData<NearbyResults>>() {
 //                @Override
 //                public LiveData<NearbyResults> apply(Location input) {
 //                    userLocation = input;
 //                    String locationAsText = input.getLatitude() + "," + input.getLongitude();
 //                    assert nearbyResponseRepository != null;
 //                    return nearbyResponseRepository.getRestaurantListLiveData("AIzaSyASyYHcFc_BTB-omhZGviy4d3QonaBmcq8", "restaurant", locationAsText, "1000");

 //                }
 //            }
 //    );

    public ListViewViewModel(@Nullable NearbyResultsUseCase nearbyResultsUseCase){

        this.nearbyResultsUseCase = nearbyResultsUseCase;

        // UPDATE LIVEDATA WITH MAP FUNCTION
        //listViewViewStateLiveData = Transformations.map(nearbyResultsLiveData, input -> map(input));
        listViewViewStateLiveData = Transformations.map(nearbyResultsUseCase.getPlaceSearchResultsLiveData(), new Function<PlaceSearchResults, RestaurantsWrapperViewState>() {
            @Override
            public RestaurantsWrapperViewState apply(PlaceSearchResults input) {
                userLocation = nearbyResultsUseCase.getLocationUseCase().getValue();
                nearbyResultsUseCase.getDetails();
                return map(input);

            }
        });
    }

    // CREATE A LIST OF RESTAURANTS ITEMS WITH NEARBY RESULTS
    private RestaurantsWrapperViewState map(PlaceSearchResults placeSearchResults){
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < placeSearchResults.getResults().size(); i++){
            String restaurantName = placeSearchResults.getResults().get(i).getRestaurantName();
            String addressRestaurant = placeSearchResults.getResults().get(i).getRestaurantAddress();
            String photoRestaurant = photoReference(placeSearchResults.getResults().get(i).getRestaurantPhotos());
            String distanceRestaurant = distance(placeSearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(), placeSearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            String openingHoursRestaurant = convertOpeningHours();
            Double ratingRestaurant = convertRatingStars(placeSearchResults.getResults().get(i).getRating());
            String placeIdRestaurant = placeSearchResults.getResults().get(i).getPlaceId();

            restaurantList.add(new RestaurantItemViewState(restaurantName, addressRestaurant, photoRestaurant, distanceRestaurant, openingHoursRestaurant, ratingRestaurant, placeIdRestaurant));

        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    // SEARCH FOR A PHOTO IN THE LIST PROVIDED BY NEARBY PLACES
    private String photoReference(List<Photo> photoList){
        if (photoList != null){
            for (Photo photo : photoList){
                if (!photo.getPhotoReference().isEmpty()){
                    return photo.getPhotoReference();

                }
            }
        }
        return null;

    }

    // CONVERT OPENING HOURS
    private String convertOpeningHours(){
        String noHoursDetails = "Opening hours unavailable !";
        return noHoursDetails;

    }

    // GET LOCATION DISTANCE FROM USER TO RESTAURANT
    private String distance(double lat, double lng){
        if (userLocation != null){
        Location restaurantLocation = new Location("restaurant location");
        restaurantLocation.setLatitude(lat);
        restaurantLocation.setLongitude(lng);
        float distance =userLocation.distanceTo(restaurantLocation);
        return (int) distance + "m";
        } else {
            return "";

        }
    }

    // CONVERT RATING STARS (5 -> 3)
    private double convertRatingStars(double rating) {

        // GIVE AN INTEGER (NUMBER ROUNDED TO THE NEAREST INTEGER)
        long convertedRating = Math.round(rating * 3 / 5);

        return convertedRating;

    }

//  // OPENING HOURS
//  private OpeningHoursPeriod getTodayOpeningHours(RestaurantDetails restaurantDetails) {
//      OpeningHoursPeriod openingHours = null;
//      int currentDay = nearbyResultsUseCase.getCurrentNumericDay();
//      if (restaurantDetails.getOpeningHours().getPeriods() != null) {
//          if (restaurantDetails.getOpeningHours().getPeriods().size() == 6) {
//              openingHours = restaurantDetails.getOpeningHours().getPeriods().get(currentDay);
//          }
//      }
//      return openingHours;
//  }

    private boolean closingSoonStatement(){
        return false;
    }

    private void openRestaurant(){ }

    // LIVEDATA OBSERVED BY LISTVIEWFRAGMENT
    public LiveData<RestaurantsWrapperViewState> getListViewViewStateLiveData() {
        return listViewViewStateLiveData;

    }
}