package com.kardabel.go4lunch.ui.listview;

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

import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.PlaceSearchResults;
import com.kardabel.go4lunch.pojo.RestaurantDetails;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.PlaceDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.PlaceSearchResultsUseCase;

import java.util.ArrayList;
import java.util.List;

public class ListViewViewModel extends ViewModel {

    private LiveData<RestaurantsWrapperViewState> listViewViewStateLiveData;
    private MediatorLiveData<RestaurantsWrapperViewState> restaurantsWrapperViewStateMutableLiveData = new MediatorLiveData<>();
    private Location userLocation;
    private PlaceSearchResultsUseCase mPlaceSearchResultsUseCase;
    private PlaceDetailsResultsUseCase mPlaceDetailsResultsUseCase;

    public ListViewViewModel(@NonNull LocationRepository locationRepository, @NonNull PlaceSearchResultsUseCase placeSearchResultsUseCase, @NonNull PlaceDetailsResultsUseCase placeDetailsResultsUseCase){

        this.mPlaceSearchResultsUseCase = placeSearchResultsUseCase;
        this.mPlaceDetailsResultsUseCase = placeDetailsResultsUseCase;




 //     // UPDATE LIVEDATA WITH MAP FUNCTION
 //     listViewViewStateLiveData = Transformations.map(placeSearchResultsUseCase.getPlaceSearchResultsLiveData(), new Function<PlaceSearchResults, RestaurantsWrapperViewState>() {
 //         @Override
 //         public RestaurantsWrapperViewState apply(PlaceSearchResults input) {
 //             userLocation = locationRepository.getLocationLiveData().getValue();

 //             return map(input);

 //         }
 //     });

        LiveData<List<PlaceDetailsResult>> placeDetailsResult = mPlaceDetailsResultsUseCase.getPlaceDetailsResultLiveData();
        LiveData<PlaceSearchResults> placeSearchResultsLiveData = mPlaceSearchResultsUseCase.getPlaceSearchResultsLiveData();



        restaurantsWrapperViewStateMutableLiveData.addSource(placeSearchResultsLiveData, new Observer<PlaceSearchResults>() {
            @Override
            public void onChanged(PlaceSearchResults placeSearchResults) {
                combine(placeSearchResults, placeDetailsResult.getValue());

            }
        });

        restaurantsWrapperViewStateMutableLiveData.addSource(placeDetailsResult, new Observer<List<PlaceDetailsResult>>() {
            @Override
            public void onChanged(List<PlaceDetailsResult> placeDetailsResults) {
                combine(placeSearchResultsLiveData.getValue(), placeDetailsResults);

            }
        });






    }

    private void combine(@Nullable PlaceSearchResults placeSearchResults,
                         @Nullable List<PlaceDetailsResult> placeDetailsResults) {

        if(placeDetailsResults == null && placeSearchResults != null){
              restaurantsWrapperViewStateMutableLiveData.setValue(map(placeSearchResults));

        }else if (placeDetailsResults != null && placeSearchResults != null){
            restaurantsWrapperViewStateMutableLiveData.setValue(mapWithDetails(placeSearchResults, placeDetailsResults));


        }


    }

    private RestaurantsWrapperViewState mapWithDetails(PlaceSearchResults placeSearchResults, List<PlaceDetailsResult> placeDetailsResults) {
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        for (RestaurantSearch place : placeSearchResults.getResults()) {
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


    // CREATE A LIST OF RESTAURANTS ITEMS WITH NEARBY RESULTS
    private RestaurantsWrapperViewState map(PlaceSearchResults placeSearchResults){
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < placeSearchResults.getResults().size(); i++){
            String restaurantName = placeSearchResults.getResults().get(i).getRestaurantName();
            String addressRestaurant = placeSearchResults.getResults().get(i).getRestaurantAddress();
            String photoRestaurant = photoReference(placeSearchResults.getResults().get(i).getRestaurantPhotos());
            String distanceRestaurant = distance(placeSearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(), placeSearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            String openingHoursRestaurant = OpeningHoursUnavailable();
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
    private String OpeningHoursUnavailable(){
        String noHoursDetails = "Opening hours unavailable !";
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
        return restaurantsWrapperViewStateMutableLiveData;

    }
}