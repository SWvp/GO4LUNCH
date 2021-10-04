package com.kardabel.go4lunch.ui.listview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.PlaceDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class ListViewViewModel extends ViewModel {

    private MediatorLiveData<RestaurantsWrapperViewState> restaurantsWrapperViewStateMutableLiveData = new MediatorLiveData<>();
    private Location userLocation;
    private NearbySearchResultsUseCase mNearbySearchResultsUseCase;
    private PlaceDetailsResultsUseCase mPlaceDetailsResultsUseCase;

    public ListViewViewModel(@NonNull LocationRepository locationRepository, @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase, @NonNull PlaceDetailsResultsUseCase placeDetailsResultsUseCase){

        this.mNearbySearchResultsUseCase = nearbySearchResultsUseCase;
        this.mPlaceDetailsResultsUseCase = placeDetailsResultsUseCase;

        LiveData<List<PlaceDetailsResult>> placeDetailsResult = mPlaceDetailsResultsUseCase.getPlaceDetailsResultLiveData();
        LiveData<NearbySearchResults> placeSearchResultsLiveData = mNearbySearchResultsUseCase.getNearbySearchResultsLiveData();

        // OBSERVE USE CASES

        // TODO: observe Location repo to retrieve position needed for distance calculation

        restaurantsWrapperViewStateMutableLiveData.addSource(placeSearchResultsLiveData, new Observer<NearbySearchResults>() {
            @Override
            public void onChanged(NearbySearchResults nearbySearchResults) {
                combine(nearbySearchResults, placeDetailsResult.getValue());

            }
        });

        restaurantsWrapperViewStateMutableLiveData.addSource(placeDetailsResult, new Observer<List<PlaceDetailsResult>>() {
            @Override
            public void onChanged(List<PlaceDetailsResult> placeDetailsResults) {
                combine(placeSearchResultsLiveData.getValue(), placeDetailsResults);

            }
        });
    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable List<PlaceDetailsResult> placeDetailsResults) {

        // NEED TO RESOLVE REPO ISSUES
        placeDetailsResults = null;

        if(placeDetailsResults == null && nearbySearchResults != null){
              restaurantsWrapperViewStateMutableLiveData.setValue(map(nearbySearchResults));

        }else if (placeDetailsResults != null && nearbySearchResults != null){
            restaurantsWrapperViewStateMutableLiveData.setValue(mapWithDetails(nearbySearchResults, placeDetailsResults));

        }
    }


    // CREATE A LIST OF RESTAURANTS ITEMS WITH NEARBY RESULTS //

    // 1.NEARBY SEARCH DATA
    private RestaurantsWrapperViewState map(NearbySearchResults nearbySearchResults){
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++){
            String restaurantName = nearbySearchResults.getResults().get(i).getRestaurantName();
            String addressRestaurant = nearbySearchResults.getResults().get(i).getRestaurantAddress();
            String photoRestaurant = photoReference(nearbySearchResults.getResults().get(i).getRestaurantPhotos());
            String distanceRestaurant = distance(nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(), nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            String openingHoursRestaurant = OpeningHoursUnavailable();
            Double ratingRestaurant = convertRatingStars(nearbySearchResults.getResults().get(i).getRating());
            String placeIdRestaurant = nearbySearchResults.getResults().get(i).getPlaceId();

            restaurantList.add(new RestaurantItemViewState(restaurantName, addressRestaurant, photoRestaurant, distanceRestaurant, openingHoursRestaurant, ratingRestaurant, placeIdRestaurant));

        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    // 2.PLACE DETAILS DATA
    private RestaurantsWrapperViewState mapWithDetails(NearbySearchResults nearbySearchResults, List<PlaceDetailsResult> placeDetailsResults) {
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        for (RestaurantSearch place : nearbySearchResults.getResults()) {
            for (int i = 0; i < placeDetailsResults.size(); i++) {
                if (placeDetailsResults.get(i).getDetailsResult().getPlaceId().equals(place.getPlaceId())) {
                    String restaurantName = place.getRestaurantName();
                    String addressRestaurant = place.getRestaurantAddress();
                    String photoRestaurant = photoReference(place.getRestaurantPhotos());
                    String distanceRestaurant = distance(place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(), place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
                    String openingHoursRestaurant = getOpeningText();
                    Double ratingRestaurant = convertRatingStars(place.getRating());
                    String placeIdRestaurant = place.getPlaceId();

                    restaurantList.add(new RestaurantItemViewState(restaurantName, addressRestaurant, photoRestaurant, distanceRestaurant, openingHoursRestaurant, ratingRestaurant, placeIdRestaurant));
                    break;

                }
            }
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
    private String OpeningHoursUnavailable(){
        String noHoursDetails = "Opening hours unavailable";
        return noHoursDetails;

    }

    private String getOpeningText() {
        String alpha = "alpha";
        return alpha;


    }

    // GET LOCATION DISTANCE FROM USER TO RESTAURANT
    private String distance(double lat, double lng){
        if (userLocation != null){
        Location restaurantLocation = new Location("restaurant location");
        restaurantLocation.setLatitude(lat);
        restaurantLocation.setLongitude(lng);
        float distance = userLocation.distanceTo(restaurantLocation);
        return (int) distance + "m";
        } else {
            return "distance unavailable";

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
        return restaurantsWrapperViewStateMutableLiveData;

    }
}