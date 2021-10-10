package com.kardabel.go4lunch.ui.listview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.PlaceDetailsResult;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.PlaceDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.util.CurrentConvertedHour;
import com.kardabel.go4lunch.util.CurrentNumericDay;

import java.util.ArrayList;
import java.util.List;

public class ListViewViewModel extends ViewModel {

    private Location userLocation;

    private final CurrentNumericDay currentNumericDay;
    private final CurrentConvertedHour currentConvertedHour;

    private final MediatorLiveData<RestaurantsWrapperViewState> restaurantsWrapperViewStateMutableLiveData = new MediatorLiveData<>();

    public ListViewViewModel(@NonNull LocationRepository locationRepository,
                             @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase,
                             @NonNull PlaceDetailsResultsUseCase placeDetailsResultsUseCase,
                             @NonNull CurrentNumericDay currentNumericDay,
                             @NonNull CurrentConvertedHour currentConvertedHour){

        this.currentNumericDay =currentNumericDay;
        this.currentConvertedHour = currentConvertedHour;

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<List<PlaceDetailsResult>> placeDetailsResultLiveData = placeDetailsResultsUseCase.getPlaceDetailsResultLiveData();
        LiveData<NearbySearchResults> placeSearchResultsLiveData = nearbySearchResultsUseCase.getNearbySearchResultsLiveData();

        // OBSERVERS
        restaurantsWrapperViewStateMutableLiveData.addSource(locationLiveData, location ->
                combine(placeSearchResultsLiveData.getValue(), placeDetailsResultLiveData.getValue(), location));

        restaurantsWrapperViewStateMutableLiveData.addSource(placeSearchResultsLiveData, nearbySearchResults ->
                combine(nearbySearchResults, placeDetailsResultLiveData.getValue(), locationLiveData.getValue()));

        restaurantsWrapperViewStateMutableLiveData.addSource(placeDetailsResultLiveData, placeDetailsResults ->
                combine(placeSearchResultsLiveData.getValue(), placeDetailsResults, locationLiveData.getValue()));
    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable List<PlaceDetailsResult> placeDetailsResults,
                         @Nullable Location location) {

        if(location != null){
            this.userLocation = location;

        }

        if(placeDetailsResults == null && nearbySearchResults != null){
              restaurantsWrapperViewStateMutableLiveData.setValue(map(nearbySearchResults));

        }else if(placeDetailsResults != null && nearbySearchResults != null){
            restaurantsWrapperViewStateMutableLiveData.setValue(mapWithDetails(nearbySearchResults, placeDetailsResults));

        }
    }


    // CREATE A LIST OF RESTAURANTS ITEMS WITH NEARBY RESULTS //

