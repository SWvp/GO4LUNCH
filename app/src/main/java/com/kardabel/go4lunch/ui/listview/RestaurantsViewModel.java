package com.kardabel.go4lunch.ui.listview;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.pojo.OpeningHours;
import com.kardabel.go4lunch.pojo.Periods;
import com.kardabel.go4lunch.pojo.Photo;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantSearch;
import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.util.CurrentConvertedHour;
import com.kardabel.go4lunch.util.CurrentNumericDay;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsViewModel extends ViewModel {

    private Location userLocation;

    private final CurrentNumericDay currentNumericDay;
    private final CurrentConvertedHour currentConvertedHour;

    private final MediatorLiveData<RestaurantsWrapperViewState> restaurantsWrapperViewStateMutableLiveData = new MediatorLiveData<>();

    public RestaurantsViewModel(@NonNull LocationRepository locationRepository,
                                @NonNull NearbySearchResultsUseCase nearbySearchResultsUseCase,
                                @NonNull RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase,
                                @NonNull CurrentNumericDay currentNumericDay,
                                @NonNull CurrentConvertedHour currentConvertedHour){

        this.currentNumericDay = currentNumericDay;
        this.currentConvertedHour = currentConvertedHour;

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        LiveData<List<RestaurantDetailsResult>> placeDetailsResultLiveData = restaurantDetailsResultsUseCase.getPlaceDetailsResultLiveData();
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
                         @Nullable List<RestaurantDetailsResult> restaurantDetailsResults,
                         @Nullable Location location) {

        if(location != null){
            this.userLocation = location;

        }

        if(restaurantDetailsResults == null && nearbySearchResults != null){
              restaurantsWrapperViewStateMutableLiveData.setValue(map(nearbySearchResults));

        }else if(restaurantDetailsResults != null && nearbySearchResults != null){
            restaurantsWrapperViewStateMutableLiveData.setValue(mapWithDetails(nearbySearchResults, restaurantDetailsResults));

        }
    }


    // CREATE A LIST OF RESTAURANTS ITEMS WITH NEARBY RESULTS //

    // 1.NEARBY SEARCH DATA (IF DETAILS ARE NOT SUPPORTED ANYMORE)
    private RestaurantsWrapperViewState map(NearbySearchResults nearbySearchResults){
        List<RestaurantsViewState> restaurantList = new ArrayList<>();

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


            restaurantList.add(new RestaurantsViewState(
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
    private RestaurantsWrapperViewState mapWithDetails(NearbySearchResults nearbySearchResults, List<RestaurantDetailsResult> restaurantDetailsResults) {
        List<RestaurantsViewState> restaurantList = new ArrayList<>();
        boolean permanentlyClosed = false;

        if (restaurantDetailsResults != null) {
            for (RestaurantSearch place : nearbySearchResults.getResults()) {
                for (int i = 0; i < restaurantDetailsResults.size(); i++) {
                    if (restaurantDetailsResults.get(i).getDetailsResult().getPlaceId().equals(place.getPlaceId())) {

                        if (place.isPermanentlyClosed()) {
                            permanentlyClosed = true;
                        }

                        String restaurantName = place.getRestaurantName();
                        String addressRestaurant = place.getRestaurantAddress();
                        String photoRestaurant = photoReference(place.getRestaurantPhotos());
                        String distanceRestaurant = distance(
                                place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLat(),
                                place.getRestaurantGeometry().getRestaurantLatLngLiteral().getLng());
                        String openingHoursRestaurant = getOpeningText(restaurantDetailsResults.get(i).getDetailsResult().getOpeningHours(), permanentlyClosed);
                        double ratingRestaurant = convertRatingStars(place.getRating());
                        String placeIdRestaurant = place.getPlaceId();


                        restaurantList.add(new RestaurantsViewState(
                                restaurantName,
                                addressRestaurant,
                                photoRestaurant,
                                distanceRestaurant,
                                openingHoursRestaurant,
                                ratingRestaurant,
                                placeIdRestaurant));
                        break;

                    }
                    permanentlyClosed = false;
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
        return "Photo unavailable";

    }

    // GET MESSAGE FROM OPENING HOURS INFORMATION WITHOUT DETAILS
    private String OpeningHoursLight(OpeningHours openingHours){
        String openStatus;
        if (openingHours != null) {
            if (openingHours.getOpenNow()) {
                openStatus = "Open";

            } else {
                openStatus = "Closed";

            }
        } else {
            openStatus = "Open hours unavailable";

        }
        return openStatus;

    }

    // GET MESSAGE FROM OPENING HOURS INFORMATION
    private String getOpeningText(OpeningHours openingHours, boolean permanentlyClosedStatus) {

        String messageToDisplay = "Opening hours unavailable";

        int firstClosingHour = -1;
        int firstOpenHour = -1;
        int secondOpenHour = 2500;
        int secondClosingHour = -1;

        if (openingHours != null) {

            // IF THE PERIOD LIST HAS ONLY ONE ELEMENT, NOT NULL, CONSIDER THAT THE PLACE IS A 24/7 ONE
            if (openingHours.getPeriods().size() == 1) {
                if (openingHours.getOpenNow()) {
                    messageToDisplay = "Open 24/7";
                }

            } else if (openingHours.getPeriods().size() > 1) {
                if (isRestaurantOpenToday(openingHours)) {

                    // SET ALL OPENING AND CLOSED HOURS
                    for (Periods period : openingHours.getPeriods()) {
                        if (period.getOpen().getDay() == getCurrentDay() && firstOpenHour == -1) {
                            firstOpenHour = Integer.parseInt(period.getOpen().getTime());


                            if (period.getClose().getDay() == getCurrentDay() && firstClosingHour == -1) {
                                firstClosingHour = Integer.parseInt(period.getClose().getTime());


                            } else {
                                firstClosingHour = Integer.parseInt(period.getClose().getTime());
                                firstClosingHour = 5000 + firstClosingHour;


                            }
                        } else if (period.getOpen().getDay() == getCurrentDay() && firstOpenHour != -1) {
                            secondOpenHour = Integer.parseInt(period.getOpen().getTime());


                            if (period.getClose().getDay() == getCurrentDay() && firstClosingHour != -1) {
                                secondClosingHour = Integer.parseInt(period.getClose().getTime());

                            } else {
                                secondClosingHour = Integer.parseInt(period.getClose().getTime());
                                secondClosingHour = 5000 + secondClosingHour;

                            }
                        }
                    }

                    // COMPARE CURRENT HOURS TO OPENING AND CLOSED HOURS SETTINGS PREVIOUSLY

                    // 1.1 open and closed same day
                    if (firstClosingHour < 5000 && secondClosingHour < 5000) {
                        if ((getCurrentHour() >= firstOpenHour && getCurrentHour() < firstClosingHour) ||
                                (getCurrentHour() >= secondOpenHour && getCurrentHour() < secondClosingHour)) {

                            if ((getCurrentHour() >= (firstClosingHour - 100) && getCurrentHour() < firstClosingHour) ||        // 1.2 if the place is about to close
                                    (getCurrentHour() >= (secondClosingHour - 100) && getCurrentHour() < secondClosingHour)) {

                                messageToDisplay = "Closing soon";
                                // TODO send singleLiveData to inform that the color must be red now and only for this case,
                                //  merci bonsoir et arrête de croire que tu vas pouvoir jouer ce soir !!! ou envoyer un arg pour trigger un bool
                            } else {                                                                                            // 1.3 if the place is not about to close
                                if ((getCurrentHour() >= firstOpenHour && getCurrentHour() < firstClosingHour)) {               // 1.3.1 first service
                                    messageToDisplay = "Open until " + getReadableClosingHour(firstClosingHour);

                                } else if ((getCurrentHour() >= secondOpenHour && getCurrentHour() < secondClosingHour)) {      // 1.3.2 if second service available
                                    messageToDisplay = "Open until " + getReadableClosingHour(secondClosingHour);

                                }
                            }
                        } else { messageToDisplay = "Closed"; }

                    } else {                                                                                                      // 1.3 the service is on two days
                        if (getCurrentHour() >= 2300) {
                            messageToDisplay = "Closing soon";

                        } else {
                            // first service
                            if (getCurrentHour() >= firstOpenHour && getCurrentHour() < firstClosingHour) {
                                firstClosingHour = firstClosingHour - 5000;
                                messageToDisplay = "Open until " + getReadableClosingHour(firstClosingHour);

                            }
                            // second service
                            else if ((getCurrentHour() >= secondOpenHour && getCurrentHour() < secondClosingHour)) {
                                secondClosingHour = secondClosingHour - 5000;
                                messageToDisplay = "Open until " + getReadableClosingHour(secondClosingHour);

                            } else { messageToDisplay = "Closed"; }
                        }
                    }
                }
            }

            // IF THE PERIOD LIST IS EMPTY OR NULL, RETRIEVE ONLY OPEN STATUS

            else if (!openingHours.getOpenNow()) {
                messageToDisplay = "Closed";


            } else if (openingHours.getOpenNow()) {
                messageToDisplay = "Open";

            }
        }

        if(permanentlyClosedStatus){
            messageToDisplay = "Permanently closed";
        }
        return messageToDisplay;

    }

    // CHECK IF RESTAURANT IS CLOSED TODAY UNTIL TOMORROW
    private boolean isRestaurantOpenToday(OpeningHours openingHours) {
        boolean bool = true;
        for (int i = 0; i < openingHours.getPeriods().size(); i++) {
            if (openingHours.getPeriods().get(i).getOpen().getDay() ==
                    getCurrentDay() && openingHours.getPeriods().get(i).getOpen().getTime() == null) {
                bool = false;

            }
        }
        return bool;

    }

    // CONVERT CLOSE HOUR TO READABLE UK HOUR
    private String getReadableClosingHour(int ClosingHour) {
        int hour;
        int min;

        String minReadable;
        String readableHour;

        if(ClosingHour == 0){
            hour = 0;
        }else{
            hour = ClosingHour/100;
        }

        min = ClosingHour - ( hour * 100 );

        // CONVERT MINUTES IF NEEDED
        if(min == 0){
            minReadable = "";
        }else if(min > 0 && min < 10){
            minReadable = "0" + min;
        }else{
            minReadable = String.valueOf(min);
        }

        // GET READABLE HOUR
        if(hour < 12 && hour > 0){
            readableHour = hour + "." + minReadable + "am";
        }else if(hour == 0){
            readableHour = "12" + minReadable + "am";
        }else if(hour == 12){
            readableHour = "12" + "." + minReadable + "pm";
        }else{
            readableHour = (hour -12) + "." + minReadable + "am";
        }
        return readableHour;
    }

    private int getCurrentDay(){ return currentNumericDay.getCurrentNumericDay(); }

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
    public LiveData<RestaurantsWrapperViewState> getRestaurantsViewStateLiveData() {
        return restaurantsWrapperViewStateMutableLiveData;

    }
}