    // 1.NEARBY SEARCH DATA (IF DETAILS ARE NOT SUPPORTED ANYMORE)
    private RestaurantsWrapperViewState map(NearbySearchResults nearbySearchResults){
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        for (int i = 0; i < nearbySearchResults.getResults().size(); i++){
            String restaurantName = nearbySearchResults.getResults().get(i).getRestaurantName();
            String addressRestaurant = nearbySearchResults.getResults().get(i).getRestaurantAddress();
            String photoRestaurant = photoReference(nearbySearchResults.getResults().get(i).getRestaurantPhotos());
            String distanceRestaurant = distance(
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                    nearbySearchResults.getResults().get(i).getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
            String openingHoursRestaurant = OpeningHoursLight(nearbySearchResults.getResults().get(i).getOpeningHours());
            double ratingRestaurant = convertRatingStars(nearbySearchResults.getResults().get(i).getRating());
            String placeIdRestaurant = nearbySearchResults.getResults().get(i).getPlaceId();


            restaurantList.add(new RestaurantItemViewState(
                    restaurantName,
                    addressRestaurant,
                    photoRestaurant,
                    distanceRestaurant,
                    openingHoursRestaurant,
                    ratingRestaurant,
                    placeIdRestaurant));

        }
        return new RestaurantsWrapperViewState(restaurantList);

    }

    // 2.PLACE DETAILS DATA
    private RestaurantsWrapperViewState mapWithDetails(NearbySearchResults nearbySearchResults, List<PlaceDetailsResult> placeDetailsResults) {
        List<RestaurantItemViewState> restaurantList = new ArrayList<>();

        if(placeDetailsResults != null){
        for (RestaurantSearch place : nearbySearchResults.getResults()) {
            for (int i = 0; i < placeDetailsResults.size(); i++) {
                if (placeDetailsResults.get(i).getDetailsResult().getPlaceId().equals(place.getPlaceId())) {
                    String restaurantName = place.getRestaurantName();
                    String addressRestaurant = place.getRestaurantAddress();
                    String photoRestaurant = photoReference(place.getRestaurantPhotos());
                    String distanceRestaurant = distance(
                            place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                            place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
                    String openingHoursRestaurant = getOpeningText(placeDetailsResults.get(i).getDetailsResult().getOpeningHours());
                    double ratingRestaurant = convertRatingStars(place.getRating());
                    String placeIdRestaurant = place.getPlaceId();


                    restaurantList.add(new RestaurantItemViewState(restaurantName, addressRestaurant, photoRestaurant, distanceRestaurant, openingHoursRestaurant, ratingRestaurant, placeIdRestaurant));
                    break;

                }
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

    // GET MESSAGE FROM OPENING HOURS INFORMATION WITHOUT DETAILS
    private String OpeningHoursLight(OpeningHours openingHours){
        String openStatus;
        if (openingHours != null) {
            if (openingHours.getOpenNow()) {
                openStatus = "open";

            } else {
                openStatus = "closed";

            }
        } else {
            openStatus = "open hours unavailable";

        }
        return openStatus;

    }

    // GET MESSAGE FROM OPENING HOURS INFORMATION
    private String getOpeningText(OpeningHours openingHours) {

        String messageToDisplay = "Opening hours unavailable";
        int hourSolo;
        int minSolo;
        String minSoloString;

        if (openingHours != null) {
            if (openingHours.getPeriods().get(getCurrentDay()).getClose() != null) {

                int closeHour = Integer.parseInt(openingHours.getPeriods().get(getCurrentDay()).getClose().getTime());
                int openHour = Integer.parseInt(openingHours.getPeriods().get(getCurrentDay()).getOpen().getTime());

                if (closeHour > getCurrentHour() && openHour <= getCurrentHour()) {
                    messageToDisplay = "Open";

                } else if (getCurrentHour() < openHour) {
                    hourSolo = openHour / 100;
                    minSolo = openHour - hourSolo * 100;
                    if (minSolo < 10) {
                        minSoloString = "0" + minSolo;
                    } else {
                        minSoloString = String.valueOf(minSolo);
                    }
                    messageToDisplay = "Closed until" + " " + hourSolo + "h" + minSoloString;

                } else if (closeHour < getCurrentHour()) {
                    hourSolo = openHour / 100;
                    minSolo = openHour - hourSolo * 100;
                    if (minSolo < 10) {
                        minSoloString = "0" + minSolo;
                    } else {
                        minSoloString = String.valueOf(minSolo);
                    }
                    messageToDisplay = "Closed until" + " " + hourSolo + "h" + minSoloString + " Tomorrow";
                } else {
                    hourSolo = closeHour / 100;
                    minSolo = closeHour - hourSolo * 100;
                    messageToDisplay = "Open until" + " " + hourSolo + "h" + minSolo;

                }
            } else if (openingHours.getOpenNow()) {
                messageToDisplay = "Open (opening hours unavailable)";

            } else if (!openingHours.getOpenNow()) {
                messageToDisplay = "Closed (opening hours unavailable)";

            }
        }
        return messageToDisplay;

    }

    private int getCurrentDay(){
        return currentNumericDay.getCurrentNumericDay();

    }

    private int getCurrentHour(){
        return currentConvertedHour.getCurrentConvertedHour();
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
        return Math.round(rating * 3 / 5);

    }

    // LIVEDATA OBSERVED BY LIST VIEW FRAGMENT
    public LiveData<RestaurantsWrapperViewState> getListViewViewStateLiveData() {
        return restaurantsWrapperViewStateMutableLiveData;

    }
}